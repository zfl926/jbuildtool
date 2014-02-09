package com.ctrip.freeway.tracing.impl;

import com.ctrip.freeway.config.ConfigManager;
import com.ctrip.freeway.tracing.ISampler;
import com.ctrip.freeway.util.RandomUtil;

/**
 * @author: huang_jie
 * @date: 3/15/13 5:06 PM
 */
public class UrlLoggingSampler implements ISampler {
    @Override
    public boolean next() {
        return RandomUtil.nextLong() > ConfigManager.getInstance().getUrlLogSampleRate();
    }
}
