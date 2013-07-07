package org.comanche.jbuildtool.utils;

import java.io.File;
import java.io.IOException;

public class FileUtils {
	public static boolean createFile(String fullName, boolean isOverWritten){
		if ( fullName == null || fullName.trim().equals("") ) {
			return false;
		}
		
		File file = new File(fullName);
		if ( file.exists() ){
			if ( isOverWritten == false ){
				return true;
			}
		}
		
		try {
			file.createNewFile();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static boolean createFolder(String fullName, boolean createNew){
		if ( fullName == null || fullName.trim().equals("") ) {
			return false;
		}
		
		File folder = new File(fullName);		
		if ( folder.exists() && folder.isDirectory() ){
			if ( createNew ){
				return false;
			}
			
			return true;
		} else {
			return folder.mkdirs();
		
		}
		
	}
}
