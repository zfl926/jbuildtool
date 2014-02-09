package com.ctrip.freeway.tracing.impl;

import com.ctrip.freeway.gen.v2.LogEvent;
import com.ctrip.freeway.gen.v2.Span;

/**
 * Trace sender will send trace span to a target
 * 
 * @author yangbo
 *
 */
public interface ITraceSender {

	void send(Span span);
	
	void send(LogEvent logEvent);
	
}
