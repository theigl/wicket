/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.cdi;

import org.jboss.weld.bean.builtin.BeanManagerProxy;
import org.jboss.weld.module.web.servlet.HttpContextLifecycle;
import org.jboss.weld.servlet.spi.helpers.AcceptingHttpContextActivationFilter;

import io.github.cdiunit.internal.servlet.CdiUnitInitialListenerImpl;
import io.github.cdiunit.internal.servlet.LifecycleAwareRequest;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * @author jsarman
 */
@ApplicationScoped
public class ContextManager
{
	private HttpServletRequest currentRequest;

	@Inject
	private BeanManager beanManager;

	private HttpContextLifecycle lifecycle;

	private HttpSession currentSession;

	@PostConstruct
	public void setup()
	{
		try
		{
			lifecycle = new HttpContextLifecycle(BeanManagerProxy.unwrap(beanManager),
					AcceptingHttpContextActivationFilter.INSTANCE, true, true, false, true);
		}
		catch (NoSuchMethodError e)
		{
			try
			{
				lifecycle = HttpContextLifecycle.class.getConstructor(BeanManager.class,
						AcceptingHttpContextActivationFilter.class).newInstance(
						BeanManagerProxy.unwrap(beanManager),
						AcceptingHttpContextActivationFilter.INSTANCE);
			}
			catch (Exception e1)
			{
				throw new RuntimeException(e1);
			}
		}
		lifecycle.setConversationActivationEnabled(true);
	}

	public void activateContexts(HttpServletRequest request)
	{
		if (currentRequest != null)
			return;

		currentRequest = new LifecycleAwareRequest(new CdiUnitInitialListenerImpl(), request);
		lifecycle.requestInitialized(currentRequest, null);
	}

	public void deactivateContexts()
	{
		lifecycle.requestDestroyed(currentRequest);
		currentSession = currentRequest.getSession(false);
		currentRequest = null;
	}

	public void destroy()
	{
		if (currentRequest != null)
		{
			currentSession = currentRequest.getSession(false);
		}

		if (currentSession != null)
		{
			lifecycle.sessionDestroyed(currentSession);
			currentSession = null;
		}
	}

	public boolean isRequestActive()
	{
		return currentRequest != null;
	}
}
