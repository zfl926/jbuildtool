package com.ctrip.freeway.tracing.impl;

import com.ctrip.freeway.tracing.ISampler;

import java.util.Random;

public class CountSampler implements ISampler {
	  final static Random random = new Random();  
	  
	  final long frequency;  
	  long count = random.nextLong();  
	  
	  public CountSampler(long frequency) {    
		  this.frequency = frequency;  
	  }  
	  
	  @Override  
	  public boolean next() {    
		  return (count++ % frequency) == 0;  
	  }
}
