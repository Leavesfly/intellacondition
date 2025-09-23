package com.leavesfly.iac.datasource.parser;

/**
 * 数据解析异常类
 * 
 * 该异常类用于封装数据解析过程中出现的各种错误信息，
 * 提供详细的错误上下文信息。
 */
public class ParseException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 错误的数据行
     */
    private final String errorLine;
    
    /**
     * 行号
     */
    private final int lineNumber;
    
    /**
     * 解析策略类型
     */
    private final String strategyType;
    
    /**
     * 构造函数
     * 
     * @param message 错误消息
     */
    public ParseException(String message) {
        super(message);
        this.errorLine = null;
        this.lineNumber = -1;
        this.strategyType = null;
    }
    
    /**
     * 构造函数
     * 
     * @param message 错误消息
     * @param cause 原因异常
     */
    public ParseException(String message, Throwable cause) {
        super(message, cause);
        this.errorLine = null;
        this.lineNumber = -1;
        this.strategyType = null;
    }
    
    /**
     * 构造函数
     * 
     * @param message 错误消息
     * @param errorLine 错误的数据行
     * @param lineNumber 行号
     * @param strategyType 解析策略类型
     */
    public ParseException(String message, String errorLine, int lineNumber, String strategyType) {
        super(message);
        this.errorLine = errorLine;
        this.lineNumber = lineNumber;
        this.strategyType = strategyType;
    }
    
    /**
     * 构造函数
     * 
     * @param message 错误消息
     * @param cause 原因异常
     * @param errorLine 错误的数据行
     * @param lineNumber 行号
     * @param strategyType 解析策略类型
     */
    public ParseException(String message, Throwable cause, String errorLine, int lineNumber, String strategyType) {
        super(message, cause);
        this.errorLine = errorLine;
        this.lineNumber = lineNumber;
        this.strategyType = strategyType;
    }
    
    /**
     * 获取错误的数据行
     * 
     * @return 错误的数据行
     */
    public String getErrorLine() {
        return errorLine;
    }
    
    /**
     * 获取行号
     * 
     * @return 行号
     */
    public int getLineNumber() {
        return lineNumber;
    }
    
    /**
     * 获取解析策略类型
     * 
     * @return 解析策略类型
     */
    public String getStrategyType() {
        return strategyType;
    }
    
    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder(super.getMessage());
        
        if (strategyType != null) {
            sb.append(" [策略类型: ").append(strategyType).append("]");
        }
        
        if (lineNumber > 0) {
            sb.append(" [行号: ").append(lineNumber).append("]");
        }
        
        if (errorLine != null) {
            sb.append(" [错误行: ").append(errorLine).append("]");
        }
        
        return sb.toString();
    }
}