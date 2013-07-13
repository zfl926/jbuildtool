package org.comanche.jbuildtool.utils.template;

import java.util.Map;

public interface Template {
	public void create(String tmpPath, String createPath, Map<String, String> datas);
}
