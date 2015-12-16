package com.dr.util.java;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * 
 * @author Verma Rahul
 * 
 */
public class IOUtil {

	/**
	 * 
	 * @param input
	 * @param output
	 * @param buffersize
	 * @throws IOException
	 */
	public static void copy(Reader src, Writer dst, int buffersize)
			throws IOException {
		char[] buffer = new char[buffersize];
		int n = 0;
		while ((n = src.read(buffer)) != -1) {
			dst.write(buffer, 0, n);
		}
	}

	/**
	 * Copy text data from source to destination.
	 * 
	 * @param src
	 * @param dst
	 * @throws IOException
	 */
	public static void copy(Reader src, Writer dst) throws IOException {
		int ch;
		while ((ch = src.read()) != -1) {
			dst.write(ch);
		}
	}

	/**
	 * Copy text data from source to two destinations.
	 * 
	 * @param src
	 * @param dst
	 * @throws IOException
	 */

	/**
	 * Copy text data from source to two destinations.
	 * 
	 * @param src
	 * @param dst
	 * @param dstMem
	 *            can be null, than copy(src,dst) called
	 * @throws IOException
	 */
	public static void copy(Reader src, Writer dst, Writer dstMem)
			throws IOException {
		if (dstMem == null) {
			copy(src, dst);
		} else {
			int ch;
			while ((ch = src.read()) != -1) {
				dst.write(ch);
				dstMem.write(ch);
			}
		}
	}

	/**
	 * Copy binary data from source to destination.
	 * 
	 * @param src
	 * @param dst
	 * @throws IOException
	 */
	public static void copy(InputStream src, OutputStream dst, int buffersize)
			throws IOException {
		byte[] buffer = new byte[buffersize];
		int n = 0;
		while ((n = src.read(buffer)) != -1) {
			dst.write(buffer, 0, n);
		}
	}

	/**
	 * Copy binary data from source to destination.
	 * 
	 * @param src
	 * @param dst
	 * @throws IOException
	 */
	public static void copy(InputStream src, OutputStream dst)
			throws IOException {
		int ch;
		while ((ch = src.read()) != -1) {
			dst.write(ch);
		}
	}

	/**
	 * Copy binary data from source to two destinations.
	 * 
	 * @param src
	 * @param dst
	 * @param dstMem
	 *            can be null, than copy(src,dst) will be called.
	 * @throws IOException
	 */
	public static void copy(InputStream src, OutputStream dst,
			OutputStream dstMem) throws IOException {
		if (dstMem == null) {
			copy(src, dst);
		} else {
			int ch;
			while ((ch = src.read()) != -1) {
				dst.write(ch);
				dstMem.write(ch);
			}
		}
	}
}
