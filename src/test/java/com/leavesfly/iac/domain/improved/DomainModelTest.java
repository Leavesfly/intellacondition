package com.leavesfly.iac.domain.improved;

import static org.junit.Assert.*;

import org.junit.Test;

import com.leavesfly.iac.exception.IntelliAirConditionException;

/**
 * 改进的领域模型测试类
 * 
 * 测试领域对象的不可变性、线程安全性和业务逻辑
 */
public class DomainModelTest {

    @Test
    public void testGeoPointCreation() {
        GeoPoint point = new GeoPoint(1.0f, 2.0f);
        
        assertEquals("X坐标应该正确", 1.0f, point.getX(), 0.001f);
        assertEquals("Y坐标应该正确", 2.0f, point.getY(), 0.001f);
    }

    @Test
    public void testGeoPointDistance() {
        GeoPoint point1 = new GeoPoint(0.0f, 0.0f);
        GeoPoint point2 = new GeoPoint(3.0f, 4.0f);
        
        float distance = point1.distanceTo(point2);
        assertEquals("距离计算应该正确", 5.0f, distance, 0.001f);
    }

    @Test
    public void testGeoPointWithinDistance() {
        GeoPoint point1 = new GeoPoint(0.0f, 0.0f);
        GeoPoint point2 = new GeoPoint(3.0f, 4.0f);
        
        assertTrue("应该在距离范围内", point1.isWithinDistance(point2, 6.0f));
        assertFalse("应该不在距离范围内", point1.isWithinDistance(point2, 4.0f));
    }

    @Test(expected = IntelliAirConditionException.class)
    public void testGeoPointNullDistance() {
        GeoPoint point = new GeoPoint(0.0f, 0.0f);
        point.distanceTo(null);
    }

    @Test
    public void testPowerRangeCreation() {
        PowerRange range = new PowerRange(10.0f, 100.0f);
        
        assertEquals("最小功率应该正确", 10.0f, range.getMinPower(), 0.001f);
        assertEquals("最大功率应该正确", 100.0f, range.getMaxPower(), 0.001f);
        assertEquals("范围跨度应该正确", 90.0f, range.getRange(), 0.001f);
    }

    @Test(expected = IntelliAirConditionException.class)
    public void testInvalidPowerRange() {
        new PowerRange(100.0f, 10.0f); // 最大值小于最小值
    }

    @Test(expected = IntelliAirConditionException.class)
    public void testNegativePowerRange() {
        new PowerRange(-10.0f, 100.0f); // 负数最小值
    }

    @Test
    public void testPowerRangeValidation() {
        PowerRange range = new PowerRange(10.0f, 100.0f);
        
        assertTrue("50应该在范围内", range.isValid(50.0f));
        assertFalse("5应该不在范围内", range.isValid(5.0f));
        assertFalse("150应该不在范围内", range.isValid(150.0f));
    }

    @Test
    public void testPowerRangeConstrain() {
        PowerRange range = new PowerRange(10.0f, 100.0f);
        
        assertEquals("约束小值", 10.0f, range.constrain(5.0f), 0.001f);
        assertEquals("约束大值", 100.0f, range.constrain(150.0f), 0.001f);
        assertEquals("不约束正常值", 50.0f, range.constrain(50.0f), 0.001f);
    }

    @Test
    public void testPowerRangeRandomValue() {
        PowerRange range = new PowerRange(10.0f, 100.0f);
        
        for (int i = 0; i < 100; i++) {
            float randomValue = range.randomValue();
            assertTrue("随机值应该在范围内", range.isValid(randomValue));
        }
    }

    @Test
    public void testPowerValueCreation() {
        PowerRange range = new PowerRange(0.0f, 100.0f);
        PowerValue power = new PowerValue(50.0f, range);
        
        assertEquals("功率值应该正确", 50.0f, power.getValue(), 0.001f);
        assertEquals("功率范围应该正确", range, power.getRange());
    }

    @Test(expected = IntelliAirConditionException.class)
    public void testInvalidPowerValue() {
        PowerRange range = new PowerRange(10.0f, 100.0f);
        new PowerValue(150.0f, range); // 超出范围
    }

    @Test(expected = IntelliAirConditionException.class)
    public void testNullPowerRange() {
        new PowerValue(50.0f, null);
    }

    @Test
    public void testPowerValueWithValue() {
        PowerRange range = new PowerRange(0.0f, 100.0f);
        PowerValue original = new PowerValue(50.0f, range);
        PowerValue modified = original.withValue(75.0f);
        
        assertEquals("原值不应改变", 50.0f, original.getValue(), 0.001f);
        assertEquals("新值应该正确", 75.0f, modified.getValue(), 0.001f);
    }

    @Test
    public void testPowerValueArithmetic() {
        PowerRange range = new PowerRange(0.0f, 100.0f);
        PowerValue power = new PowerValue(50.0f, range);
        
        PowerValue added = power.add(10.0f);
        assertEquals("加法应该正确", 60.0f, added.getValue(), 0.001f);
        
        PowerValue multiplied = power.multiply(1.5f);
        assertEquals("乘法应该正确", 75.0f, multiplied.getValue(), 0.001f);
    }

    @Test
    public void testPowerValueUtilization() {
        PowerRange range = new PowerRange(10.0f, 100.0f);
        PowerValue power = new PowerValue(55.0f, range);
        
        float utilization = power.getUtilization();
        assertEquals("利用率应该正确", 0.5f, utilization, 0.001f);
    }

    @Test
    public void testPowerVectorCreation() {
        PowerRange range = new PowerRange(0.0f, 100.0f);
        PowerValue[] powers = {
            new PowerValue(30.0f, range),
            new PowerValue(50.0f, range),
            new PowerValue(70.0f, range)
        };
        
        PowerVector vector = new PowerVector(powers);
        
        assertEquals("向量大小应该正确", 3, vector.size());
        assertEquals("总功率应该正确", 150.0f, vector.getTotalPower(), 0.001f);
        assertEquals("平均功率应该正确", 50.0f, vector.getAveragePower(), 0.001f);
    }

    @Test(expected = IntelliAirConditionException.class)
    public void testEmptyPowerVector() {
        new PowerVector();
    }

    @Test(expected = IntelliAirConditionException.class)
    public void testNullPowerVector() {
        new PowerVector((PowerValue[]) null);
    }

    @Test
    public void testPowerVectorArithmetic() {
        PowerRange range = new PowerRange(0.0f, 200.0f);
        PowerValue[] powers1 = {
            new PowerValue(30.0f, range),
            new PowerValue(50.0f, range)
        };
        PowerValue[] powers2 = {
            new PowerValue(20.0f, range),
            new PowerValue(30.0f, range)
        };
        
        PowerVector vector1 = new PowerVector(powers1);
        PowerVector vector2 = new PowerVector(powers2);
        
        PowerVector sum = vector1.add(vector2);
        assertEquals("向量加法应该正确", 130.0f, sum.getTotalPower(), 0.001f);
        
        PowerVector scaled = vector1.multiply(2.0f);
        assertEquals("向量乘法应该正确", 160.0f, scaled.getTotalPower(), 0.001f);
    }
}