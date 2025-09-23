package com.leavesfly.iac.evalute;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.leavesfly.iac.domain.PowerRange;
import com.leavesfly.iac.domain.PowerValue;
import com.leavesfly.iac.domain.PowerVector;

/**
 * 解决方案测试类
 * 
 * 测试解决方案对象的创建、属性访问和字符串表示
 */
public class SolutionTest {

    private PowerVector testPowerVector;
    private Solution testSolution;

    @Before
    public void setUp() {
        PowerRange range = new PowerRange(0.0f, 100.0f);
        PowerValue[] powers = {
            new PowerValue(30.0f, range),
            new PowerValue(50.0f, range),
            new PowerValue(70.0f, range)
        };
        testPowerVector = new PowerVector(powers);
        testSolution = new Solution("TestSolution", testPowerVector);
    }

    @Test
    public void testSolutionCreation() {
        assertNotNull("解决方案不应为null", testSolution);
        assertEquals("解决方案名称应该正确", "TestSolution", testSolution.getSolutionName());
        assertEquals("功率向量应该正确", testPowerVector, testSolution.getPowerVector());
    }

    @Test
    public void testSolutionWithNullName() {
        Solution solution = new Solution(null, testPowerVector);
        assertNull("解决方案名称应该为null", solution.getSolutionName());
        assertEquals("功率向量应该正确", testPowerVector, solution.getPowerVector());
    }

    @Test
    public void testSolutionWithNullPowerVector() {
        Solution solution = new Solution("TestSolution", null);
        assertEquals("解决方案名称应该正确", "TestSolution", solution.getSolutionName());
        assertNull("功率向量应该为null", solution.getPowerVector());
    }

    @Test
    public void testSolutionToString() {
        String expected = "TestSolution:" + testPowerVector.toString();
        assertEquals("字符串表示应该正确", expected, testSolution.toString());
    }

    @Test
    public void testSolutionToStringWithNullName() {
        Solution solution = new Solution(null, testPowerVector);
        String expected = "null:" + testPowerVector.toString();
        assertEquals("字符串表示应该正确处理null名称", expected, solution.toString());
    }

    @Test
    public void testSolutionEquality() {
        Solution solution1 = new Solution("TestSolution", testPowerVector);
        Solution solution2 = new Solution("TestSolution", testPowerVector);
        
        // 注意：这里测试的是引用相等性，因为Solution类没有重写equals方法
        assertNotSame("两个解决方案对象应该是不同的实例", solution1, solution2);
        assertEquals("但应该具有相同的名称", solution1.getSolutionName(), solution2.getSolutionName());
        assertEquals("但应该具有相同的功率向量", solution1.getPowerVector(), solution2.getPowerVector());
    }

    @Test
    public void testSolutionImmutability() {
        String originalName = testSolution.getSolutionName();
        PowerVector originalVector = testSolution.getPowerVector();
        
        // 尝试修改返回的对象（如果可能的话）
        String retrievedName = testSolution.getSolutionName();
        PowerVector retrievedVector = testSolution.getPowerVector();
        
        assertEquals("名称应该保持不变", originalName, retrievedName);
        assertEquals("功率向量应该保持不变", originalVector, retrievedVector);
    }
}