package com.dr.util.java;

import java.io.File;
import java.io.FilenameFilter;

/**
 * 
 * @author Verma Rahul
 *
 */
public final class FilenameFilterExt implements FilenameFilter {
	private String ext;

	public FilenameFilterExt(String ext) {
		this.ext = ext;
	}

	@Override
	public boolean accept(File dir, String filename) {
		return filename.endsWith(ext);
	}

}
