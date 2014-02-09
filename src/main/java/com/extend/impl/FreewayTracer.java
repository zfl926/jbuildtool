package com.ctrip.freeway.tracing.impl;

import com.ctrip.freeway.agent.AgentManager;
import com.ctrip.freeway.agent.MessageConsumer;
import com.ctrip.freeway.config.CollectorRegistry;
import com.ctrip.freeway.config.ConfigManager;
import com.ctrip.freeway.gen.v2.LogEvent;
import com.ctrip.freeway.gen.v2.LogLevel;
import com.ctrip.freeway.gen.v2.LogType;
import com.ctrip.freeway.gen.v2.Span;
import com.ctrip.freeway.util.LogEventUtil;

import java.util.ArrayList;
import java.util.List;

public class FreewayTracer extends CommonTracer {
	public FreewayTracer(String name) {
		super(name);
		this.setSender(new FreewayTraceSender());
	}
	
	static class FreewayTraceSender implements ITraceSender {
		
		@Override
		public void send(Span span) {
			
			// if no collector configured, just ignore this event
			if (CollectorRegistry.getInstance().getCollector() == null) return;
			
            // lazy initialization
            MessageConsumer messageConsumer = AgentManager.getConsumerInstance();
            ConfigManager configManager = ConfigManager.getInstance();

            // swith on or off logging by global setting
            boolean traceEnabled = configManager.isTraceEnabled();
            LogLevel traceLogLevel = configManager.getTraceLogLevel();
			
			if (traceEnabled) {
				
				filterLogEventByLevel(span, traceLogLevel);
				for(LogEvent logEvent : span.getLogEvents()) {
					LogEventUtil.truncateLogSize(logEvent);
				}
				messageConsumer.process(span);
			} else { // APP log should be sent out since it is a separate log type
				for(LogEvent logEvent : span.getLogEvents()) {
					if (logEvent.getLogType() == LogType.APP) {
						// truncate log event size
						LogEventUtil.truncateLogSize(logEvent);
						
						// remove trace id since tracing is disabled
						logEvent.setTraceId(0);
						messageConsumer.process(logEvent);
					}
				}
			}
		}

		@Override
		public void send(LogEvent logEvent) {
			// if no collector configured, just ignore this event
			if (CollectorRegistry.getInstance().getCollector() == null) return;
			
            // lazy initialization
            MessageConsumer messageConsumer = AgentManager.getConsumerInstance();
            ConfigManager configManager = ConfigManager.getInstance();

            // swith on or off logging by global setting
            boolean traceEnabled = configManager.isTraceEnabled();
            LogLevel traceLogLevel = configManager.getTraceLogLevel();
            if (logEvent.getLogType() != LogType.APP) {
            	if (traceEnabled)
            	{
            		if (!this.isLogLevelEnabled(logEvent.getLogLevel(), traceLogLevel)) {
            			return;
            		}
            	} else {
            		return;
            	}
            }
            
            LogLevel appLogLevel = configManager.getAppLogLevel();
            if (!isLogLevelEnabled(logEvent.getLogLevel(), appLogLevel)) {
            	return;
            }
            
            // truncate log event size
            LogEventUtil.truncateLogSize(logEvent);
			messageConsumer.process(logEvent);
		}
		
		private void filterLogEventByLevel(Span span, LogLevel currentLogLevel) {
			if (span.getLogEventsSize() > 0) {
				List<LogEvent> tobeRemovedList = new ArrayList<LogEvent>();
				for(LogEvent logEvent : span.getLogEvents()) {
					// app log has already been filtered by app logger, so we don't filter them again here
					if (logEvent.getLogType() != LogType.APP && 
							!isLogLevelEnabled(logEvent.getLogLevel(), currentLogLevel)) {
						tobeRemovedList.add(logEvent);
					}
				}
				if (tobeRemovedList.size() > 0) {
					span.getLogEvents().removeAll(tobeRemovedList);
				}
			}
		}
		
		private boolean isLogLevelEnabled(LogLevel level, LogLevel currentLogLevel) {
			return level.getValue() >= currentLogLevel.getValue();
		}
		
	}
}
