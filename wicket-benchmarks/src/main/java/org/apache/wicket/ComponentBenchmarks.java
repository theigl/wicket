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
package org.apache.wicket;

import java.io.IOException;
import java.util.stream.IntStream;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.mock.MockApplication;
import org.apache.wicket.util.tester.WicketTester;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

@State(Scope.Thread)
public class ComponentBenchmarks
{

	private static final String ID = "id";

	private static final WicketTester tester = new WicketTester(new MockApplication());

	@Param({ "500" })
	private int numChildren;

	private MarkupContainer c;

	@Setup
	public void setup()
	{
		c = new WebMarkupContainer(ID);
		IntStream.range(0, numChildren)
			.forEach(i -> c.add(new WebMarkupContainer(String.valueOf(i))));
	}

	@Benchmark
	public void forLoop()
	{
		for (Component child : c)
		{
			child.setMarkup(null);
		}
	}

	@Benchmark
	public void forEach()
	{
		c.forEach((child, o) -> child.setMarkup(null));
	}

	@Benchmark
	public void detach()
	{
		c.detach();
	}

	@Benchmark
	public void internalInitialize()
	{
		c.internalInitialize();
	}

	@Benchmark
	public void visibleStateChange()
	{
		c.onVisibleStateChanged();
	}

	@Benchmark
	public void visitChildren()
	{
		c.visitChildren(new DummyVisitor());
	}

	public static void main(String[] args) throws RunnerException, IOException
	{
		if (args.length == 0)
		{
			final Options opt = new OptionsBuilder()
				.include(".*" + ComponentBenchmarks.class.getSimpleName() + ".*")
				.warmupIterations(3)
				.warmupTime(TimeValue.seconds(3))
				.measurementIterations(3)
				.measurementTime(TimeValue.seconds(3))
				.threads(1)
				// .addProfiler("gc")
				.forks(2)
				.build();
			new Runner(opt).run();
		}
		else
		{
			Main.main(args);
		}
	}

	private static class DummyVisitor implements IVisitor<Component, Object>
	{
		@Override
		public void component(Component object, IVisit<Object> visit)
		{

		}
	}

}
