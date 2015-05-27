package com.leavesfly.iac.datasource.datagene;

import com.leavesfly.iac.config.AppContextConstant;

public class TempUtilityDistanceUtil {

	public static float calcTempUtilityByDistance(float rawUtility, float distance) {
		if (rawUtility > AppContextConstant.AIR_CONDITION_MAX_TEMP
				- AppContextConstant.AIR_CONDITION_MIN_TEMP
				|| rawUtility < 0 || distance > 14 || distance < 0) {
			throw new IllegalArgumentException();
		}
		return (float) ((8.0f * rawUtility) / (distance + 8.0f));
	}

	public static float calcTemUtilByDistanceArray(Float[] rawUtilityArray, Float[] distances) {
		if (rawUtilityArray == null || distances == null
				|| rawUtilityArray.length != distances.length) {
			throw new IllegalArgumentException();
		}

		float result = 0.0f;
		int size = rawUtilityArray.length;
		float[] utilityArray = new float[size];
		for (int i = 0; i < size; i++) {
			utilityArray[i] = calcTempUtilityByDistance(rawUtilityArray[i], distances[i]);
		}

		result = plusUtility(utilityArray);
		return result;
	}

	private static float plusUtility(float[] utilityArray) {

		float result = 0.0f;
		float maxUtility = 0.0f;
		float averageUtility = 0.0f;

		int size = utilityArray.length;
		for (int i = 0; i < size; i++) {
			if (maxUtility < utilityArray[i]) {
				maxUtility = utilityArray[i];
			}
			averageUtility += utilityArray[i];
		}
		averageUtility /= size;
		float difference = maxUtility - averageUtility;

		if (difference >= 1) {
			result = (float) (maxUtility - Math.pow((maxUtility - averageUtility), 0.25d) + 1);
		} else {
			result = maxUtility;
		}
		result = maxUtility;
		return result;
	}

	public static void main(String[] args) {
		System.out.println(Math.pow((5), 0.2d));
		// System.out.println(Math.pow(8000, 0.5d));
	}

}
