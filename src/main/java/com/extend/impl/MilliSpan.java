package com.ctrip.freeway.tracing.impl;

import com.ctrip.freeway.gen.v2.LogEvent;
import com.ctrip.freeway.gen.v2.Span;
import com.ctrip.freeway.gen.v2.SpanType;
import com.ctrip.freeway.tracing.ISpan;
import com.ctrip.freeway.tracing.ITrace;
import com.ctrip.freeway.util.HostUtil;
import com.ctrip.freeway.util.RandomUtil;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * A Span implementation that stores its information in milliseconds since the epoch.
 * 
 * @author yangbo
 *
 */
public class MilliSpan implements ISpan {
	
	// inner thrift span
	private final Span innerSpan;
	private final ISpan parent;
	private final CommonTracer tracer;
	
	public MilliSpan(String name, String serviceName, long id, ISpan parent, SpanType spanType, CommonTracer tracer) {
		this.innerSpan = new Span();
		if (StringUtils.isBlank(name)) {
			this.innerSpan.setName("NoNameSpan");
		} else {
			this.innerSpan.setName(name);
		}
		if (StringUtils.isBlank(serviceName)) {
			this.innerSpan.setServiceName("NoNameService");
		} else {
			this.innerSpan.setServiceName(serviceName);
		}
		this.innerSpan.setSpanId(id);
		this.innerSpan.setSpanType(spanType);
		this.parent = parent;
		this.innerSpan.setParentId(getParentId());
		this.innerSpan.setTraceId(getTraceId());
		this.innerSpan.setStartTime(System.currentTimeMillis());
		this.innerSpan.setStopTime(0);
		this.innerSpan.setHostIp(HostUtil.getHostIp());
		this.innerSpan.setUnfinished(false);
		this.innerSpan.setThreadId(Thread.currentThread().getId());
		
		this.tracer = tracer;
	}

	@Override
	public void stop() {
		if (this.isStopped()) {
			// no effect, has been stopped
			return;
		}
		this.innerSpan.setStopTime(System.currentTimeMillis());
		tracer.pop(this);
	}
	
	@Override
	public boolean isStopped() {
		return this.innerSpan.getStopTime() != 0L;
	}

	@Override
	public long getAccumulateMillis() {
		if (this.innerSpan.getStartTime() == 0) {
			return 0;
		}
		if (this.innerSpan.getStopTime() > 0) {
			return this.innerSpan.getStopTime() - this.innerSpan.getStartTime();
		}
		return System.currentTimeMillis() - this.innerSpan.getStartTime();
	}

	@Override
	public boolean isRunning() {
		return this.innerSpan.getStartTime() != 0L && this.innerSpan.getStopTime() == 0L;
	}
	
	@Override
	public String toString() {
		long parentId = this.getParentId();
		return "\"" + this.getDescription() + "\" trace:" + this.getTraceId()
		+ " span:" + this.innerSpan.getSpanId() + (parentId > 0? " parent:" + parentId : "")
		+ " start:" + this.innerSpan.getStartTime() + " ms " + Long.toString(this.getAccumulateMillis()) + (this.isRunning()? "..." : "");
	}

	@Override
	public String getDescription() {
		return "[" + this.innerSpan.getServiceName() + " : " +  this.innerSpan.getName()  + "]"; 
	}

	@Override
	public long getSpanId() {
		return this.innerSpan.getSpanId();
	}

	@Override
	public ISpan getParent() {
		return parent;
	}

	@Override
	public long getTraceId() {
		return this.parent.getTraceId();
	}

	@Override
	public ISpan createChild(String name, String serviceName, SpanType spanType, ITrace tracer) {
		return new MilliSpan(name, serviceName, RandomUtil.nextLong(), this, spanType, (CommonTracer)tracer);
	}

	@Override
	public long getParentId() {
		return this.parent.getSpanId();
	}

	@Override
	public List<LogEvent> getLogEvents() {
		return Collections.unmodifiableList(this.innerSpan.getLogEvents());
	}

	public void addLogEvent(LogEvent logEvent) {
		if (logEvent == null) return;
		logEvent.setTraceId(this.getTraceId());
		this.innerSpan.addToLogEvents(logEvent);
	}

	@Override
	public SpanType getSpanType() {
		return this.innerSpan.getSpanType();
	}

	@Override
	public Span getInnerSpan() {
		return this.innerSpan;
	}

	@Override
	public long getStartTimeMillis() {
		return this.getInnerSpan().getStartTime();
	}
	
	@Override
	public long getStopTimeMillis() {
		return this.getInnerSpan().getStopTime();
	}

}
