package com.ctrip.freeway.tracing.impl;

import com.ctrip.freeway.tracing.ISampler;

public final class NeverSampler implements ISampler {
	public static final NeverSampler INSTANCE = new NeverSampler();  
	
	private NeverSampler() {  }  
	
	@Override  
	public boolean next() {
		return false;  
	}
} 

