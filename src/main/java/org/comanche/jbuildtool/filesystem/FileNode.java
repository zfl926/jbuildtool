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

}
