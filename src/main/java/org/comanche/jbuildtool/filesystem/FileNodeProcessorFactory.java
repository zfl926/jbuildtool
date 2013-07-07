package org.comanche.jbuildtool.filesystem;

public class FileNodeProcessorFactory {
	private static FileNodeProcessorFactory INSTANCE = new FileNodeProcessorFactory();
	
	private FileNodeProcessorFactory(){
		
	}
	
	public static FileNodeProcessorFactory getInstatnce(){
		return INSTANCE;
	}
	
	public FileNodeProcessor getProcessor(){
		return new FileNodeProcessorImpl();
	}
	
}
