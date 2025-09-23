package com.leavesfly.iac.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * MathUtil数学工具类测试
 * 
 * 测试数学工具类的随机数生成功能
 */
public class MathUtilTest {

    @Test
    public void testNextFloatRange() {
        float from = 1.0f;
        float to = 10.0f;
        
        // 测试多次生成，确保都在范围内
        for (int i = 0; i < 100; i++) {
            float result = MathUtil.nextFloat(from, to);
            assertTrue("生成的浮点数应该大于等于from", result >= from);
            assertTrue("生成的浮点数应该小于等于to", result <= to);
        }
    }

    @Test
    public void testNextFloatSameValues() {
        float value = 5.0f;
        float result = MathUtil.nextFloat(value, value);
        assertEquals("相同from和to应该返回相同值", value, result, 0.001f);
    }

    @Test
    public void testNextFloatNegativeRange() {
        float from = -10.0f;
        float to = -1.0f;
        
        for (int i = 0; i < 50; i++) {
            float result = MathUtil.nextFloat(from, to);
            assertTrue("负数范围的结果应该大于等于from", result >= from);
            assertTrue("负数范围的结果应该小于等于to", result <= to);
        }
    }

    @Test
    public void testNextFloatCrossZeroRange() {
        float from = -5.0f;
        float to = 5.0f;
        
        for (int i = 0; i < 50; i++) {
            float result = MathUtil.nextFloat(from, to);
            assertTrue("跨零范围的结果应该大于等于from", result >= from);
            assertTrue("跨零范围的结果应该小于等于to", result <= to);
        }
    }

    @Test
    public void testNextFloatZeroRange() {
        float from = 0.0f;
        float to = 1.0f;
        
        float result = MathUtil.nextFloat(from, to);
        assertTrue("从0开始的范围应该正确", result >= from && result <= to);
    }

    @Test
    public void testNextIntRange() {
        int from = 1;
        int to = 10;
        
        // 测试多次生成，确保都在范围内
        for (int i = 0; i < 100; i++) {
            int result = MathUtil.nextInt(from, to);
            assertTrue("生成的整数应该大于等于from", result >= from);
            assertTrue("生成的整数应该小于to", result < to);
        }
    }

    @Test
    public void testNextIntSameValues() {
        int value = 5;
        int result = MathUtil.nextInt(value, value);
        assertEquals("相同from和to应该返回from", value, result);
    }

    @Test
    public void testNextIntSmallRange() {
        int from = 5;
        int to = 6;
        
        int result = MathUtil.nextInt(from, to);
        assertEquals("小范围应该返回from", from, result);
    }

    @Test
    public void testNextIntLargeRange() {
        int from = 1;
        int to = 1000;
        
        for (int i = 0; i < 50; i++) {
            int result = MathUtil.nextInt(from, to);
            assertTrue("大范围的结果应该大于等于from", result >= from);
            assertTrue("大范围的结果应该小于to", result < to);
        }
    }

    @Test
    public void testNextIntZeroRange() {
        int from = 0;
        int to = 10;
        
        for (int i = 0; i < 50; i++) {
            int result = MathUtil.nextInt(from, to);
            assertTrue("从0开始的范围应该正确", result >= from && result < to);
        }
    }

    @Test
    public void testNextIntNegativeRange() {
        int from = -10;
        int to = -1;
        
        for (int i = 0; i < 50; i++) {
            int result = MathUtil.nextInt(from, to);
            assertTrue("负数范围的结果应该大于等于from", result >= from);
            assertTrue("负数范围的结果应该小于to", result < to);
        }
    }

    @Test
    public void testRandomnessFloat() {
        float from = 0.0f;
        float to = 100.0f;
        
        float first = MathUtil.nextFloat(from, to);
        float second = MathUtil.nextFloat(from, to);
        
        // 虽然理论上可能相等，但在实际情况下应该不同
        // 这个测试可能偶尔失败，但概率很小
        assertNotEquals("连续两次调用应该产生不同结果（概率上）", first, second, 0.001f);
    }

    @Test
    public void testRandomnessInt() {
        int from = 0;
        int to = 1000;
        
        int first = MathUtil.nextInt(from, to);
        int second = MathUtil.nextInt(from, to);
        
        // 对于足够大的范围，连续两次调用产生相同结果的概率很小
        // 这个测试可能偶尔失败，但概率很小
        assertNotEquals("连续两次调用应该产生不同结果（概率上）", first, second);
    }

    @Test
    public void testFloatDistribution() {
        float from = 0.0f;
        float to = 10.0f;
        float sum = 0.0f;
        int count = 1000;
        
        for (int i = 0; i < count; i++) {
            sum += MathUtil.nextFloat(from, to);
        }
        
        float average = sum / count;
        float expectedAverage = (from + to) / 2;
        
        // 期望平均值应该接近理论中值，允许一定误差
        assertTrue("平均值应该接近理论中值", 
            Math.abs(average - expectedAverage) < 1.0f);
    }

    @Test
    public void testIntDistribution() {
        int from = 0;
        int to = 100;
        double sum = 0.0;
        int count = 1000;
        
        for (int i = 0; i < count; i++) {
            sum += MathUtil.nextInt(from, to);
        }
        
        double average = sum / count;
        double expectedAverage = (from + to - 1) / 2.0; // 因为to是exclusive的
        
        // 期望平均值应该接近理论中值，允许一定误差
        assertTrue("平均值应该接近理论中值", 
            Math.abs(average - expectedAverage) < 5.0);
    }
}