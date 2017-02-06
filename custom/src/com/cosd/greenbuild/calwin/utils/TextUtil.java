package com.cosd.greenbuild.calwin.utils;

import java.text.DecimalFormat;

public class TextUtil {
	private static final long K = 1024;
	private static final long M = K * K;
	private static final long G = M * K;
	private static final long T = G * K;
	private static final DecimalFormat FMT_LEN = new DecimalFormat("#,##0.#");

	/**
	 * Formats a length of bytes to string representation.  ie 1024 == 1K
	 *  
	 * @param value
	 * @return
	 */
	public static String formatLength(final long value) {
		final long[] dividers = new long[] { T, G, M, K, 1 };
		final String[] units = new String[] { "TB", "GB", "MB", "KB", "B" };
		if (value < 0)
			throw new IllegalArgumentException("Invalid file size: " + value);
		String result = null;
		for (int i = 0; i < dividers.length; i++) {
			final long divider = dividers[i];
			if (value >= divider) {
				result = formatLength(value, divider, units[i]);
				break;
			}
		}
		return result;
	}

	private static String formatLength(final long value, final long divider, final String unit) {
		final double result = divider > 1 ? (double) value / (double) divider : (double) value;
		return FMT_LEN.format(result) + unit;
	}

	/**
	 * Converts a string to a length.  Valid strings are doubles followed by
	 * B, KB,MB,GB or TB.  ie 23.4GB, 123.45 TB.  Distinction between upper and lower
	 * is not made.
	 *  
	 * @param lenStr
	 * @return
	 */
	public static long stringToLength(String lenStr) {
		lenStr = lenStr.toUpperCase();
		long mult = 1;
		if (lenStr.endsWith("KB"))
			mult = K;
		else if (lenStr.endsWith("MB"))
			mult = M;
		else if (lenStr.endsWith("GB"))
			mult = G;
		else if (lenStr.endsWith("TB"))
			mult = T;
		else if (lenStr.endsWith("B"))
			mult = 1;

		if (mult != 1) {
			lenStr = lenStr.substring(0, lenStr.length() - 2);
			lenStr.trim();
		}

		double val = Double.parseDouble(lenStr);
		long lv = (long) (val * (mult));
		return lv;
	}

}
