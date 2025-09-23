package com.leavesfly.iac.datasource.parser;

import java.util.Map;

/**
 * 数据解析策略接口
 * 
 * 该接口定义了数据解析的基本规范，支持不同数据格式的解析策略。
 * 采用策略模式设计，便于扩展新的解析格式。
 * 
 * @param <T> 解析结果类型
 */
public interface DataParseStrategy<T> {
    
    /**
     * 解析数据行
     * 
     * @param dataLine 待解析的数据行
     * @return 解析后的结果对象
     * @throws ParseException 解析异常
     */
    T parse(String dataLine) throws ParseException;
    
    /**
     * 验证数据行格式
     * 
     * @param dataLine 待验证的数据行
     * @return 如果格式正确返回true，否则返回false
     */
    boolean validate(String dataLine);
    
    /**
     * 获取支持的数据格式描述
     * 
     * @return 数据格式描述
     */
    String getFormatDescription();
    
    /**
     * 获取解析策略类型标识
     * 
     * @return 策略类型标识
     */
    String getStrategyType();
}