package com.dr.util.java;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * 
 * @author Verma Rahul
 *
 */
public class FileUtil {

	/**
	 * Copy content from one file to destination file.
	 * 
	 * @param src
	 * @param dst
	 * @return
	 */
	public static boolean copy(File src, File dst) {
		if(src.equals(dst)){
			return false;
		}
		
		try {
			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(src));
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(dst));
			IOUtil.copy(bis, bos);
			bis.close();
			bos.close();
		} catch (Exception e) {			
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 *
	 * @param is
	 * @param dst
	 * @return
	 */
	public static boolean save(InputStream is, File dst){

		try {
			BufferedInputStream bis = new BufferedInputStream(
					is);
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(dst));
			IOUtil.copy(bis, bos);
			bis.close();
			bos.close();

		}catch(Exception e){
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
