package com.leavesfly.iac.config;

/**
 * 应用上下文常量配置类
 * 
 * 该类定义了智能空调仿真平台的各种配置常量，包括：
 * 1. 环境参数（区域大小、设备数量等）
 * 2. 用户舒适度相关参数
 * 3. 空调设备参数
 * 4. 解决方案命名规则
 */
public class AppContextConstant {

	/**
	 * 区域长度（米）
	 */
	public static final int AREA_LENGTH = 10;
	
	/**
	 * 区域宽度（米）
	 */
	public static final int AREA_WITCH = 10;

	/**
	 * 用户数量
	 */
	public static final int USER_NUM = 16;
	
	/**
	 * 传感器数量
	 */
	public static final int SENSOR_NUM = 10;
	
	/**
	 * 空调数量
	 */
	public static final int AIR_CONDITION_NUM = 8;

	/**
	 * 用户舒适温度数据文件名
	 */
	public static final String USER_COMFORT_TEMP_DATA_FILE_NAME = "user_comfort_temp.txt";
	
	/**
	 * 用户地理位置表文件名
	 */
	public static final String USER_GEO_TABLE_FILE_NAME = "user_geo_table.txt";
	
	/**
	 * 传感器地理位置表文件名
	 */
	public static final String SENSOR_GEO_TABLE_FILE_NAME = "sensor_geo_table.txt";
	
	/**
	 * 功率温度训练数据文件名
	 */
	public static final String TRAIN_DATA_FILE_NAME = "power_temp_train_data.txt";

	/**
	 * 舒适度最小值阈值
	 */
	public static final float COMFORT_MIN_VALUE = 0.2f;
	
	/**
	 * 舒适度上下调整范围值
	 */
	public static final float COMFORT_UP_DOWN_RANGE_VALUE = 1.5f;
	
	/**
	 * 最大距离计算值
	 * 基于区域大小和传感器数量计算得出
	 */
	public static final float MAX_DISTANCE = (float) Math.pow(
			((AREA_LENGTH * AREA_WITCH / SENSOR_NUM) * 3.0f) / Math.PI, 0.5f);

	/**
	 * 满意度权重
	 */
	public static final float SATISFY_WEIGHT = 0.5f;
	
	/**
	 * 功耗成本权重
	 */
	public static final float POWER_COST_WEIGHT = 1.0f - SATISFY_WEIGHT;
	
	/**
	 * 电费单价
	 */
	public static final float POWER_PRICE = 1.0f;
	
	/**
	 * 可调节因子
	 */
	public static final float ABLE_ADJUST_FACTOR = 0.25f;

	/**
	 * 室外温度（摄氏度）
	 */
	public static final float OUTSIDE_TEMP = 35.0f;
	
	/**
	 * 空调最低温度（摄氏度）
	 */
	public static final float AIR_CONDITION_MIN_TEMP = 15.0f;
	
	/**
	 * 空调最高温度（摄氏度）
	 */
	public static final float AIR_CONDITION_MAX_TEMP = OUTSIDE_TEMP;

	/**
	 * 空调最小功率
	 */
	public static final float AIR_CONDITION_MIN_POWER = 0.0f;
	
	/**
	 * 空调最大功率
	 */
	public static final float AIR_CONDITION_MAX_POWER = 400.0f;

	/**
	 * 功率效用单位
	 */
	public static final int POWER_UTILITY_UNIT = 1_000;

	/**
	 * 解决方案名称前缀
	 */
	public static final String SOLUTIN_NAME_PREFIX = "solution_";
	
	/**
	 * PSO算法解决方案后缀
	 */
	public static final String SOLUTIN_NAME_PSO_SUFFIX = "pso";
	
	/**
	 * 混沌PSO算法解决方案后缀
	 */
	public static final String SOLUTIN_NAME_PSO_CHAOS_SUFFIX = "pso_chaos";

	/**
	 * 主函数，用于测试常量值
	 * 
	 * @param args 命令行参数
	 */
	public static void main(String[] args) {
		System.out.println(MAX_DISTANCE);
	}

}