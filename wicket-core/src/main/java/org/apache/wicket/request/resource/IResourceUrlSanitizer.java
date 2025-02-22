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
package org.apache.wicket.request.resource;

import org.apache.wicket.request.resource.ResourceReference.UrlAttributes;

/**
 * Sanitizes the resource URL parameters. Implementations should be concerned to return a set of
 * style/locale/variation that is safe to end up as keys in the server resource cache, without
 * causing unnecessary values to be kept in the primary memory.
 * 
 * @author Pedro Santos
 */
public interface IResourceUrlSanitizer
{

	/**
	 * Sanitizes the {@link UrlAttributes} and returns a new instance with style/locale/variation
	 * values that are safe to be kept in-memory.
	 * 
	 * @param urlAttributes
	 * @param scope
	 * @param name
	 * @return null if there are no resource matching the scope/name
	 */
	public UrlAttributes sanitize(UrlAttributes urlAttributes, Class<?> scope, String name);

}
