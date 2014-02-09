package com.ctrip.freeway.tracing.impl;

import com.ctrip.freeway.gen.v2.*;
import com.ctrip.freeway.tracing.ISpan;
import com.ctrip.freeway.tracing.ITrace;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TracerTest {

	private ITrace tracer;
	private ITraceSender sender;
	
	@Before
	public void initCase() {
		tracer = new CommonTracer(TracerTest.class.getName());
		tracer.clear();
		sender = new CountSender();
		((CommonTracer)tracer).setSender(sender);
		
		assertFalse(tracer.isTracing());
	}
	
	@Test
	public void testTracer() {
        ISpan tSpan = tracer.startSpan("testRootSpan", "testUrlService", SpanType.URL);
        assertTrue(tracer.isTracing());
        
        ISpan currentSpan = tracer.getCurrentSpan();
        assertNotNull(currentSpan);
        assertTrue(currentSpan instanceof RootMilliSpan);
        
        assertNull(currentSpan.getParent());
        assertEquals(0, currentSpan.getParentId());
        assertEquals("[testUrlService : testRootSpan]", currentSpan.getDescription());
        assertTrue(currentSpan.getLogEvents().size() == 1);
        LogEvent logEvent = currentSpan.getLogEvents().get(0);
        assertEquals(LogType.URL, logEvent.getLogType());
        assertEquals(SpanType.URL + " Trace Span Start", logEvent.getTitle());
        assertEquals("[testUrlService : testRootSpan]", logEvent.getMessage());
        assertEquals(LogLevel.DEBUG, logEvent.getLogLevel());
        assertEquals(TracerTest.class.getName(), logEvent.getSource());
        assertTrue(currentSpan.isRunning());
        
        tSpan.stop();
        currentSpan = tracer.getCurrentSpan();
        assertNull(currentSpan);
        
        CountSender cSender = (CountSender)sender;
        assertEquals(1, cSender.getSpanCount());
        assertEquals(0, cSender.getLogEventCount());
        Span span = cSender.getSpans().get(0);
        assertEquals(2, span.getLogEventsSize());
        logEvent = span.getLogEvents().get(1);
        assertEquals(LogType.URL, logEvent.getLogType());
        assertEquals(SpanType.URL + " Trace Span Stop", logEvent.getTitle());
        assertEquals("[testUrlService : testRootSpan]", logEvent.getMessage());
        assertEquals(LogLevel.DEBUG, logEvent.getLogLevel());
        assertEquals(TracerTest.class.getName(), logEvent.getSource());
        assertEquals(span.getTraceId(), logEvent.getTraceId());
	}
	
	@Test
	public void testNestedSpan() {
        ISpan urlSpan = tracer.startSpan("testRootSpan", "testUrlService", SpanType.URL);
        assertTrue(tracer.isTracing());

        ISpan rootSpan = tracer.getCurrentSpan();

        ISpan sqlSpan = tracer.startSpan("testSqlSpan", "testSqlService", SpanType.SQL);
        assertTrue(rootSpan.getTraceId() > 0 );
        assertEquals(rootSpan.getTraceId(), sqlSpan.getTraceId());
        assertEquals(rootSpan.getInnerSpan().getTraceId(), sqlSpan.getInnerSpan().getTraceId());
        assertEquals(rootSpan, sqlSpan.getParent());
        assertTrue(rootSpan.isRunning());
        assertEquals(rootSpan.getSpanId(), sqlSpan.getParentId());
        assertEquals(rootSpan.getSpanType(), SpanType.URL);
        assertEquals(sqlSpan.getSpanType(), SpanType.SQL);
        
        sqlSpan.stop();
        assertEquals(tracer.getCurrentSpan(), rootSpan);
        
        CountSender cSender = (CountSender)sender;
        assertEquals(1, cSender.getSpanCount());
        assertEquals(0, cSender.getLogEventCount());
        Span span = cSender.getSpans().get(0);
        assertEquals(2, span.getLogEventsSize());
        LogEvent logEvent = span.getLogEvents().get(0);
        assertEquals(rootSpan.getTraceId(), logEvent.getTraceId());
        logEvent = span.getLogEvents().get(1);
        assertEquals(rootSpan.getTraceId(), logEvent.getTraceId());
        
        urlSpan.stop();
        assertEquals(null, tracer.getCurrentSpan());
        cSender = (CountSender)sender;
        assertEquals(2, cSender.getSpanCount());
        assertEquals(0, cSender.getLogEventCount());
        span = cSender.getSpans().get(1);
        assertEquals(2, span.getLogEventsSize());
        logEvent = span.getLogEvents().get(0);
        assertEquals(rootSpan.getTraceId(), logEvent.getTraceId());
        logEvent = span.getLogEvents().get(1);
        assertEquals(rootSpan.getTraceId(), logEvent.getTraceId());
	}
	
	@Test
	public void testLogging() {
        ISpan urlSpan = tracer.startSpan("testRootSpan", "testUrlService", SpanType.URL);
        ISpan rootSpan = tracer.getCurrentSpan();

        ISpan sqlSpan = tracer.startSpan("testSqlSpan1", "testSqlService", SpanType.SQL);
        sqlSpan.stop();
        assertEquals(rootSpan, tracer.getCurrentSpan());
        

        sqlSpan = tracer.startSpan("testSqlSpan2", "testSqlService", SpanType.SQL);

        ISpan webServiceSpan = tracer.startSpan("testWsSpan", "testWsService", SpanType.WEB_SERVICE);
        tracer.log(LogType.WEB_SERVICE, LogLevel.INFO, "testTitle1", "test message1");
        tracer.log(LogType.WEB_SERVICE, LogLevel.INFO, "testTitle2", "test message2");
        tracer.log(LogType.WEB_SERVICE, LogLevel.ERROR, "testTitle3", new Exception("test exception"));
        Map<String, String> attrs = new HashMap<String,String>();
        attrs.put("key1", "value1");
        attrs.put("key2", "value2");
        tracer.log(LogType.WEB_SERVICE, LogLevel.INFO,"testTitle4" , "test message 4", attrs);
        webServiceSpan.stop(); // testWsSpan
        
        CountSender cSender = (CountSender)sender;
        assertEquals(2, cSender.getSpanCount());
        Span wsSpan = cSender.getSpans().get(1);
        assertEquals(6, wsSpan.getLogEventsSize());
        LogEvent logEvent = wsSpan.getLogEvents().get(1);
        assertEquals("testTitle1", logEvent.getTitle());
        assertEquals("test message1", logEvent.getMessage());
        assertEquals(LogType.WEB_SERVICE, logEvent.getLogType());
        assertEquals(TracerTest.class.getName(), logEvent.getSource());
        assertEquals(rootSpan.getTraceId(), logEvent.getTraceId());
        assertEquals(LogLevel.INFO, logEvent.getLogLevel());
        logEvent = wsSpan.getLogEvents().get(4);
        assertEquals("testTitle4", logEvent.getTitle());
        assertEquals("test message 4", logEvent.getMessage());
        assertEquals(LogType.WEB_SERVICE, logEvent.getLogType());
        assertEquals(TracerTest.class.getName(), logEvent.getSource());
        assertEquals(rootSpan.getTraceId(), logEvent.getTraceId());
        assertEquals(LogLevel.INFO, logEvent.getLogLevel());
        attrs = logEvent.getAttributes();
        assertNotNull(attrs);
        assertEquals("value1", attrs.get("key1"));
        assertEquals("value2", attrs.get("key2"));
        
        logEvent = wsSpan.getLogEvents().get(3);
        assertEquals(TracerTest.class.getName(), logEvent.getSource());
        assertEquals(rootSpan.getTraceId(), logEvent.getTraceId());
        assertEquals(LogLevel.ERROR, logEvent.getLogLevel());

        sqlSpan.stop(); // testSqlSpan1

        urlSpan.stop(); // testRootSpan
	}
	
}
