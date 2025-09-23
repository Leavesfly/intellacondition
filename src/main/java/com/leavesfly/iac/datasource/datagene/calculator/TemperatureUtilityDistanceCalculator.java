package com.leavesfly.iac.datasource.datagene.calculator;

import com.leavesfly.iac.config.AppContextConstant;

/**
 * 温度效用距离计算器
 * 
 * 该类负责根据距离计算温度效用的衰减，使用科学的距离衰减模型。
 * 采用策略模式设计，便于扩展不同的衰减算法。
 */
public class TemperatureUtilityDistanceCalculator {
    
    /**
     * 距离衰减参数
     */
    private static final float DISTANCE_DECAY_FACTOR = 8.0f;
    
    /**
     * 效用放大因子
     */
    private static final float UTILITY_AMPLIFICATION_FACTOR = 8.0f;
    
    /**
     * 最大允许距离
     */
    private static final float MAX_ALLOWED_DISTANCE = 14.0f;
    
    /**
     * 最大允许效用值
     */
    private static final float MAX_ALLOWED_UTILITY = AppContextConstant.AIR_CONDITION_MAX_TEMP 
            - AppContextConstant.AIR_CONDITION_MIN_TEMP;
    
    /**
     * 根据距离计算温度效用
     * 
     * 使用距离衰减公式：utility = (AMPLIFICATION_FACTOR * rawUtility) / (distance + DECAY_FACTOR)
     * 
     * @param rawUtility 原始效用值
     * @param distance 距离
     * @return 经过距离衰减后的温度效用
     * @throws IllegalArgumentException 如果参数超出有效范围
     */
    public static float calculateTemperatureUtilityByDistance(float rawUtility, float distance) {
        validateUtility(rawUtility);
        validateDistance(distance);
        
        return (UTILITY_AMPLIFICATION_FACTOR * rawUtility) / (distance + DISTANCE_DECAY_FACTOR);
    }
    
    /**
     * 根据多个效用值和对应距离计算总效用
     * 
     * @param rawUtilityArray 原始效用数组
     * @param distanceArray 距离数组
     * @return 总效用值
     * @throws IllegalArgumentException 如果数组为空、长度不匹配或包含无效值
     */
    public static float calculateCombinedUtilityByDistanceArray(Float[] rawUtilityArray, Float[] distanceArray) {
        validateArrays(rawUtilityArray, distanceArray);
        
        int size = rawUtilityArray.length;
        float[] adjustedUtilityArray = new float[size];
        
        // 计算每个位置的调整后效用
        for (int i = 0; i < size; i++) {
            adjustedUtilityArray[i] = calculateTemperatureUtilityByDistance(
                    rawUtilityArray[i], distanceArray[i]);
        }
        
        // 合并效用值
        return combineUtilities(adjustedUtilityArray);
    }
    
    /**
     * 合并多个效用值
     * 
     * 使用改进的合并算法，考虑最大值和平均值的关系
     * 
     * @param utilityArray 效用数组
     * @return 合并后的效用值
     */
    private static float combineUtilities(float[] utilityArray) {
        if (utilityArray.length == 0) {
            return 0.0f;
        }
        
        if (utilityArray.length == 1) {
            return utilityArray[0];
        }
        
        float maxUtility = 0.0f;
        float totalUtility = 0.0f;
        
        // 计算最大值和总和
        for (float utility : utilityArray) {
            maxUtility = Math.max(maxUtility, utility);
            totalUtility += utility;
        }
        
        float averageUtility = totalUtility / utilityArray.length;
        float difference = maxUtility - averageUtility;
        
        // 使用非线性合并函数
        if (difference >= 1.0f) {
            float adjustedDifference = (float) Math.pow(difference, 0.25);
            return maxUtility - adjustedDifference + 1.0f;
        } else {
            return maxUtility;
        }
    }
    
    /**
     * 计算距离衰减因子
     * 
     * @param distance 距离
     * @return 衰减因子 (0-1之间)
     */
    public static float calculateDistanceDecayFactor(float distance) {
        validateDistance(distance);
        return DISTANCE_DECAY_FACTOR / (distance + DISTANCE_DECAY_FACTOR);
    }
    
    /**
     * 计算有效影响半径
     * 
     * @param minimumUtilityRatio 最小效用比率 (0-1之间)
     * @return 有效影响半径
     */
    public static float calculateEffectiveRadius(float minimumUtilityRatio) {
        if (minimumUtilityRatio <= 0 || minimumUtilityRatio >= 1) {
            throw new IllegalArgumentException("最小效用比率必须在(0,1)范围内: " + minimumUtilityRatio);
        }
        
        // 根据衰减公式反推距离
        return DISTANCE_DECAY_FACTOR * ((1.0f / minimumUtilityRatio) - 1.0f);
    }
    
    /**
     * 批量计算温度效用
     * 
     * @param utilities 效用列表
     * @param distances 距离列表
     * @return 调整后的效用列表
     */
    public static java.util.List<Float> calculateBatchUtilities(
            java.util.List<Float> utilities, java.util.List<Float> distances) {
        
        if (utilities == null || distances == null) {
            throw new IllegalArgumentException("效用列表和距离列表不能为空");
        }
        
        if (utilities.size() != distances.size()) {
            throw new IllegalArgumentException("效用列表和距离列表长度必须相同");
        }
        
        java.util.List<Float> results = new java.util.ArrayList<>(utilities.size());
        
        for (int i = 0; i < utilities.size(); i++) {
            Float utility = utilities.get(i);
            Float distance = distances.get(i);
            
            if (utility == null || distance == null) {
                throw new IllegalArgumentException("效用值和距离值不能为null，位置: " + i);
            }
            
            float adjustedUtility = calculateTemperatureUtilityByDistance(utility, distance);
            results.add(adjustedUtility);
        }
        
        return results;
    }
    
    /**
     * 验证效用值
     * 
     * @param utility 效用值
     * @throws IllegalArgumentException 如果效用值无效
     */
    private static void validateUtility(float utility) {
        if (utility < 0 || utility > MAX_ALLOWED_UTILITY) {
            throw new IllegalArgumentException(String.format(
                    "效用值超出有效范围 [0, %.1f]: %.1f", 
                    MAX_ALLOWED_UTILITY, utility));
        }
    }
    
    /**
     * 验证距离值
     * 
     * @param distance 距离值
     * @throws IllegalArgumentException 如果距离值无效
     */
    private static void validateDistance(float distance) {
        if (distance < 0 || distance > MAX_ALLOWED_DISTANCE) {
            throw new IllegalArgumentException(String.format(
                    "距离值超出有效范围 [0, %.1f]: %.1f", 
                    MAX_ALLOWED_DISTANCE, distance));
        }
    }
    
    /**
     * 验证数组参数
     * 
     * @param rawUtilityArray 原始效用数组
     * @param distanceArray 距离数组
     * @throws IllegalArgumentException 如果数组无效
     */
    private static void validateArrays(Float[] rawUtilityArray, Float[] distanceArray) {
        if (rawUtilityArray == null || distanceArray == null) {
            throw new IllegalArgumentException("效用数组和距离数组不能为空");
        }
        
        if (rawUtilityArray.length != distanceArray.length) {
            throw new IllegalArgumentException(String.format(
                    "效用数组和距离数组长度必须相同: %d vs %d", 
                    rawUtilityArray.length, distanceArray.length));
        }
        
        if (rawUtilityArray.length == 0) {
            throw new IllegalArgumentException("数组长度不能为0");
        }
        
        // 验证数组中的每个值
        for (int i = 0; i < rawUtilityArray.length; i++) {
            if (rawUtilityArray[i] == null || distanceArray[i] == null) {
                throw new IllegalArgumentException("数组中不能包含null值，位置: " + i);
            }
        }
    }
    
    /**
     * 获取距离衰减因子
     * 
     * @return 距离衰减因子
     */
    public static float getDistanceDecayFactor() {
        return DISTANCE_DECAY_FACTOR;
    }
    
    /**
     * 获取效用放大因子
     * 
     * @return 效用放大因子
     */
    public static float getUtilityAmplificationFactor() {
        return UTILITY_AMPLIFICATION_FACTOR;
    }
    
    /**
     * 获取最大允许距离
     * 
     * @return 最大允许距离
     */
    public static float getMaxAllowedDistance() {
        return MAX_ALLOWED_DISTANCE;
    }
}