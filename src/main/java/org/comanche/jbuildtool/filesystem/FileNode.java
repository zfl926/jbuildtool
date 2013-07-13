package org.comanche.jbuildtool.filesystem;

import java.util.ArrayList;
import java.util.List;

public class FileNode {
	public enum Type { FILE, FOLDER }
	
	private FileNode parentNode;
	private String name;
	private String path;
	private Type type;
	private List<FileNode> childs = new ArrayList<FileNode>();
	
	public FileNode getParentNode() {
		return parentNode;
	}
	public void setParentNode(FileNode parentNode) {
		this.parentNode = parentNode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public List<FileNode> getChilds() {
		return childs;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

	public static FileNode createNode(String name, String relativePath, Type type, FileNode parent){
		if ( name == null || relativePath == null || type == null ){
			throw new IllegalArgumentException();
		}
		
		FileNode node = new FileNode();
		node.setName(name);
		if ( parent != null){
			node.setPath(parent.getPath() + "/" + relativePath);
			parent.getChilds().add(node);
		}
		else{
			node.setPath(relativePath);
		}
		node.setParentNode(parent);
		node.setType(type);
		
		return node;
	}
	
	
}
