package com.ctrip.freeway.tracing.impl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestCountSampler {

	@Test
	public void testNext() {
		CountSampler half = new CountSampler(2);
		CountSampler hundred = new CountSampler(100);
		int halfCount = 0;
		int hundredCount = 0;
		for(int i = 0; i < 200; i++) {
			if (half.next())
				halfCount++;
			if (hundred.next())
				hundredCount++;
		}
		assertEquals(2, hundredCount);
		assertEquals(100, halfCount);
	}
}
