package com.ctrip.freeway.tracing.impl;

import com.ctrip.freeway.tracing.ISampler;

public class AlwaysSampler implements ISampler{
	  public static final AlwaysSampler INSTANCE = new AlwaysSampler(); 
	  
	  private AlwaysSampler() {  }  
	  
	  @Override  
	  public boolean next() {
		  return true;  
	  }
}
