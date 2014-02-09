package com.ctrip.freeway.tracing.impl;

import com.ctrip.freeway.gen.v2.LogEvent;
import com.ctrip.freeway.gen.v2.SpanType;
import com.ctrip.freeway.tracing.ISpan;
import com.ctrip.freeway.tracing.ITrace;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SpanTest {
	
	private ITrace tracer;
	private ITraceSender sender;
	
	@Before
	public void initCase() {
		tracer = new CommonTracer(SpanTest.class.getName());
		tracer.clear();
		sender = new CountSender();
		((CommonTracer)tracer).setSender(sender);
		
		assertFalse(tracer.isTracing());
	}
	
	@Test
	public void testSpan() {
		ISpan urlSpan = tracer.startSpan("testUrl111Span", "testUrlService", SpanType.URL);
		long traceId = urlSpan.getTraceId();
		long urlSpanId = urlSpan.getSpanId();
        assertEquals("[testUrlService : testUrl111Span]", urlSpan.getDescription());
        
        ISpan sqlSpan = urlSpan.createChild("testSqlSpan", "testSqlService", SpanType.SQL, (CommonTracer)tracer);
        ISpan wsSpan = urlSpan.createChild("testWsSpan", "testWebService", SpanType.WEB_SERVICE, (CommonTracer)tracer);
        ISpan memcachedSpan = wsSpan.createChild("testMemcachedSpan", "testMemcachedService", SpanType.MEMCACHED, (CommonTracer)tracer);
        assertEquals(wsSpan, memcachedSpan.getParent());
        assertEquals(traceId, urlSpan.getTraceId());
        assertEquals(urlSpanId, urlSpan.getSpanId());
        

        assertEquals(traceId, sqlSpan.getTraceId());
        assertEquals(urlSpanId, sqlSpan.getParentId());

        assertEquals(traceId, wsSpan.getTraceId());
        assertEquals(urlSpanId, wsSpan.getParentId());

        assertEquals(urlSpan.getTraceId(), memcachedSpan.getTraceId());
        assertEquals(wsSpan.getSpanId(), memcachedSpan.getParentId());

        assertTrue(wsSpan.isRunning());
        
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        assertTrue(wsSpan.getAccumulateMillis() > 0);
        
        ((MilliSpan)wsSpan).addLogEvent(new LogEvent());
        assertTrue(wsSpan.getLogEvents().size() == 1);
        assertEquals(wsSpan.getTraceId(), wsSpan.getLogEvents().get(0).getTraceId());
        assertEquals(traceId, wsSpan.getLogEvents().get(0).getTraceId());
        
        wsSpan.stop();
	}
	
}
