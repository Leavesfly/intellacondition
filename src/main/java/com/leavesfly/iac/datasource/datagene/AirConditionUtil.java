package com.leavesfly.iac.datasource.datagene;

import java.util.List;

import com.leavesfly.iac.config.AppContextConstant;

public class AirConditionUtil {

	public static float mapPower2Temp(float power) {
		if (power > AppContextConstant.AIR_CONDITION_MAX_POWER
				|| power < AppContextConstant.AIR_CONDITION_MIN_POWER) {
			throw new IllegalArgumentException();
		}
		return AppContextConstant.AIR_CONDITION_MAX_TEMP - (float) Math.sqrt(power);
	}

	public static Float[] mapPower2UtilityByArray(Float[] powerArray) {
		if (powerArray == null) {
			throw new IllegalArgumentException();
		}
		Float[] result = new Float[powerArray.length];

		for (int i = 0; i < powerArray.length; i++) {
			result[i] = AppContextConstant.OUTSIDE_TEMP - mapPower2Temp(powerArray[i]);
		}
		return result;
	}

	public static float averageAroundTemp(List<Float> aroundTemps) {
		if (aroundTemps.isEmpty()) {
			throw new IllegalArgumentException();
		}
		float result = 0f;
		for (float temp : aroundTemps) {
			result += temp;
		}
		result /= aroundTemps.size();
		return result;
	}

	public static float costPowerWhenTemp(float temp) {
		if (temp > AppContextConstant.AIR_CONDITION_MAX_TEMP
				|| temp < AppContextConstant.AIR_CONDITION_MIN_TEMP) {
			throw new IllegalArgumentException();
		}
		return (float) Math.pow(AppContextConstant.OUTSIDE_TEMP - temp
				+ AppContextConstant.ABLE_ADJUST_FACTOR, 2);
	}

}
