package com.ctrip.freeway.tracing.impl;

import com.ctrip.freeway.gen.v2.SpanType;

/**
 * Span that roots the span tree.
 * 
 * @author yangbo
 *
 */
class RootMilliSpan extends MilliSpan {
	
	private final long traceId;
	private final long parentId;
	
	public RootMilliSpan(String name, String serviceName, long traceId, long spanId, long parentId, SpanType spanType, CommonTracer tracer) {
		super(name, serviceName, spanId, null, spanType, tracer);
		this.traceId = traceId;
		this.getInnerSpan().setTraceId(traceId);
		this.parentId = parentId;
		this.getInnerSpan().setParentId(parentId);
	}
	
	public long getTraceId() {
		return traceId;
	}
	
	public long getParentId() {
		return parentId;
	}
	
}
