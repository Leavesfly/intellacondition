package com.leavesfly.iac.exception;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * IntelliAirConditionException异常类测试
 * 
 * 测试自定义异常类的创建和属性访问
 */
public class IntelliAirConditionExceptionTest {

    @Test
    public void testExceptionWithMessage() {
        String message = "测试异常消息";
        IntelliAirConditionException exception = new IntelliAirConditionException(message);
        
        assertEquals("异常消息应该正确", message, exception.getMessage());
        assertEquals("默认错误代码应该正确", "IAC_UNKNOWN", exception.getErrorCode());
    }

    @Test
    public void testExceptionWithErrorCodeAndMessage() {
        String errorCode = "IAC_TEST";
        String message = "测试异常消息";
        IntelliAirConditionException exception = new IntelliAirConditionException(errorCode, message);
        
        assertEquals("异常消息应该正确", message, exception.getMessage());
        assertEquals("错误代码应该正确", errorCode, exception.getErrorCode());
    }

    @Test
    public void testExceptionWithErrorCodeMessageAndCause() {
        String errorCode = "IAC_TEST";
        String message = "测试异常消息";
        Exception cause = new RuntimeException("原因异常");
        
        IntelliAirConditionException exception = new IntelliAirConditionException(errorCode, message, cause);
        
        assertEquals("异常消息应该正确", message, exception.getMessage());
        assertEquals("错误代码应该正确", errorCode, exception.getErrorCode());
        assertEquals("异常原因应该正确", cause, exception.getCause());
    }

    @Test
    public void testExceptionWithNullMessage() {
        IntelliAirConditionException exception = new IntelliAirConditionException((String) null);
        
        assertNull("null消息应该被正确处理", exception.getMessage());
        assertEquals("默认错误代码应该正确", "IAC_UNKNOWN", exception.getErrorCode());
    }

    @Test
    public void testExceptionWithNullErrorCode() {
        String message = "测试消息";
        IntelliAirConditionException exception = new IntelliAirConditionException(null, message);
        
        assertEquals("异常消息应该正确", message, exception.getMessage());
        assertNull("null错误代码应该被正确处理", exception.getErrorCode());
    }

    @Test
    public void testExceptionInheritance() {
        IntelliAirConditionException exception = new IntelliAirConditionException("test");
        
        assertTrue("应该继承自RuntimeException", exception instanceof RuntimeException);
        assertTrue("应该继承自Exception", exception instanceof Exception);
        assertTrue("应该继承自Throwable", exception instanceof Throwable);
    }

    @Test
    public void testExceptionSerializable() {
        IntelliAirConditionException exception = new IntelliAirConditionException("IAC_TEST", "测试消息");
        
        // 检查是否可以被序列化（通过检查是否有serialVersionUID）
        // 注意：这里只是检查类型，实际序列化测试需要更复杂的设置
        assertTrue("异常应该是可序列化的", exception instanceof java.io.Serializable);
    }

    @Test
    public void testExceptionToString() {
        String errorCode = "IAC_TEST";
        String message = "测试异常消息";
        IntelliAirConditionException exception = new IntelliAirConditionException(errorCode, message);
        
        String toString = exception.toString();
        
        assertNotNull("toString不应为null", toString);
        assertTrue("toString应该包含类名", toString.contains("IntelliAirConditionException"));
        assertTrue("toString应该包含消息", toString.contains(message));
    }

    @Test
    public void testExceptionStackTrace() {
        IntelliAirConditionException exception = new IntelliAirConditionException("IAC_TEST", "测试消息");
        
        StackTraceElement[] stackTrace = exception.getStackTrace();
        
        assertNotNull("堆栈跟踪不应为null", stackTrace);
        assertTrue("堆栈跟踪应该包含元素", stackTrace.length > 0);
    }
}