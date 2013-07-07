package org.comanche.jbuildtool.filesystem;

import java.io.File;

import org.comanche.jbuildtool.filesystem.FileNode.Type;
import org.junit.Before;
import org.junit.Test;

public class FileNodeProcessorTest {

	private final FileNodeProcessor processor = new FileNodeProcessorImpl();
	private FileNode node = new FileNode();
	
	@Before
	public void beforeTest(){
		
		node.setName("folderRooter");
		node.setPath("D:\\test");
		node.setType(Type.FOLDER);
		
		FileNode subFolder = new FileNode();
		subFolder.setType(Type.FOLDER);
		subFolder.setParentNode(node);
		subFolder.setName("subFolder");
		subFolder.setPath(node.getPath() + File.separator + node.getName());
		node.getChilds().add(subFolder);
		
		FileNode subFileNode = new FileNode();
		subFileNode.setType(Type.FILE);
		subFileNode.setName("test.txt");
		subFileNode.setParentNode(node);
		subFileNode.setPath(node.getPath() + File.separator + node.getName());
		node.getChilds().add(subFileNode);
		
		
	}
	
	@Test
	public void testProcess(){
		processor.process(node);
	}
	
}
