package org.comanche.jbuildtool.filesystem;

import java.io.File;

import org.comanche.jbuildtool.utils.FileUtils;

public class FileNodeProcessorImpl implements FileNodeProcessor{

	public boolean process(FileNode node) {
		checkFileNode(node);
		
		String fileName = node.getName();
		switch ( node.getType() ){
		case FILE:
			FileUtils.createFile(node.getPath() + File.separator + fileName, true);
			break;
		case FOLDER:
			FileUtils.createFolder(node.getPath() + File.separator + fileName, false);
			break;
		}
		
		
		for ( FileNode fileNode : node.getChilds() ){
			process(fileNode);
		}
		
		return true;
	}
	
	
	private void checkFileNode(FileNode node){
		if ( node == null ){
			throw new IllegalArgumentException();
		}
		
		if ( node.getName() == null ){
			throw new IllegalArgumentException();
		}
	}

}
