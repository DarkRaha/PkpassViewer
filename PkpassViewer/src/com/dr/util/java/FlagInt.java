package com.dr.util.java;

/**
 * For keeping bits flags and etc. Internally used int type value, so maximum 32 flags. If you need more bits use BitSet.
 * 
 * @author Verma Rahul
 * 
 */
public final class FlagInt {

	private int flags;

	public void setFlag(int i, boolean val) {
		int k = 1 << i;

		if (val) {
			flags = flags | k;
		} else {
			flags = flags & (~k);
		}
	}

	public boolean getFlag(int i) {
		return (flags & (1 << i)) > 0;
	}

	public void fill() {
		flags = 0xffffffff;
	}

	public void clear() {
		flags = 0;
	}

	public int toInt() {
		return flags;
	}

	// ============================================================
	public static int setBit(int src, int i, boolean val) {
		int k = 1 << i;
		if (val) {
			src = src | k;
		} else {
			src = src & (~k);
		}
				
		return src;
	}

	public static boolean getBit(int src, int i) {
		return (src & (1 << i)) > 0;
	}

}
