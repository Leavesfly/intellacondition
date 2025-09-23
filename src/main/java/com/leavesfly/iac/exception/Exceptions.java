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

/**
 * 配置异常
 */
class ConfigurationException extends IntelliAirConditionException {
    public ConfigurationException(String message) {
        super("IAC_CONFIG", message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super("IAC_CONFIG", message, cause);
    }
}

/**
 * 数据访问异常
 */
class DataAccessException extends IntelliAirConditionException {
    public DataAccessException(String message) {
        super("IAC_DATA", message);
    }

    public DataAccessException(String message, Throwable cause) {
        super("IAC_DATA", message, cause);
    }
}

/**
 * 训练异常
 */
class TrainingException extends IntelliAirConditionException {
    public TrainingException(String message) {
        super("IAC_TRAINING", message);
    }

    public TrainingException(String message, Throwable cause) {
        super("IAC_TRAINING", message, cause);
    }
}

/**
 * 调度异常
 */
class SchedulingException extends IntelliAirConditionException {
    public SchedulingException(String message) {
        super("IAC_SCHEDULING", message);
    }

    public SchedulingException(String message, Throwable cause) {
        super("IAC_SCHEDULING", message, cause);
    }
}

/**
 * 评估异常
 */
class EvaluationException extends IntelliAirConditionException {
    public EvaluationException(String message) {
        super("IAC_EVALUATION", message);
    }

    public EvaluationException(String message, Throwable cause) {
        super("IAC_EVALUATION", message, cause);
    }
}