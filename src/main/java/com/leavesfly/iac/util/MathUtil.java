package com.leavesfly.iac.util;

import org.apache.commons.lang.math.RandomUtils;

import com.leavesfly.iac.config.BpAlgorithmConstant;
import com.leavesfly.iac.domain.PowerRange;

public class MathUtil {

	public static float nextFloat(float from, float to) {
		return from + (to - from) * RandomUtils.nextFloat();
	}

	public static int nextInt(int from, int to) {
		return from + (int) ((to - from) * RandomUtils.nextFloat());
	}

	public static void main(String[] args) {
		// System.out.println(MathUtil.nextFloat(
		// BpAlgorithmConstant.WEIGHT_RANDOM_FROM,
		// BpAlgorithmConstant.WEIGHT_RANDOM_TO));
		// System.out.println(MathUtil.nextFloat(
		// BpAlgorithmConstant.WEIGHT_RANDOM_FROM,
		// BpAlgorithmConstant.WEIGHT_RANDOM_TO));
		// for (int i = 0; i < 10; i++) {
		// System.out.println(MathUtil.nextFloat(-10, 10));
		// }

		PowerRange[] powerRangeArray = new PowerRange[2];
		powerRangeArray[0] = new PowerRange(-10.0f, 10.0f);
		powerRangeArray[1] = new PowerRange(-10.0f, 10.0f);
		System.out.println(powerRangeArray[0].getFrom());
		System.out.println(powerRangeArray[0].getTo());
		System.out.println(powerRangeArray[0].genInitValue().getValue());
		//System.out.println(Float.parseFloat("-5.9049027E-5")+10);
	}
}
