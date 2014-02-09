package com.ctrip.freeway.tracing;

import com.ctrip.freeway.gen.v2.LogLevel;
import com.ctrip.freeway.gen.v2.LogType;
import com.ctrip.freeway.gen.v2.SpanType;

import java.util.Map;

/**
 * Trace接口，具体实现会在线程空间中收集span以及和span关联的日志，并将收集到的span发送到后端。
 * 
 * 使用方法：
 *
 * 
 * ITrace tracer = TraceManager.getTracer(); // get tracer instance
 * 
 * ISpan span = tracer.startSpan("spanName", "serviceName", SpanType.XXX); 
 * try { 
 *   doSomething(); // call service or do computation
 * } 
 * finally 
 * { 
 *   span.stop(); 
 * }
 *
 * tracer.startSpan()会开启一个新的span，而span.stop会关闭span，将该span写到后端并从当前线程空间移除该span.
 * 
 * 在doSomething()调用的过程中，用户可以根据需要利用tracer.log() API写入一些和span type对应的相关日志，这些日志将附着在当前span上。
 * 
 * 注意，log title, message, 附加的健值对都有字符数限制，超过的部分会被截断, 
 * 相应字符数限制如下：
 * log title, 字符数限制为32个字符,
 * log message, 字符数限制为32K个字符,
 * 键值对key字符数限制为32个字符,value字符数限制为2K个字符,健值对的总数限制为8个。
 * 
 * 
 * @author yangbo
 *
 */
public interface ITrace {
	
	/**
	 * 开启一个trace span, 该调用将使用用户指定的采样器。
     * 
     * 如果当前线程空间没有开启的span，那么该调用将自动开启一个根(root)span,
     * 否则，该调用创建当前span的子span，并将该子span作为当前span.
	 * 
	 * @param spanName the name of the span, 通常是服务的一个具体的方法或调用名
	 * @param serviceName the name of the service
	 * @param spanType the type of the span
	 * @param s a sampler
	 * @return an ISpan instance
	 */
	ISpan startSpan(String spanName, String serviceName, SpanType spanType, ISampler s);
	
	/**
     * 开启一个trace span, 该调用将使用缺省的采样器-所有的trace都被采样。
     * 
     * 如果当前线程空间没有开启的span，那么该调用将自动开启一个根(root)span,
     * 否则，该调用创建当前span的子span，并将该子span作为当前span.
	 * 
	 * @param spanName the name of the span, 通常是服务的一个具体的方法或调用名
	 * @param serviceName the name of the service
	 * @param spanType the type of the span
	 * @return an ISpan instance
	 */
	ISpan startSpan(String spanName, String serviceName, SpanType spanType);
	
	/**
	 * 检查tracing开启状态
	 * @return the tracing status
	 */
	boolean isTracing();
	
	/**
     * 清理当前线程空间中的所有trace spans。
     * 
     * 清理时如果如果当前线程空间有未关闭的spans，这些spans会被写入后端，并标记状态为unfinished.
     * 
     * 该方法将清理所有未显式关闭(stop)的spans，理想情况下，如果所有spans都被正确开启和关闭，那么该方法是不需要调用的。
     * 但为确保tracing的正确和完整，可以在线程开启和退出时显式调用该方法，例如可以在前端（请求入口）开启URL span之前显式调用该方法，
     * 以确保当前线程空间是干净的，亦可以在前端关闭URL span之后显式调用该方法，以确保不污染当前线程空间（该线程可能被后续请求重用）。
     * 清理的另外一个功能就是将未正确关闭（unfinished）的spans写入后端
	 */
	void clear();
	
	/**
     * 利用指定的tracing信息接续一条trace.
     * 该方法用于在跨越进程边界时接续一条trace，调用进程(例如一个web service客户)将tracing信息传递到被调用进程(例如一个web service服务器)，
     * 被调用进程利用这些指定的tracing信息继续这条trace.
     * 
     * 
	 * @param spanName span name
	 * @param serviceName service name
	 * @param traceId trace id
	 * @param parentId parent id
	 * @param spanType span type
	 */
	ISpan continueSpan(String spanName, String serviceName, long traceId, long parentId, SpanType spanType);
	
	/**
	 * 获得当前开启的span
	 * 
	 * @return current span
	 */
	ISpan getCurrentSpan();
	
	/**
	 * This method is reserved for App log via LogManager
	 * 
	 * @param logEvent
	 */
//	void log(LogEvent logEvent);
	
	/**
	 * 在当前trace span上记录一条span相关日志，
     * 日志类型(log type)同当前span type.
	 * 
	 * @param type log type
	 * @param level log level
	 * @param title log title
	 * @param message log message
	 */
	void log(LogType type, LogLevel level, String title, String message);
	
	/**
     *在当前trace span上记录一条span相关例外日志，
     * 日志类型(log type)同当前span type.
	 * 
	 * @param type log type
	 * @param level log level
	 * @param title log title
	 * @param t throwable to be logged
	 */
	void log(LogType type, LogLevel level, String title, Throwable t);
	
	/**
	 * 在当前trace span上记录span相关健/值对日志，
     * 日志类型(log type)同当前span type.
	 * 
	 * @param type log type
	 * @param level log level
	 * @param title log title
	 * @param message log message
	 * @param attrs key/value pairs
	 */
	void log(LogType type, LogLevel level, String title, String message, Map<String, String> attrs);
	
	/**
	 * 在当前trace span上记录span相关例外和健/值对日志，
     * 日志类型(log type)同当前span type.
	 * 
	 * @param type log type
	 * @param level log level
	 * @param title log title
	 * @param throwable to log
	 * @param attrs key/value pairs
	 */
	void log(LogType type, LogLevel level, String title, Throwable throwable, Map<String, String> attrs);
	
	/**
	 * 在当前trace span上记录一条span相关日志，
     * 日志类型(log type)同当前span type.
     * 
	 * @param type log type
     * @param level log level
	 * @param message log message
	 */
	void log(LogType type, LogLevel level, String message);
	
	/**
	 * 在当前trace span上记录一条span相关例外日志，
     * 日志类型(log type)同当前span type.
     * 
	 * @param type log type
     * @param level log level
	 * @param t throwable to be logged
	 */
	void log(LogType type, LogLevel level, Throwable t);
	
	/**
	 *  在当前trace span上记录span相关健/值对日志，
     * 日志类型(log type)同当前span type.
	 * 
	 * @param type log type
	 * @param level log level
	 * @param message log message
	 * @param attrs key/value pairs
	 */
	void log(LogType type, LogLevel level, String message, Map<String, String> attrs);
	
	/**
	 *  在当前trace span上记录span相关例外和健/值对日志，
     * 日志类型(log type)同当前span type.
	 * 
	 * @param type log type
	 * @param level log level
	 * @param throwable to log
	 * @param attrs key/value pairs
	 */
	void log(LogType type, LogLevel level, Throwable throwable, Map<String, String> attrs);
}
