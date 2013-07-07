package org.comanche.jbuildtool.project;

import org.comanche.jbuildtool.filesystem.FileNode;
import org.comanche.jbuildtool.filesystem.FileNodeProcessor;
import org.comanche.jbuildtool.filesystem.FileNodeProcessorFactory;
import org.comanche.jbuildtool.project.processor.ProjectProcessor;
import org.comanche.jbuildtool.project.processor.ProjectProcessorFactory;

public class ProjectBuilder {

	public void build(Project project){
		//1. create folder
		ProjectProcessor processor = ProjectProcessorFactory.
				getInstance().getProcessor(project);
		
		FileNode node =processor.processFolder(project);
		FileNodeProcessor fileProcessor = FileNodeProcessorFactory.
				getInstatnce().getProcessor();
		if ( fileProcessor.process(node) ){
			// add some template
			
		}
		
	}
	
	
	
}
