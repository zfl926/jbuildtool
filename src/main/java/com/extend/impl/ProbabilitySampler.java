package com.ctrip.freeway.tracing.impl;

import com.ctrip.freeway.tracing.ISampler;

import java.util.Random;

public class ProbabilitySampler implements ISampler {

	  public final double threshold;  
	  
	  private Random random;  
	  
	  public ProbabilitySampler(double threshold) {    
		  this.threshold = threshold;   
          random = new Random();  
      }  
	  
	  @Override  
	  public boolean next() {    
		  return random.nextDouble() > threshold;  
	  }	
	
}
