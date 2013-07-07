package org.comanche.jbuildtool.project.processor;

import org.comanche.jbuildtool.filesystem.FileNode;
import org.comanche.jbuildtool.project.Project;

public interface ProjectProcessor {
	public FileNode processFolder(Project project);
	public void processTemplate(Project project);
}
