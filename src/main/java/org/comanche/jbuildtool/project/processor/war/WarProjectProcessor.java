package org.comanche.jbuildtool.project.processor.war;

import org.comanche.jbuildtool.filesystem.FileNode;
import org.comanche.jbuildtool.filesystem.FileNode.Type;
import org.comanche.jbuildtool.project.processor.AbstractProjectProcessor;

public class WarProjectProcessor extends AbstractProjectProcessor {

	/**
	 * main
	 *      webapp
	 *           WEB-INF
	 */
	@Override
	protected FileNode addExtendNode(FileNode node) {
		FileNode main = FileNode.findNode(node, node.getPath() + "main");
		if ( main != null ){
			FileNode webappNode = FileNode.createNode("webapp","webapp", Type.FOLDER, main);
			FileNode.createNode("WEB-INF","WEB-INF", Type.FOLDER, webappNode);
		}
		
		return node;
	}

	@Override
	protected void createFileTemplate() {
		// TODO Auto-generated method stub

	}

}
