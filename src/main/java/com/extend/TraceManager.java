package com.ctrip.freeway.tracing;

import com.ctrip.freeway.tracing.impl.FreewayTracer;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于创建ITrace<see cref="ITrace" />实例。
 * 
 * @author yangbo
 *
 */
public final class TraceManager {

	private static Map<String, ITrace> _tracers = new ConcurrentHashMap<String, ITrace>();
	
	/**
	 * Initializes a new instance of the <see cref="TraceManager" /> class. 
	 * 
	 * Uses a private access modifier to prevent instantiation of this class.
	 * 
	 */
	private TraceManager() {}
	
	/**
	 * 通过类型名获取ITrace实例
	 * 
	 * @param type The type
	 * @return ITrace instance
	 */
	public static ITrace getTracer(Class<?> type) {
		if (type == null) {
			return getTracer("NoName");
		}
		return getTracer(type.getName());
	}
	
	/**
	 * 通过字符串名获取ITrace实例。
	 * 
	 * @param name tracer name
	 * @return ITrace instance
	 */
	public static ITrace getTracer(String name)
    {
		String defaultName = name;
        if (StringUtils.isBlank(name))
        {
            defaultName = "defaultName";
        }

        ITrace trace = _tracers.get(defaultName);
        if (trace == null)
        {
            trace = new FreewayTracer(defaultName);
            _tracers.put(defaultName, trace);
        }
        return trace;
    }
}
