package org.comanche.jbuildtool.project.processor;

import org.comanche.jbuildtool.project.Project;

public class ProjectProcessorFactory {
	private static final ProjectProcessorFactory INSTANCE = new ProjectProcessorFactory();
	
	private ProjectProcessorFactory(){
		
	}
	
	public static ProjectProcessorFactory getInstance(){
		return INSTANCE;
	}
	
	public ProjectProcessor getProcessor(Project project){
		return null;
	}
	
}
