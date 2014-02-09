package com.ctrip.freeway.tracing;

import com.ctrip.freeway.tracing.impl.AlwaysSampler;
import com.ctrip.freeway.tracing.impl.NeverSampler;

/** 
 * 用于确定采样频率的简单接口。
 * 
 *  next方法的样例：
 *  
 *  private SomeType info;
 *  
 *  public boolean next(){ 
 *      if (info == null) { 
 *          return false; 
 *      } else if (info.getName().equals("get")) { 
 *          return Math.random() > 0.5; 
 *      } else if (info.getName().equals("put")) { 
 *          return Math.random() > 0.25;     
 *      } else {   
 *          return false; 
 *      } 
 *  } 
 * 
 * 以上实现将trace 50%的gets，75%的所有puts，而对其它的请求不作trace.
 */
public interface ISampler {

	public static final ISampler ALWAYS = AlwaysSampler.INSTANCE;
	public static final ISampler NEVER = NeverSampler.INSTANCE;
	
	/**
	 * 确定是否要对一个span进行trace
	 * @return true or false
	 */
	public boolean next();
	
}
