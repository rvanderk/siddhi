/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.siddhi.core.query.pattern;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.wso2.siddhi.core.ExecutionPlanRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.query.output.callback.QueryCallback;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.util.EventPrinter;


public class WithinPatternTestCase {

    private static final Logger log = Logger.getLogger(WithinPatternTestCase.class);
    private int inEventCount;
    private int removeEventCount;
    private boolean eventArrived;

    @Before
    public void init() {
        inEventCount = 0;
        removeEventCount = 0;
        eventArrived = false;
    }

    @Test
    public void testQuery1() throws InterruptedException {
        log.info("testPatternWithin1 - OUT 1");

        SiddhiManager siddhiManager = new SiddhiManager();

        String streams = "" +
                "define stream Stream1 (symbol string, price float, volume int); " +
                "define stream Stream2 (symbol string, price float, volume int); ";
        String query = "" +
                "@info(name = 'query1') " +
                "from every e1=Stream1[price>20] -> e2=Stream2[price>e1.price] within 1 sec " +
                "select e1.symbol as symbol1, e2.symbol as symbol2 " +
                "insert into OutputStream ;";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(streams + query);

        executionPlanRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                if (inEvents != null) {
                    inEventCount = inEventCount + inEvents.length;
                    Assert.assertArrayEquals(new Object[]{"GOOG", "IBM"}, inEvents[0].getData());
                    eventArrived = true;
                }
                if (removeEvents != null) {
                    removeEventCount = removeEventCount + removeEvents.length;
                }
                eventArrived = true;
            }

        });

        InputHandler stream1 = executionPlanRuntime.getInputHandler("Stream1");
        InputHandler stream2 = executionPlanRuntime.getInputHandler("Stream2");

        executionPlanRuntime.start();

        stream1.send(new Object[]{"WSO2", 55.6f, 100});
        Thread.sleep(1500);
        stream1.send(new Object[]{"GOOG", 54f, 100});
        Thread.sleep(500);
        stream2.send(new Object[]{"IBM", 55.7f, 100});
        Thread.sleep(500);

        Assert.assertEquals("Number of success events", 1, inEventCount);
        Assert.assertEquals("Number of remove events", 0, removeEventCount);
        Assert.assertEquals("Event arrived", true, eventArrived);

        executionPlanRuntime.shutdown();
    }

    @Test
    public void testQuery2() throws InterruptedException {
        log.info("testPatternWithin2 - OUT 1");

        SiddhiManager siddhiManager = new SiddhiManager();

        String streams = "" +
                "define stream Stream1 (symbol string, price float, volume int); " +
                "define stream Stream2 (symbol string, price float, volume int); ";
        String query = "" +
                "@info(name = 'query1') " +
                "from (every e1=Stream1[price>20]-> e2=Stream2[price>e1.price]) " +
                "within 1 sec " +
                "select e1.symbol as symbol1, e2.symbol as symbol2 " +
                "insert into OutputStream ;";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(streams + query);

        executionPlanRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                if (inEvents != null) {
                    inEventCount = inEventCount + inEvents.length;
                    Assert.assertArrayEquals(new Object[]{"GOOG", "IBM"}, inEvents[0].getData());
                    eventArrived = true;
                }
                if (removeEvents != null) {
                    removeEventCount = removeEventCount + removeEvents.length;
                }
                eventArrived = true;
            }

        });

        InputHandler stream1 = executionPlanRuntime.getInputHandler("Stream1");
        InputHandler stream2 = executionPlanRuntime.getInputHandler("Stream2");

        executionPlanRuntime.start();

        stream1.send(new Object[]{"WSO2", 55.6f, 100});
        Thread.sleep(1500);
        stream1.send(new Object[]{"GOOG", 54f, 100});
        Thread.sleep(500);
        stream2.send(new Object[]{"IBM", 55.7f, 100});
        Thread.sleep(500);

        Assert.assertEquals("Number of success events", 1, inEventCount);
        Assert.assertEquals("Number of remove events", 0, removeEventCount);
        Assert.assertEquals("Event arrived", true, eventArrived);

        executionPlanRuntime.shutdown();
    }

    @Test
    public void testQuery3() throws InterruptedException {
        log.info("testPatternWithin3 - OUT 1");

        SiddhiManager siddhiManager = new SiddhiManager();

        String streams = "" +
                "define stream Stream1 (symbol string, price float, volume int); " +
                "define stream Stream2 (symbol string, price float, volume int); ";
        String query = "" +
                "@info(name = 'query1') " +
                "from (every (e1=Stream1[price>20] -> e3=Stream1[price>20]) -> e2=Stream2[price>e1.price]) within 2 sec " +
                "select e1.price as price1, e3.price as price3, e2.price as price2 " +
                "insert into OutputStream ;";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(streams + query);

        executionPlanRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                if (inEvents != null) {
                    inEventCount = inEventCount + inEvents.length;
                    Assert.assertArrayEquals(new Object[]{53.6f, 53f, 57.7f},inEvents[0].getData());
                    eventArrived = true;
                }
                if (removeEvents != null) {
                    removeEventCount = removeEventCount + removeEvents.length;
                }
                eventArrived = true;
            }

        });

        InputHandler stream1 = executionPlanRuntime.getInputHandler("Stream1");
        InputHandler stream2 = executionPlanRuntime.getInputHandler("Stream2");

        executionPlanRuntime.start();

        stream1.send(new Object[]{"WSO2", 55.6f, 100});
        Thread.sleep(600);
        stream1.send(new Object[]{"GOOG", 54f, 100});
        Thread.sleep(600);
        stream1.send(new Object[]{"WSO2", 53.6f, 100});
        Thread.sleep(600);
        stream1.send(new Object[]{"GOOG", 53f, 100});
        Thread.sleep(600);
        stream2.send(new Object[]{"IBM", 57.7f, 100});
        Thread.sleep(600);

        Assert.assertEquals("Number of success events", 1, inEventCount);
        Assert.assertEquals("Number of remove events", 0, removeEventCount);
        Assert.assertEquals("Event arrived", true, eventArrived);

        executionPlanRuntime.shutdown();
    }



}
