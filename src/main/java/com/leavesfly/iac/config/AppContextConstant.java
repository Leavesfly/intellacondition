package com.leavesfly.iac.config;

public class AppContextConstant {

	public static final int AREA_LENGTH = 10;
	public static final int AREA_WITCH = 10;

	public static final int USER_NUM = 16;
	public static final int SENSOR_NUM = 10;
	public static final int AIR_CONDITION_NUM = 8;

	public static final String USER_COMFORT_TEMP_DATA_FILE_NAME = "user_comfort_temp.txt";
	public static final String USER_GEO_TABLE_FILE_NAME = "user_geo_table.txt";
	public static final String SENSOR_GEO_TABLE_FILE_NAME = "sensor_geo_table.txt";
	public static final String TRAIN_DATA_FILE_NAME = "power_temp_train_data.txt";

	public static final float COMFORT_MIN_VALUE = 0.2f;
	public static final float COMFORT_UP_DOWN_RANGE_VALUE = 1.5f;
	public static final float MAX_DISTANCE = (float) Math.pow(
			((AREA_LENGTH * AREA_WITCH / SENSOR_NUM) * 3.0f) / Math.PI, 0.5f);

	public static final float SATISFY_WEIGHT = 0.5f;
	public static final float POWER_COST_WEIGHT = 1.0f - SATISFY_WEIGHT;
	public static final float POWER_PRICE = 1.0f;
	public static final float ABLE_ADJUST_FACTOR = 0.25f;

	public static final float OUTSIDE_TEMP = 35.0f;
	public static final float AIR_CONDITION_MIN_TEMP = 15.0f;
	public static final float AIR_CONDITION_MAX_TEMP = OUTSIDE_TEMP;

	public static final float AIR_CONDITION_MIN_POWER = 0.0f;
	public static final float AIR_CONDITION_MAX_POWER = 400.0f;

	public static final int POWER_UTILITY_UNIT = 1_000;

	public static final String SOLUTIN_NAME_PREFIX = "solution_";
	public static final String SOLUTIN_NAME_PSO_SUFFIX = "pso";
	public static final String SOLUTIN_NAME_PSO_CHAOS_SUFFIX = "pso_chaos";

	public static void main(String[] args) {
		System.out.println(MAX_DISTANCE);
	}

}
