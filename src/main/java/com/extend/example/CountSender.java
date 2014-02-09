package com.ctrip.freeway.tracing.impl;

import com.ctrip.freeway.gen.v2.LogEvent;
import com.ctrip.freeway.gen.v2.Span;

import java.util.ArrayList;
import java.util.List;

public class CountSender implements ITraceSender {
	
	private int spanCount;
	private int logEventCount;
	private List<Span> spans = new ArrayList<Span>();
	private List<LogEvent> logEvents = new ArrayList<LogEvent>();
	
	@Override
	public void send(Span span) {
		spanCount++;
		spans.add(span);
	}

	@Override
	public void send(LogEvent logEvent) {
		logEventCount++;
		logEvents.add(logEvent);
	}

	public int getSpanCount() {
		return spanCount;
	}
	
	public int getLogEventCount() {
		return logEventCount;
	}
	
	public List<Span> getSpans() {
		return spans;
	}
	
	public List<LogEvent> getLogEvents() {
		return logEvents;
	}
}
