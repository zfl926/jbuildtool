package org.comanche.jbuildtool.project;

import org.comanche.jbuildtool.project.processor.ProjectProcessor;
import org.comanche.jbuildtool.project.processor.ProjectProcessorFactory;

public class ProjectBuilder {

	public void build(Project project){
		//create folder
		ProjectProcessor processor = ProjectProcessorFactory.
				getInstance().getProcessor(project);
		
		processor.processProject(project);
		
		
	}
	
	
	
}
