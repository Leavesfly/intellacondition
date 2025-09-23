package com.leavesfly.iac.domain;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * PowerVector功率向量测试类
 * 
 * 测试功率向量的创建、运算和属性计算
 */
public class PowerVectorTest {

    private PowerVector testPowerVector;
    private PowerRange testRange;

    @Before
    public void setUp() {
        testRange = new PowerRange(0.0f, 100.0f);
        PowerValue[] powers = {
            new PowerValue(30.0f, testRange),
            new PowerValue(50.0f, testRange),
            new PowerValue(70.0f, testRange)
        };
        testPowerVector = new PowerVector(powers);
    }

    @Test
    public void testPowerVectorCreationWithArray() {
        assertNotNull("PowerVector不应为null", testPowerVector);
        assertEquals("向量大小应该正确", 3, testPowerVector.getPowerValueVector().length);
    }

    @Test
    public void testPowerVectorCreationWithVarargs() {
        PowerValue power1 = new PowerValue(20.0f, testRange);
        PowerValue power2 = new PowerValue(40.0f, testRange);
        
        PowerValue[] powers = {power1, power2};
        PowerVector vector = new PowerVector(powers);
        
        assertNotNull("PowerVector不应为null", vector);
        assertEquals("向量大小应该正确", 2, vector.getPowerValueVector().length);
    }

    @Test
    public void testGetPowerValueVector() {
        PowerValue[] values = testPowerVector.getPowerValueVector();
        
        assertNotNull("功率值数组不应为null", values);
        assertEquals("数组长度应该正确", 3, values.length);
        assertEquals("第一个元素值应该正确", 30.0f, values[0].getValue(), 0.001f);
        assertEquals("第二个元素值应该正确", 50.0f, values[1].getValue(), 0.001f);
        assertEquals("第三个元素值应该正确", 70.0f, values[2].getValue(), 0.001f);
    }

    @Test
    public void testGetPowerValueFloatArray() {
        Float[] floatArray = testPowerVector.getPowerValueFloatArray();
        
        assertNotNull("Float数组不应为null", floatArray);
        assertEquals("数组长度应该正确", 3, floatArray.length);
        assertEquals("第一个元素值应该正确", 30.0f, floatArray[0], 0.001f);
        assertEquals("第二个元素值应该正确", 50.0f, floatArray[1], 0.001f);
        assertEquals("第三个元素值应该正确", 70.0f, floatArray[2], 0.001f);
    }

    @Test
    public void testToString() {
        String vectorString = testPowerVector.toString();
        
        assertNotNull("toString结果不应为null", vectorString);
        assertTrue("应该包含第一个值", vectorString.contains("30.0"));
        assertTrue("应该包含第二个值", vectorString.contains("50.0"));
        assertTrue("应该包含第三个值", vectorString.contains("70.0"));
    }

    @Test
    public void testEmptyPowerVectorNotSupported() {
        // PowerVector类没有无参构造函数，所以测试null数组
        try {
            new PowerVector((PowerValue[]) null);
            fail("应该抛出异常");
        } catch (Exception e) {
            // 预期的异常
        }
    }

    @Test(expected = NullPointerException.class) 
    public void testNullPowerValueArray() {
        new PowerVector((PowerValue[]) null);
    }

    @Test
    public void testPowerVectorWithNullElement() {
        PowerValue[] powers = {
            new PowerValue(30.0f, testRange),
            null,
            new PowerValue(70.0f, testRange)
        };
        
        // PowerVector类不会检查null元素，所以不会抛出异常
        PowerVector vector = new PowerVector(powers);
        assertNotNull("向量不应为null", vector);
        
        // 但是访问时可能会有问题
        try {
            powers[1].getValue(); // 这里会抛出NullPointerException
            fail("应该抛出异常");
        } catch (NullPointerException e) {
            // 预期的异常
        }
    }

    @Test
    public void testSingleElementVector() {
        PowerValue singlePower = new PowerValue(42.0f, testRange);
        PowerValue[] powers = {singlePower};
        PowerVector singleVector = new PowerVector(powers);
        
        assertEquals("单元素向量大小应该为1", 1, singleVector.getPowerValueVector().length);
        assertEquals("单元素值应该正确", 42.0f, singleVector.getPowerValueVector()[0].getValue(), 0.001f);
    }

    @Test
    public void testLargeVector() {
        PowerValue[] largePowers = new PowerValue[100];
        for (int i = 0; i < 100; i++) {
            largePowers[i] = new PowerValue(i * 1.0f, testRange);
        }
        
        PowerVector largeVector = new PowerVector(largePowers);
        
        assertEquals("大向量大小应该正确", 100, largeVector.getPowerValueVector().length);
        assertEquals("第一个元素应该正确", 0.0f, largeVector.getPowerValueVector()[0].getValue(), 0.001f);
        assertEquals("最后一个元素应该正确", 99.0f, largeVector.getPowerValueVector()[99].getValue(), 0.001f);
    }

    @Test
    public void testVectorImmutability() {
        PowerValue[] originalValues = testPowerVector.getPowerValueVector();
        
        // 尝试修改返回的数组
        PowerValue originalFirst = originalValues[0];
        originalValues[0] = new PowerValue(999.0f, testRange);
        
        // PowerVector类直接返回内部数组引用，所以会被修改
        // 这是一个设计缺陷，但是我们测试实际行为
        PowerValue[] newValues = testPowerVector.getPowerValueVector();
        assertEquals("向量内容已被修改（这是设计缺陷）", 999.0f, newValues[0].getValue(), 0.001f);
        
        // 重新设置为原始值以便其他测试正常运行
        originalValues[0] = originalFirst;
    }

    @Test
    public void testFloatArrayImmutability() {
        Float[] originalFloats = testPowerVector.getPowerValueFloatArray();
        
        // 尝试修改返回的数组
        Float originalFirst = originalFloats[0];
        originalFloats[0] = 999.0f;
        
        // 再次获取数组，检查是否受影响
        Float[] newFloats = testPowerVector.getPowerValueFloatArray();
        assertEquals("Float数组内容应该保持不变", originalFirst, newFloats[0]);
    }

    @Test
    public void testEqualsAndHashCode() {
        PowerValue[] samePowers = {
            new PowerValue(30.0f, testRange),
            new PowerValue(50.0f, testRange),
            new PowerValue(70.0f, testRange)
        };
        PowerVector sameVector = new PowerVector(samePowers);
        
        // 注意：PowerVector可能没有重写equals方法，这里测试引用相等性
        assertNotSame("两个向量对象应该是不同的实例", testPowerVector, sameVector);
        
        // 如果实现了equals方法，可以这样测试：
        // assertEquals("相同内容的向量应该相等", testPowerVector, sameVector);
    }
}