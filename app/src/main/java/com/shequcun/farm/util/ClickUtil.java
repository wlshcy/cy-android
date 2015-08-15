package com.shequcun.farm.util;

public class ClickUtil {

	private static long mLastClickTime;

	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		if (time - mLastClickTime < 500) {
			return true;
		}
		mLastClickTime = time;
		return false;
	}

}
