package helpers;

import java.io.File;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.BufferedReader;

public class StickyPathHelper {
	public static int getMethodToCall(File stickyPathDir, String appAlias, int classId, int methodId) {
		File methodToCallFile = getMethodToCallFile(stickyPathDir, appAlias, classId, methodId);
		
		if (methodToCallFile == null) {
			return -1;
		}
		
		if (!methodToCallFile.canRead()) {
			System.out.println("Unable to read " + methodToCallFile);
			return -1;
		}
		
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		
		try {
			fileReader = new FileReader(methodToCallFile);
			bufferedReader = new BufferedReader(fileReader);
			
			String methodToCallLine = bufferedReader.readLine();
			
			if (methodToCallLine == null) {
				return -1;
			}
			
			return Integer.parseInt(methodToCallLine);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
				if (fileReader != null) {
					fileReader.close();
				}
			} catch (Exception e) { }
		}
	}
	
	public static boolean persistMethodToCall(File stickyPathDir, 
		String appAlias, int classId, int methodId, int methodToCall) {
		File methodToCallFile = getMethodToCallFile(stickyPathDir, appAlias, classId, methodId);
		
		if (methodToCallFile == null) {
			return false;
		}
		
		FileOutputStream out = null;
		
		try {
			out = new FileOutputStream(methodToCallFile);
			out.write(Integer.toString(methodToCall).getBytes());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) { }
		}
	}
	
	public static File getMethodToCallFile(File stickyPathDir, 
		String appAlias, int classId, int methodId) {
		File appDir = new File(stickyPathDir, appAlias);
		appDir.mkdirs();
		
		if (!appDir.isDirectory()) {
			System.out.println("App dir is not a directory " + appDir);
			return null;
		}
		
		return new File(appDir, classId + ":" + methodId);
	}
}
