package com.ctrip.freeway.tracing.impl;

import com.ctrip.freeway.gen.v2.SpanType;
import com.ctrip.freeway.tracing.ISampler;
import com.ctrip.freeway.tracing.ISpan;
import com.ctrip.freeway.tracing.ITrace;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestSampler {
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
	public void testParamterizedSampler() {    
		TestParamSampler sampler = new TestParamSampler(1);    
		ISpan s = tracer.startSpan("test", "testService", SpanType.OTHER, sampler);    
		assertFalse(s.equals(NullSpan.getInstance()));    
		s.stop();
		sampler = new TestParamSampler(-1);
		s = tracer.startSpan("test", "testService", SpanType.OTHER, sampler);    
		assertTrue(s.equals(NullSpan.getInstance()));    
		s.stop();  
	}
	
	@Test
	public void testAlwaysSampler() {
		ISpan cur = tracer.startSpan("test", "testService", SpanType.OTHER, ISampler.ALWAYS);
		assertFalse(cur.equals(NullSpan.getInstance()));
		cur.stop();
		
	}
	
	private class TestParamSampler implements ISampler {
		
		private int info;
		
		public TestParamSampler(int info) {
			this.info = info;
		}
		
		@Override
		public boolean next() {
			return info > 0;
		}
		
	}

}
