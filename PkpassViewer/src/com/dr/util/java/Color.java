package com.dr.util.java;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO add default alfa value
 * 
 * @author Verma Rahul
 *
 */
public class Color {

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	/**
	 * 255.f/100.f
	 */
	public static final float RGB_PERCENT = 255f / 100f;

		
	public static final String PATTERN_RGB_HEX = "[\\#]{0,1}([a-fA-F0-9]{6}|[a-fA-F0-9]{8})";
	public static final String PATTERN_RGB_HEX_SHORT = "[\\#]{0,1}([a-fA-F0-9]{3}||[a-fA-F0-9]{4})";
	public static final String PATTERN_RGB_CSS = "rgb\\s*\\(\\s*([0-9]{1,3})\\s*,\\s*([0-9]{1,3})\\s*,\\s*([0-9]{1,3})\\s*\\)";
	public static final String PATTERN_RGBA_CSS = "rgba\\s*\\(\\s*([0-9]{1,3})\\s*,\\s*([0-9]{1,3})\\s*,\\s*([0-9]{1,3})\\s*,\\s*(0|1|(1\\.0)|(0\\.0)|(0\\.[0-9]+))\\s*\\)";

	public static int parseColor(Matcher m, int rgba[]) {
		long ret;
		StringBuilder sb;
		String src;
		int srclen;

		switch (m.pattern().pattern()) {
		case PATTERN_RGB_HEX_SHORT:
			src = m.group(1);
			srclen = src.length();
			sb = new StringBuilder();
			char ch;

			for (int i = 0; i < srclen; ++i) {
				ch = src.charAt(i);
				sb.append(ch).append(ch);
			}

			src = sb.toString();
			ret = Long.valueOf(src, 16);
			ret= ret | (0xff << 24);
			return (int) ret;
		
		case PATTERN_RGB_HEX:
			src = m.group(1);
			ret = Long.valueOf(src, 16);
			ret= ret | (0xff << 24);
			return (int) ret;

		

		case PATTERN_RGB_CSS:
			if (rgba == null) {
				rgba = new int[3];
			}
			rgba[0] = (int) (Integer.parseInt(m.group(1)));
			rgba[1] = (int) (Integer.parseInt(m.group(2)));
			rgba[2] = (int) (Integer.parseInt(m.group(3)));
			return 0xff << 24 | rgba[0] << 16 | rgba[1] << 8 | rgba[2];

		case PATTERN_RGBA_CSS:
			if (rgba == null) {
				rgba = new int[4];
			}
			rgba[0] = Integer.parseInt(m.group(1));
			rgba[1] = Integer.parseInt(m.group(2));
			rgba[2] = Integer.parseInt(m.group(3));
			rgba[3] = (int) (Float.parseFloat(m.group(4)) * 100 * RGB_PERCENT);
			return rgba[3] << 24 | rgba[0] << 16 | rgba[1] << 8 | rgba[2];

		default:
		

		}
		throw new RuntimeException("Unknown pattern for parse color: "
				+ m.pattern().pattern());
	}

	public static int parseColorFrom(String src) {
		Pattern patterns[] = { Pattern.compile(PATTERN_RGB_HEX),
				Pattern.compile(PATTERN_RGB_HEX_SHORT),
				Pattern.compile(PATTERN_RGB_CSS),
				Pattern.compile(PATTERN_RGBA_CSS)

		};

		Matcher matcher = patterns[0].matcher(src);

		Pattern p;
		for (int i = 0; i < patterns.length; ++i) {
			p = patterns[i];
			matcher.usePattern(p);
			if (matcher.matches()) {
				return parseColor(matcher, null);
			}
		}

		throw new RuntimeException("No pattern for parse color: "
				+ src);
	
	}

}
