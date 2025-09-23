package com.leavesfly.iac.exception;

/**
 * 智能空调系统基础异常类
 * 
 * 所有自定义异常的基类，提供统一的异常处理机制
 */
public class IntelliAirConditionException extends RuntimeException {

    private final String errorCode;

    public IntelliAirConditionException(String message) {
        super(message);
        this.errorCode = "IAC_UNKNOWN";
    }

    public IntelliAirConditionException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public IntelliAirConditionException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}