package org.comanche.jbuildtool.project.processor;

import org.comanche.jbuildtool.filesystem.FileNode;
import org.comanche.jbuildtool.filesystem.FileNodeProcessor;
import org.comanche.jbuildtool.filesystem.FileNodeProcessorFactory;
import org.comanche.jbuildtool.filesystem.FileNode.Type;
import org.comanche.jbuildtool.project.Project;
import org.comanche.jbuildtool.project.Project.ProjectType;

public abstract class AbstractProjectProcessor implements ProjectProcessor {

	private ProjectType type;
	private FileNode fileNode;
	
	
	protected ProjectType getType() {
		return type;
	}



	protected void setType(ProjectType type) {
		this.type = type;
	}


	public void processProject(Project project) {
		fileNode = initFileNode(project);
		fileNode = addExtendNode(fileNode);
		FileNodeProcessor fileProcessor = FileNodeProcessorFactory.
				getInstatnce().getProcessor();
		// create all the node
		if ( fileProcessor.process(fileNode) ) {
			
		}
	}
	
	/**
	 * Project node
	 *      src
	 *          main
	 *          	java
	 *         		resource
	 *      	test
	 *              java
	 *        
	 * 
	 * @return
	 */
	protected FileNode initFileNode(Project project){
		// create project name node
		FileNode projectNode = FileNode.createNode(project.getProjectName(), project.getPath(), Type.FOLDER, null);
		// create src
		FileNode srcNode = FileNode.createNode("src", "/src", Type.FOLDER, projectNode);
		// create main
		FileNode mainNode = FileNode.createNode("main", "/main", Type.FOLDER, srcNode);
		// create java
		FileNode.createNode("java", "/java", Type.FOLDER, mainNode);
		// create resource
		FileNode.createNode("resources", "/resources", Type.FOLDER, mainNode);
		// create test
		FileNode testNode = FileNode.createNode("test", "/test", Type.FOLDER, srcNode);
		// create test/java
		FileNode.createNode("java", "/java", Type.FOLDER, testNode);
		// create test/resources
		FileNode.createNode("resources", "/resources", Type.FOLDER, testNode);
		return projectNode;
	}
	
	
	protected abstract FileNode addExtendNode(FileNode node);
	protected abstract void createFileTemplate();

}
