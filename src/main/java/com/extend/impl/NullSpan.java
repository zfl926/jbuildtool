package com.ctrip.freeway.tracing.impl;

import com.ctrip.freeway.gen.v2.LogEvent;
import com.ctrip.freeway.gen.v2.Span;
import com.ctrip.freeway.gen.v2.SpanType;
import com.ctrip.freeway.tracing.ISpan;
import com.ctrip.freeway.tracing.ITrace;

import java.util.Collections;
import java.util.List;

/**
 * A Span that does nothing. Used to avoid returning and checking for nulls when we are not tracing.
 * 
 * @author yangbo
 *
 */
public class NullSpan implements ISpan {
	
	private static NullSpan instance = new NullSpan();
	
	// No need to ever have more than one NullSpan.
	public static NullSpan getInstance() {
		return instance;
	}

	private NullSpan() {
	}
	
	@Override
	public void stop() {
	}
	
	@Override
	public boolean isStopped() {
		return false;
	}

	@Override
	public long getStartTimeMillis() {
		return 0;
	}

	@Override
	public long getStopTimeMillis() {
		return 0;
	}

	@Override
	public long getAccumulateMillis() {
		return 0;
	}

	@Override
	public boolean isRunning() {
		return false;
	}

	@Override
	public String getDescription() {
		return "NullSpan";
	}

	@Override
	public long getSpanId() {
		return -1;
	}

	@Override
	public ISpan getParent() {
		return null;
	}

	@Override
	public long getTraceId() {
		return -1;
	}

	@Override
	public ISpan createChild(String name, String serviceName,
			SpanType spanType, ITrace tracer) {
		return this;
	}

	@Override
	public long getParentId() {
		return -1;
	}

	@Override
	public List<LogEvent> getLogEvents() {
		return Collections.EMPTY_LIST;
	}

	public void addLogEvent(LogEvent logEvent) {
	}

	@Override
	public SpanType getSpanType() {
		return SpanType.OTHER;
	}

	@Override
	public Span getInnerSpan() {
		return null;
	}

}
