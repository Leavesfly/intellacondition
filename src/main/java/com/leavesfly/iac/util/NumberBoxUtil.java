package com.leavesfly.iac.util;

public class NumberBoxUtil {

	/**
	 * 例如：float[]-> Float[]; double[]->Double[] int[]->Integer[]
	 * short[]->Short[]
	 */
	public static <T> Object numUnboxArray2Box(T numUnboxArray) {
		
		return null;
	}

	public static void main(String[] args) {
		System.out.println(new Float[1].getClass().getName());
		System.out.println(new float[1].getClass().getName());

	}
}
