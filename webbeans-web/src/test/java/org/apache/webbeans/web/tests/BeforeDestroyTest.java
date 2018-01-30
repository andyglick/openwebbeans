/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.webbeans.web.tests;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.BeforeDestroyed;
import javax.enterprise.event.Observes;

import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.spi.ContextsService;
import org.apache.webbeans.test.AbstractUnitTest;
import org.apache.webbeans.web.context.WebContextsService;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test whether @BeforeDestroy(ApplicationScoped.class) really works.
 * See OWB-1223
 */
public class BeforeDestroyTest extends AbstractUnitTest
{
    @Test
    public void testBeforeDestroy()
    {
        startContainer(ShutMeDownProperly.class);

        getInstance(ShutMeDownProperly.class).ping();

        ContextsService contextsService = WebBeansContext.getInstance().getContextsService();
        Assert.assertTrue(contextsService instanceof WebContextsService);

        ShutMeDownProperly.properShutDown = false;

        shutDownContainer();

        Assert.assertTrue(ShutMeDownProperly.properShutDown);
    }


    @ApplicationScoped
    public static class ShutMeDownProperly
    {
        public static boolean properShutDown = false;

        public void ping() {
            // do nothing, just to start up the bean
        }

        public void shutdown(@Observes @BeforeDestroyed(ApplicationScoped.class) Object val)
        {
            properShutDown = true;
        }
    }
}
