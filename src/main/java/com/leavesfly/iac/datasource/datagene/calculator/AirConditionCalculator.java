package com.leavesfly.iac.datasource.datagene.calculator;

import com.leavesfly.iac.config.AppContextConstant;

/**
 * 空调计算工具类
 * 
 * 该类提供了与空调功率和温度转换相关的核心计算功能，
 * 采用策略模式设计，便于扩展不同的计算算法。
 */
public class AirConditionCalculator {
    
    /**
     * 将功率值映射为温度值
     * 
     * 使用平方根函数进行功率到温度的转换：
     * temperature = MAX_TEMP - sqrt(power)
     * 
     * @param power 功率值
     * @return 对应的温度值
     * @throws IllegalArgumentException 如果功率值超出有效范围
     */
    public static float mapPowerToTemperature(float power) {
        validatePowerRange(power);
        return AppContextConstant.AIR_CONDITION_MAX_TEMP - (float) Math.sqrt(power);
    }
    
    /**
     * 将功率数组映射为效用数组
     * 
     * 效用定义为室外温度与空调产生温度的差值
     * 
     * @param powerArray 功率数组
     * @return 对应的效用数组
     * @throws IllegalArgumentException 如果功率数组为空或包含无效值
     */
    public static Float[] mapPowerToUtilityArray(Float[] powerArray) {
        if (powerArray == null) {
            throw new IllegalArgumentException("功率数组不能为空");
        }
        
        Float[] utilityArray = new Float[powerArray.length];
        
        for (int i = 0; i < powerArray.length; i++) {
            if (powerArray[i] == null) {
                throw new IllegalArgumentException("功率数组中不能包含null值，位置: " + i);
            }
            
            float temperature = mapPowerToTemperature(powerArray[i]);
            utilityArray[i] = AppContextConstant.OUTSIDE_TEMP - temperature;
        }
        
        return utilityArray;
    }
    
    /**
     * 计算平均温度
     * 
     * @param temperatures 温度列表
     * @return 平均温度值
     * @throws IllegalArgumentException 如果温度列表为空
     */
    public static float calculateAverageTemperature(java.util.List<Float> temperatures) {
        if (temperatures == null || temperatures.isEmpty()) {
            throw new IllegalArgumentException("温度列表不能为空");
        }
        
        float sum = 0f;
        int validCount = 0;
        
        for (Float temperature : temperatures) {
            if (temperature != null) {
                sum += temperature;
                validCount++;
            }
        }
        
        if (validCount == 0) {
            throw new IllegalArgumentException("温度列表中没有有效值");
        }
        
        return sum / validCount;
    }
    
    /**
     * 根据目标温度计算所需功率
     * 
     * 使用平方函数进行温度到功率的反向转换：
     * power = (OUTSIDE_TEMP - temperature + ADJUST_FACTOR)²
     * 
     * @param targetTemperature 目标温度
     * @return 所需功率值
     * @throws IllegalArgumentException 如果温度值超出有效范围
     */
    public static float calculateRequiredPower(float targetTemperature) {
        validateTemperatureRange(targetTemperature);
        
        float tempDifference = AppContextConstant.OUTSIDE_TEMP - targetTemperature 
                + AppContextConstant.ABLE_ADJUST_FACTOR;
        
        return (float) Math.pow(tempDifference, 2);
    }
    
    /**
     * 计算功率效率
     * 
     * 效率定义为温度变化与功率消耗的比值
     * 
     * @param powerConsumed 消耗的功率
     * @param temperatureChange 温度变化量
     * @return 功率效率
     */
    public static float calculatePowerEfficiency(float powerConsumed, float temperatureChange) {
        if (powerConsumed <= 0) {
            throw new IllegalArgumentException("消耗功率必须大于0");
        }
        
        return Math.abs(temperatureChange) / powerConsumed;
    }
    
    /**
     * 验证功率值是否在有效范围内
     * 
     * @param power 功率值
     * @throws IllegalArgumentException 如果功率值超出范围
     */
    private static void validatePowerRange(float power) {
        if (power < AppContextConstant.AIR_CONDITION_MIN_POWER 
                || power > AppContextConstant.AIR_CONDITION_MAX_POWER) {
            throw new IllegalArgumentException(String.format(
                    "功率值超出有效范围 [%.1f, %.1f]: %.1f", 
                    AppContextConstant.AIR_CONDITION_MIN_POWER,
                    AppContextConstant.AIR_CONDITION_MAX_POWER,
                    power));
        }
    }
    
    /**
     * 验证温度值是否在有效范围内
     * 
     * @param temperature 温度值
     * @throws IllegalArgumentException 如果温度值超出范围
     */
    private static void validateTemperatureRange(float temperature) {
        if (temperature < AppContextConstant.AIR_CONDITION_MIN_TEMP 
                || temperature > AppContextConstant.AIR_CONDITION_MAX_TEMP) {
            throw new IllegalArgumentException(String.format(
                    "温度值超出有效范围 [%.1f, %.1f]: %.1f", 
                    AppContextConstant.AIR_CONDITION_MIN_TEMP,
                    AppContextConstant.AIR_CONDITION_MAX_TEMP,
                    temperature));
        }
    }
    
    /**
     * 获取功率有效范围
     * 
     * @return 功率范围数组 [最小值, 最大值]
     */
    public static float[] getPowerRange() {
        return new float[] {
            AppContextConstant.AIR_CONDITION_MIN_POWER,
            AppContextConstant.AIR_CONDITION_MAX_POWER
        };
    }
    
    /**
     * 获取温度有效范围
     * 
     * @return 温度范围数组 [最小值, 最大值]
     */
    public static float[] getTemperatureRange() {
        return new float[] {
            AppContextConstant.AIR_CONDITION_MIN_TEMP,
            AppContextConstant.AIR_CONDITION_MAX_TEMP
        };
    }
}