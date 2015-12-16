package com.dr.util.java;

import java.io.File;
import java.util.Comparator;

/**
 * 
 * @author Verma Rahul
 *
 */
public class FilenameAlphanumComparator implements Comparator<File> {

	@Override
	public int compare(File lhs, File rhs) {
		return lhs.getAbsolutePath().compareTo(rhs.getAbsolutePath());
	}

}
