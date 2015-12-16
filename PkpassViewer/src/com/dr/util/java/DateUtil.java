package com.dr.util.java;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author Verma Rahul
 * 
 */
public class DateUtil {

	/**
	 * 
	 */
	public final static String DATE_DOT = "dd.MM.yyyy";

	/**
	 * 
	 */
	public final static String DATE_SLASH = "dd/MM/yyyy";

	/**
	 * 
	 */
	public final static String DATTIME_DOT = "dd.MM.yyyy hh:mm.ss";

	/**
	 * example 2012-07-22T14:25-08:00
	 */
	public final static String DATE_USA_ZONE = "yyyy-MM-dd'T'hh:mmXXX";

	// By standard first must go year than month than day, but for user friendly
	// sometime input will be in back order .
	protected static String parsePatterns[] = new String[] {

			// TODO проверить то что y.M.d'T'hh:mm.ssZ включает более мелкие как
			// y.M.d
			"y-M-d'T'hh:mm.ssZ", //
			"y.M.d'T'hh:mm.ssZ",//
			"y/M/d'T'hh:mm.ssZ",//

			"y-M-d'T'hh:mmZ", //
			"y.M.d'T'hh:mmZ",//
			"y/M/d'T'hh:mmZ",//

			"d.M.y", "y.M.d", "M/y", "d/M/y", "y/M/d", "d-M-y", "y-M-d"

	};

	/**
	 * 
	 * @param fromPattern
	 * @param toPattern
	 * @param src
	 * @return
	 * @throws ParseException
	 */
	public static String convert(String fromPattern, String toPattern,
			String src) throws ParseException {
		SimpleDateFormat fromSdf = new SimpleDateFormat(fromPattern);
		SimpleDateFormat toSdf = new SimpleDateFormat(toPattern);
		return toSdf.format(fromSdf.parse(src));
	}

	/**
	 * 
	 * @param fromPattern
	 * @param toPattern
	 * @param source
	 * @return null if can not convert
	 */
	public static String convert(String fromPattern[], String toPattern,
			String source) {

		SimpleDateFormat fromSdf = new SimpleDateFormat();
		SimpleDateFormat toSdf = new SimpleDateFormat(toPattern);
		String ret;

		for (String from : fromPattern) {

			fromSdf.applyLocalizedPattern(from);

			try {
				ret = toSdf.format(fromSdf.parse(source));
			} catch (Exception e) {
				ret = null;
			}

			if (ret != null) {
				return ret;
			}
		}

		return null;
	}

	/**
	 * Try parse date from string by using inner array of patterns.
	 * 
	 * 
	 * @param src
	 * @return null if can not parse
	 * 
	 */
	public static Date parse(String src) {

		SimpleDateFormat sdf = new SimpleDateFormat();
		Date date;

		for (String pattern : parsePatterns) {
			//System.out.println("DateUtil parse " + pattern);
			sdf.applyPattern(pattern);

			try {
				date = sdf.parse(src);
			} catch (Exception e) {
				date = null;
			}

			if (date != null) {
				return date;
			}
		}

		return null;
	}

	/**
	 * 
	 * @param toPattern
	 * @param src
	 * @return null if can not convert
	 */
	public static String convert(String toPattern, String src) {
		Date date = parse(src);
		return date == null ? null : (new SimpleDateFormat(toPattern))
				.format(date);
	}
}
