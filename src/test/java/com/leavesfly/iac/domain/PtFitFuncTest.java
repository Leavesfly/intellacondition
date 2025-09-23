package com.leavesfly.iac.domain;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.leavesfly.iac.train.trainer.TrainModel;

/**
 * PtFitFunc功率-温度拟合函数测试类
 * 
 * 测试功率-温度拟合函数的创建、属性访问和温度计算功能
 */
public class PtFitFuncTest {

    private PtFitFunc ptFitFunc;
    private PowerVector testPowerVector;
    private MockTrainModel mockTrainModel;

    /**
     * 模拟训练模型，用于测试
     */
    private static class MockTrainModel implements TrainModel {
        private float expectedResult;

        public MockTrainModel(float expectedResult) {
            this.expectedResult = expectedResult;
        }

        @Override
        public <T extends com.leavesfly.iac.train.domain.TrainDataItem<Float, Float>> void train(
                java.util.Collection<T> trainDataSet) {
            // 空实现，用于测试
        }

        @Override
        public <T extends Number> float useMode(T[] feature) {
            return expectedResult;
        }
    }

    @Before
    public void setUp() {
        // 创建模拟训练模型
        mockTrainModel = new MockTrainModel(25.5f);
        
        // 创建PtFitFunc实例
        ptFitFunc = new PtFitFunc("sensor001", 35.0f, mockTrainModel);
        
        // 创建测试用的功率向量
        PowerRange range = new PowerRange(0.0f, 100.0f);
        PowerValue[] powers = {
            new PowerValue(30.0f, range),
            new PowerValue(50.0f, range),
            new PowerValue(70.0f, range)
        };
        testPowerVector = new PowerVector(powers);
    }

    @Test
    public void testPtFitFuncCreation() {
        assertNotNull("PtFitFunc不应为null", ptFitFunc);
        assertEquals("传感器ID应该正确", "sensor001", ptFitFunc.getSensorId());
        assertEquals("室外温度应该正确", 35.0f, ptFitFunc.getOutsideTemp(), 0.001f);
    }

    @Test
    public void testSensorIdGetterSetter() {
        String newSensorId = "sensor002";
        ptFitFunc.setSensorId(newSensorId);
        assertEquals("传感器ID应该被正确设置", newSensorId, ptFitFunc.getSensorId());
    }

    @Test
    public void testOutsideTempGetterSetter() {
        float newOutsideTemp = 40.0f;
        ptFitFunc.setOutsideTemp(newOutsideTemp);
        assertEquals("室外温度应该被正确设置", newOutsideTemp, ptFitFunc.getOutsideTemp(), 0.001f);
    }

    @Test
    public void testCalTemperature() {
        float calculatedTemp = ptFitFunc.calTemperature(testPowerVector);
        assertEquals("计算温度应该使用训练模型的结果", 25.5f, calculatedTemp, 0.001f);
    }

    @Test
    public void testCalTemperatureWithDifferentModel() {
        // 创建另一个模拟模型，返回不同的结果
        MockTrainModel anotherModel = new MockTrainModel(28.0f);
        PtFitFunc anotherPtFitFunc = new PtFitFunc("sensor003", 30.0f, anotherModel);
        
        float calculatedTemp = anotherPtFitFunc.calTemperature(testPowerVector);
        assertEquals("不同模型应该返回不同的结果", 28.0f, calculatedTemp, 0.001f);
    }

    @Test
    public void testWithNullSensorId() {
        PtFitFunc ptFitFuncWithNullId = new PtFitFunc(null, 35.0f, mockTrainModel);
        assertNull("传感器ID可以为null", ptFitFuncWithNullId.getSensorId());
    }

    @Test
    public void testWithZeroOutsideTemp() {
        PtFitFunc ptFitFuncZeroTemp = new PtFitFunc("sensor004", 0.0f, mockTrainModel);
        assertEquals("室外温度可以为0", 0.0f, ptFitFuncZeroTemp.getOutsideTemp(), 0.001f);
    }

    @Test
    public void testWithNegativeOutsideTemp() {
        PtFitFunc ptFitFuncNegTemp = new PtFitFunc("sensor005", -10.0f, mockTrainModel);
        assertEquals("室外温度可以为负数", -10.0f, ptFitFuncNegTemp.getOutsideTemp(), 0.001f);
    }

    @Test(expected = NullPointerException.class)
    public void testCalTemperatureWithNullPowerVector() {
        ptFitFunc.calTemperature(null);
    }

    @Test
    public void testMultipleTemperatureCalculations() {
        // 创建多个不同的功率向量
        PowerRange range = new PowerRange(0.0f, 100.0f);
        
        PowerValue[] powers1 = {new PowerValue(10.0f, range), new PowerValue(20.0f, range)};
        PowerValue[] powers2 = {new PowerValue(50.0f, range), new PowerValue(60.0f, range)};
        
        PowerVector vector1 = new PowerVector(powers1);
        PowerVector vector2 = new PowerVector(powers2);
        
        float temp1 = ptFitFunc.calTemperature(vector1);
        float temp2 = ptFitFunc.calTemperature(vector2);
        
        // 因为使用的是相同的模拟模型，所以结果应该相同
        assertEquals("相同模型应该返回相同结果", temp1, temp2, 0.001f);
        assertEquals("结果应该等于模拟模型的预期值", 25.5f, temp1, 0.001f);
    }
}