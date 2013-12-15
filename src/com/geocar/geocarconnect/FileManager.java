package com.geocar.geocarconnect;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.os.Environment;

public class FileManager {
	private static final String SUB_DIR = "GPS";
	
	public static ArrayList<FileInfo> getFileList() {
		ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();
		return fileList;
	}

	public static BufferedWriter getNewOutputFile() {
		return getNewOutputFile("GPS.txt"); 
	}

	public static BufferedWriter getNewOutputFile(String fileName) {
		BufferedWriter out = null;
		String fullPath = Environment.getExternalStorageDirectory() 
			+ "/" + SUB_DIR + "/" + fileName;
		File file = new File(fullPath);
			
		try {
			if(!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			out = new BufferedWriter( new OutputStreamWriter(new FileOutputStream(file)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}

	public static boolean writeFileToSDCard( BufferedWriter out, String data) {
		try {
			out.write(data);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean close( BufferedWriter out ){
		try {
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean close( BufferedReader in ){
		try {
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static BufferedReader getInputFile(String fileName) {
		BufferedReader in = null;
		String fullPath = Environment.getExternalStorageDirectory() 
			+ "/" + SUB_DIR + "/" + fileName;
		File file = new File(fullPath);
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return in;
	}
}
