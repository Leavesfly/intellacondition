package com.leavesfly.iac.datasource.datagene.calculator;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.leavesfly.iac.config.AppContextConstant;

/**
 * 空调计算工具测试类
 */
public class AirConditionCalculatorTest {
    
    @Test
    public void testMapPowerToTemperature() {
        // 测试功率到温度的映射
        float power = 100.0f;
        float temperature = AirConditionCalculator.mapPowerToTemperature(power);
        
        // 验证计算结果合理性
        assertTrue("计算的温度应在合理范围内", 
                temperature >= AppContextConstant.AIR_CONDITION_MIN_TEMP 
                && temperature <= AppContextConstant.AIR_CONDITION_MAX_TEMP);
        
        // 验证计算公式正确性
        float expectedTemperature = AppContextConstant.AIR_CONDITION_MAX_TEMP - (float) Math.sqrt(power);
        assertEquals("计算结果应符合公式", expectedTemperature, temperature, 0.01f);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testMapPowerToTemperatureInvalidPower() {
        // 测试无效功率值
        float invalidPower = AppContextConstant.AIR_CONDITION_MAX_POWER + 100;
        AirConditionCalculator.mapPowerToTemperature(invalidPower);
    }
    
    @Test
    public void testMapPowerToUtilityArray() {
        // 测试功率数组到效用数组的映射
        Float[] powerArray = {100.0f, 200.0f, 300.0f};
        Float[] utilityArray = AirConditionCalculator.mapPowerToUtilityArray(powerArray);
        
        assertNotNull("效用数组不应为空", utilityArray);
        assertEquals("效用数组长度应与功率数组相同", powerArray.length, utilityArray.length);
        
        // 验证每个效用值的计算正确性
        for (int i = 0; i < powerArray.length; i++) {
            float expectedTemperature = AirConditionCalculator.mapPowerToTemperature(powerArray[i]);
            float expectedUtility = AppContextConstant.OUTSIDE_TEMP - expectedTemperature;
            assertEquals("效用值计算应正确", expectedUtility, utilityArray[i], 0.01f);
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testMapPowerToUtilityArrayNullInput() {
        // 测试空输入
        AirConditionCalculator.mapPowerToUtilityArray(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testMapPowerToUtilityArrayNullElement() {
        // 测试包含null元素的数组
        Float[] powerArray = {100.0f, null, 300.0f};
        AirConditionCalculator.mapPowerToUtilityArray(powerArray);
    }
    
    @Test
    public void testCalculateAverageTemperature() {
        // 测试平均温度计算
        List<Float> temperatures = Arrays.asList(20.0f, 22.0f, 24.0f, 26.0f);
        float averageTemperature = AirConditionCalculator.calculateAverageTemperature(temperatures);
        
        float expectedAverage = (20.0f + 22.0f + 24.0f + 26.0f) / 4;
        assertEquals("平均温度计算应正确", expectedAverage, averageTemperature, 0.01f);
    }
    
    @Test
    public void testCalculateAverageTemperatureWithNull() {
        // 测试包含null值的温度列表
        List<Float> temperatures = Arrays.asList(20.0f, null, 24.0f, 26.0f);
        float averageTemperature = AirConditionCalculator.calculateAverageTemperature(temperatures);
        
        float expectedAverage = (20.0f + 24.0f + 26.0f) / 3; // 忽略null值
        assertEquals("含null的平均温度计算应正确", expectedAverage, averageTemperature, 0.01f);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCalculateAverageTemperatureEmptyList() {
        // 测试空列表
        List<Float> emptyList = Arrays.asList();
        AirConditionCalculator.calculateAverageTemperature(emptyList);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCalculateAverageTemperatureAllNull() {
        // 测试全null列表
        List<Float> allNullList = Arrays.asList(null, null, null);
        AirConditionCalculator.calculateAverageTemperature(allNullList);
    }
    
    @Test
    public void testCalculateRequiredPower() {
        // 测试根据目标温度计算所需功率
        float targetTemperature = 25.0f;
        float requiredPower = AirConditionCalculator.calculateRequiredPower(targetTemperature);
        
        // 验证计算结果为正数
        assertTrue("所需功率应为正数", requiredPower > 0);
        
        // 验证计算公式正确性
        float tempDifference = AppContextConstant.OUTSIDE_TEMP - targetTemperature 
                + AppContextConstant.ABLE_ADJUST_FACTOR;
        float expectedPower = (float) Math.pow(tempDifference, 2);
        assertEquals("功率计算应符合公式", expectedPower, requiredPower, 0.01f);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCalculateRequiredPowerInvalidTemperature() {
        // 测试无效温度值
        float invalidTemperature = AppContextConstant.AIR_CONDITION_MAX_TEMP + 10;
        AirConditionCalculator.calculateRequiredPower(invalidTemperature);
    }
    
    @Test
    public void testCalculatePowerEfficiency() {
        // 测试功率效率计算
        float powerConsumed = 100.0f;
        float temperatureChange = 5.0f;
        float efficiency = AirConditionCalculator.calculatePowerEfficiency(powerConsumed, temperatureChange);
        
        float expectedEfficiency = Math.abs(temperatureChange) / powerConsumed;
        assertEquals("功率效率计算应正确", expectedEfficiency, efficiency, 0.01f);
    }
    
    @Test
    public void testCalculatePowerEfficiencyNegativeChange() {
        // 测试负温度变化
        float powerConsumed = 100.0f;
        float temperatureChange = -5.0f;
        float efficiency = AirConditionCalculator.calculatePowerEfficiency(powerConsumed, temperatureChange);
        
        float expectedEfficiency = Math.abs(temperatureChange) / powerConsumed;
        assertEquals("负温度变化的功率效率计算应正确", expectedEfficiency, efficiency, 0.01f);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCalculatePowerEfficiencyZeroPower() {
        // 测试零功率值
        float zeroPower = 0.0f;
        float temperatureChange = 5.0f;
        AirConditionCalculator.calculatePowerEfficiency(zeroPower, temperatureChange);
    }
    
    @Test
    public void testGetPowerRange() {
        // 测试获取功率范围
        float[] powerRange = AirConditionCalculator.getPowerRange();
        
        assertNotNull("功率范围不应为空", powerRange);
        assertEquals("功率范围数组长度应为2", 2, powerRange.length);
        assertEquals("最小功率应正确", AppContextConstant.AIR_CONDITION_MIN_POWER, powerRange[0], 0.01f);
        assertEquals("最大功率应正确", AppContextConstant.AIR_CONDITION_MAX_POWER, powerRange[1], 0.01f);
        assertTrue("最小功率应小于最大功率", powerRange[0] < powerRange[1]);
    }
    
    @Test
    public void testGetTemperatureRange() {
        // 测试获取温度范围
        float[] temperatureRange = AirConditionCalculator.getTemperatureRange();
        
        assertNotNull("温度范围不应为空", temperatureRange);
        assertEquals("温度范围数组长度应为2", 2, temperatureRange.length);
        assertEquals("最小温度应正确", AppContextConstant.AIR_CONDITION_MIN_TEMP, temperatureRange[0], 0.01f);
        assertEquals("最大温度应正确", AppContextConstant.AIR_CONDITION_MAX_TEMP, temperatureRange[1], 0.01f);
        assertTrue("最小温度应小于最大温度", temperatureRange[0] < temperatureRange[1]);
    }
    
    @Test
    public void testBoundaryValues() {
        // 测试边界值
        
        // 测试最小功率
        float minPower = AppContextConstant.AIR_CONDITION_MIN_POWER;
        float minTemp = AirConditionCalculator.mapPowerToTemperature(minPower);
        assertTrue("最小功率对应的温度应在有效范围内", 
                minTemp <= AppContextConstant.AIR_CONDITION_MAX_TEMP);
        
        // 测试最大功率
        float maxPower = AppContextConstant.AIR_CONDITION_MAX_POWER;
        float maxTemp = AirConditionCalculator.mapPowerToTemperature(maxPower);
        assertTrue("最大功率对应的温度应在有效范围内", 
                maxTemp >= AppContextConstant.AIR_CONDITION_MIN_TEMP);
        
        // 验证功率越大，温度越低（制冷效果）
        assertTrue("更大的功率应产生更低的温度", maxTemp < minTemp);
    }
}