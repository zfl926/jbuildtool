package com.ctrip.freeway.tracing.impl;

import com.ctrip.freeway.gen.v2.LogEvent;
import com.ctrip.freeway.gen.v2.LogLevel;
import com.ctrip.freeway.gen.v2.LogType;
import com.ctrip.freeway.gen.v2.SpanType;
import com.ctrip.freeway.tracing.ISampler;
import com.ctrip.freeway.tracing.ISpan;
import com.ctrip.freeway.tracing.ITrace;
import com.ctrip.freeway.util.LogIdUtil;
import com.ctrip.freeway.util.RandomUtil;
import org.apache.commons.lang.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

class CommonTracer implements ITrace {

	private ITraceSender sender;
	
	private static final ThreadLocal<ISpan> currentSpan = new ThreadLocal<ISpan>();
	private static final long ROOT_SPAN_ID = 0l;
	private String name;
	
	public CommonTracer(String name) {
		if (StringUtils.isBlank(name)) {
			this.name = "defaultTraceName";
		} else {
			this.name = name;
		}
	}
	
	@Override
	public ISpan startSpan(String spanName, String serviceName, SpanType spanType, ISampler s) {
		if (!s.next()) {
			currentSpan.set(NullSpan.getInstance());
			return currentSpan.get();
		}
		ISpan parent = currentSpan.get();
		ISpan root;
		if (parent == null || parent instanceof NullSpan) {
			root = new RootMilliSpan(spanName, serviceName, RandomUtil.nextLong(), 
					RandomUtil.nextLong(), ROOT_SPAN_ID, spanType, this);
		} else {
			root = parent.createChild(spanName, serviceName, spanType, this);
		}
		push(root);
		
		return root;
	}
	
	@Override
	public ISpan startSpan(String spanName, String serviceName,
			SpanType spanType) {
		return this.startSpan(spanName, serviceName, spanType, AlwaysSampler.INSTANCE);
	}


	@Override
	public boolean isTracing() {
		return currentSpan.get() != null && !(currentSpan.get() instanceof NullSpan);
	}
	
	private void push(ISpan span) {
		if (span != null) {
			currentSpan.set(span);
			
			LogEvent logEvent = new LogEvent();
			logEvent.setId(LogIdUtil.getNextLogId());
			logEvent.setLogType(spanToLogType(span.getSpanType()));
			logEvent.setTitle(span.getSpanType() + " Trace Span Start");
			logEvent.setMessage(span.getDescription());
			logEvent.setLogLevel(LogLevel.DEBUG);
			logEvent.setSource(name);
			logEvent.setThreadId(Thread.currentThread().getId());
			logEvent.setCreatedTime(System.currentTimeMillis());
			((MilliSpan)span).addLogEvent(logEvent);
		}
	}
	
    private LogType spanToLogType(SpanType spanType)
    {
        if (spanType == SpanType.URL) {
            return LogType.URL;
        }
        if (spanType == SpanType.SQL) {
            return LogType.SQL;
        }
        if (spanType == SpanType.WEB_SERVICE)
        {
            return LogType.WEB_SERVICE;
        }
        if (spanType == SpanType.MEMCACHED)
        {
            return LogType.MEMCACHED;
        }
        return LogType.OTHER;
    }
    
    // check if span is on parent path of currSpan
    private boolean isOnParentPath(ISpan currSpan, ISpan span)
    {
    	if (span == null) return false;
    	int i = 0;
    	while(currSpan != span && currSpan != null) {
    		i++;
    		if (i > 50) {
    			log(LogType.OTHER, LogLevel.WARN, "possible unlimited loop in unfinished span handling.");
    			break; // prevent possible unlimited loop
    		}
    		currSpan = currSpan.getParent();
    	}
    	
    	if (currSpan == span) return true;
    	
    	return false;
    }
    
    protected void pop(ISpan span) {
    	if (span != null) {
    		if (span != currentSpan.get()) {
    			log(LogType.OTHER, LogLevel.WARN, "Stopped span: " + span
    				+ " was not the current span. current span is: "
    				+ currentSpan.get());
    			if (this.isOnParentPath(currentSpan.get(), span)) {
    				while(currentSpan.get() != span) {
    					// deliver unfinished span
    					ISpan currSpan = currentSpan.get();
    					currSpan.getInnerSpan().setStopTime(System.currentTimeMillis());
    					currSpan.getInnerSpan().setUnfinished(true);
    					deliver(currSpan);
    					currentSpan.set(currSpan.getParent());
    				}
    			}
    		}
    		
    		LogEvent logEvent = new LogEvent();
    		logEvent.setId(LogIdUtil.getNextLogId());
    		logEvent.setLogType(spanToLogType(span.getSpanType()));
    		logEvent.setTitle(span.getSpanType() + " Trace Span Stop");
    		logEvent.setMessage(span.getDescription());
    		logEvent.setLogLevel(LogLevel.DEBUG);
    		logEvent.setSource(name);
    		logEvent.setThreadId(Thread.currentThread().getId());
    		logEvent.setCreatedTime(System.currentTimeMillis());
    		((MilliSpan)span).addLogEvent(logEvent);
    		
    		currentSpan.set(span.getParent());
    		
    		deliver(span);
    	} else {
    		currentSpan.set(null);
    	}
    }

	@Override
	public void clear() {
		int i = 0;
		while (currentSpan.get() != null && !(currentSpan.get() instanceof NullSpan)) {
			i++;
			if (i > 50) {
				log(LogType.OTHER, LogLevel.WARN, "possible unlimited loop in unfinished span handling");
				break; // prevent possible unlimited loop
			}
			// deliver unfinished span
			ISpan currSpan = currentSpan.get();
			currSpan.getInnerSpan().setStopTime(System.currentTimeMillis()); // mark the span as stopped
			currSpan.getInnerSpan().setUnfinished(true);
			deliver(currSpan);
			currentSpan.set(currSpan.getParent());
		}
		
		currentSpan.set(null);
	}

	@Override
	public ISpan continueSpan(String spanName, String serviceName,
			long traceId, long parentId, SpanType spanType) {
		ISpan rootSpan = new RootMilliSpan(spanName, serviceName, traceId, RandomUtil.nextLong(), parentId, spanType, this);
		push(rootSpan);
		return rootSpan;
	}

	@Override
	public ISpan getCurrentSpan() {
		return currentSpan.get();
	}

	public void log(LogEvent logEvent) {
		if (!logEvent.isSetId()) {
			logEvent.setId(LogIdUtil.getNextLogId());
		}
		if (!logEvent.isSetLogType()) {
			logEvent.setLogType(LogType.APP);
		}
		if (!logEvent.isSetCreatedTime()) {
			logEvent.setCreatedTime(System.currentTimeMillis());
		}
		if (!logEvent.isSetThreadId()) {
			logEvent.setThreadId(Thread.currentThread().getId());
		}
		if (StringUtils.isBlank(logEvent.getTitle())) {
			logEvent.setTitle("NA");
		}
		if (StringUtils.isBlank(logEvent.getMessage())) {
			logEvent.setMessage("NA");
		}
		if (!logEvent.isSetSource()) {
			logEvent.setSource(name);
		}
		if (this.isTracing()) {
			// attach the log event to current span
			((MilliSpan)currentSpan.get()).addLogEvent(logEvent);
		} else {
			// deliver the logEvent directly if tracing is not enabled.
			deliver(logEvent);
		}
	}
	
	private void deliver(ISpan span) {
		if (sender != null) {
			sender.send(span.getInnerSpan());
		}
	}
	
	private void deliver(LogEvent logEvent) {
		if (sender != null) {
			sender.send(logEvent);
		}
	}
	
	private void log(LogType type, String title, String message, LogLevel logLevel, Map<String, String> attrs) {
		/*
		if (currentSpan.get() instanceof NullSpan) {
			return; // ignore this log
		}
		if (currentSpan.get() == null) {
			throw new IllegalStateException("No trace span has been started yet.");
		}
		*/
		LogEvent logEvent = new LogEvent();
		logEvent.setId(LogIdUtil.getNextLogId());
		//SpanType spanType = currentSpan.get().getSpanType();
		//logEvent.setLogType(this.spanToLogType(spanType));
		logEvent.setLogType(type);
		logEvent.setTitle(title);
		
		logEvent.setMessage(message);
		logEvent.setSource(name);
		logEvent.setAttributes(attrs);
		logEvent.setLogLevel(logLevel);
		
		log(logEvent);
	}

	@Override
	public void log(LogType type, LogLevel level, String title, String message) {
		this.log(type, title, message, level, null);
	}

	@Override
	public void log(LogType type, LogLevel level, String title, Throwable t) {
		String msg = "NullThrowable";
		if (t != null) {
			msg = t.toString();
		}
		this.log(type, title, msg, level, null);
	}

	@Override
	public void log(LogType type, LogLevel level, String title, String message, Map<String, String> attrs) {
		this.log(type, title, message, level, attrs);
	}

	@Override
	public void log(LogType type, LogLevel level, String message) {
		this.log(type, null, message, level, null);
	}

	@Override
	public void log(LogType type, LogLevel level, Throwable t) {
		String msg = "NullThrowable";
		if (t != null) {
			msg = t.toString();
		}
		this.log(type, null, msg, level, null);
	}

	@Override
	public void log(LogType type, LogLevel level, String message, Map<String, String> attrs) {
		this.log(type, null, message, level, attrs);
	}
	
	/**
	 * Get a span or log event sender
	 * @return ITraceSender instance
	 */
	public ITraceSender getSender() {
		return sender;
	}
	
	/**
	 * Set a span or log event sender
	 * @param sender ITranceSender instance
	 */
	public void setSender(ITraceSender sender) {
		this.sender = sender;
	}

	@Override
	public void log(LogType type, LogLevel level, String title, Throwable throwable,
			Map<String, String> attrs) {
		String msg = "NullThrowable";
		if (throwable != null) {			
			msg = this.convertThrowableToString(throwable);
		}
		this.log(type, title, msg, level, attrs);	
	}

	@Override
	public void log(LogType type, LogLevel level, Throwable throwable,
			Map<String, String> attrs) {
		String msg = "NullThrowable";
		if (throwable != null) {			
			msg = throwable.getMessage() + this.convertThrowableToString(throwable);
		}
		this.log(type, null, msg, level, attrs);	
		
	}
	
	private String convertThrowableToString(Throwable throwable) {
		StringWriter sw = new StringWriter();             
		PrintWriter pw = new PrintWriter(sw); 
		
		throwable.printStackTrace(pw);
		
		return sw.toString();
	}
	
	
}
