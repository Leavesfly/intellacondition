package com.leavesfly.iac.datasource.parser.impl;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.leavesfly.iac.config.AppContextConstant;
import com.leavesfly.iac.datasource.parser.DataParseStrategy;
import com.leavesfly.iac.datasource.parser.ParseException;
import com.leavesfly.iac.domain.PowerVector;
import com.leavesfly.iac.train.domain.IntellacTrainDataItem;

/**
 * 训练数据解析策略实现类
 * 
 * 该类负责解析训练数据格式的字符串，数据格式为：
 * 传感器ID\t温度\t室外温度\t功率值1,功率值2,...,功率值N
 * 
 * 示例：0\t22.5\t35.0\t20.4,120.3,114.9,297.6,298.3,190.0,198.7,83.5
 */
public class TrainDataParseStrategy implements DataParseStrategy<IntellacTrainDataItem> {
    
    /**
     * 表格分隔符（制表符）
     */
    private static final Pattern SPLITTER_TABLE = Pattern.compile("\t");
    
    /**
     * 逗号分隔符
     */
    private static final Pattern SPLITTER_COMMA = Pattern.compile(",");
    
    /**
     * 数据格式验证正则表达式
     */
    private static final Pattern DATA_FORMAT_PATTERN = Pattern.compile(
        "^[^\\t]+\\t[0-9]+\\.?[0-9]*\\t[0-9]+\\.?[0-9]*\\t([0-9]+\\.?[0-9]*,)*[0-9]+\\.?[0-9]*$"
    );
    
    @Override
    public IntellacTrainDataItem parse(String dataLine) throws ParseException {
        if (StringUtils.isBlank(dataLine)) {
            throw new ParseException("训练数据行不能为空", dataLine, -1, getStrategyType());
        }
        
        String trimmedLine = dataLine.trim();
        
        if (!validate(trimmedLine)) {
            throw new ParseException("训练数据格式不正确", trimmedLine, -1, getStrategyType());
        }
        
        try {
            String[] segments = SPLITTER_TABLE.split(trimmedLine);
            
            // 解析传感器ID
            String sensorId = segments[0].trim();
            if (sensorId.isEmpty()) {
                throw new ParseException("传感器ID不能为空", trimmedLine, -1, getStrategyType());
            }
            
            // 解析温度值
            float temperature = parseFloatValue(segments[1], "温度", trimmedLine);
            
            // 解析室外温度
            float outsideTemp = parseFloatValue(segments[2], "室外温度", trimmedLine);
            
            // 解析功率值数组
            PowerVector powerVector = parsePowerVector(segments[3], trimmedLine);
            
            return new IntellacTrainDataItem(sensorId, powerVector, temperature, outsideTemp);
            
        } catch (ParseException e) {
            throw e;
        } catch (Exception e) {
            throw new ParseException("解析训练数据时发生未知错误: " + e.getMessage(), 
                    e, trimmedLine, -1, getStrategyType());
        }
    }
    
    /**
     * 解析浮点数值
     * 
     * @param valueStr 数值字符串
     * @param fieldName 字段名称
     * @param dataLine 数据行
     * @return 解析后的浮点数
     * @throws ParseException 解析异常
     */
    private float parseFloatValue(String valueStr, String fieldName, String dataLine) throws ParseException {
        if (!NumberUtils.isNumber(valueStr)) {
            throw new ParseException(String.format("%s值格式不正确: %s", fieldName, valueStr), 
                    dataLine, -1, getStrategyType());
        }
        return NumberUtils.toFloat(valueStr);
    }
    
    /**
     * 解析功率向量
     * 
     * @param powerStr 功率字符串
     * @param dataLine 数据行
     * @return 功率向量对象
     * @throws ParseException 解析异常
     */
    private PowerVector parsePowerVector(String powerStr, String dataLine) throws ParseException {
        String[] powerValues = SPLITTER_COMMA.split(powerStr);
        
        if (powerValues.length == 0) {
            throw new ParseException("功率值数组不能为空", dataLine, -1, getStrategyType());
        }
        
        Float[] powerValueArray = new Float[powerValues.length];
        
        for (int i = 0; i < powerValues.length; i++) {
            String powerValue = powerValues[i].trim();
            if (!NumberUtils.isNumber(powerValue)) {
                throw new ParseException(String.format("功率值格式不正确: %s (位置: %d)", powerValue, i + 1), 
                        dataLine, -1, getStrategyType());
            }
            powerValueArray[i] = NumberUtils.toFloat(powerValue);
        }
        
        try {
            return new PowerVector(powerValueArray, AppContextConstant.AIR_CONDITION_NUM);
        } catch (Exception e) {
            throw new ParseException("创建PowerVector对象失败: " + e.getMessage(), 
                    e, dataLine, -1, getStrategyType());
        }
    }
    
    @Override
    public boolean validate(String dataLine) {
        if (StringUtils.isBlank(dataLine)) {
            return false;
        }
        
        String trimmedLine = dataLine.trim();
        
        // 检查基本格式
        if (!DATA_FORMAT_PATTERN.matcher(trimmedLine).matches()) {
            return false;
        }
        
        // 检查字段数量
        String[] segments = SPLITTER_TABLE.split(trimmedLine);
        return segments.length == 4;
    }
    
    @Override
    public String getFormatDescription() {
        return "训练数据格式: 传感器ID\\t温度\\t室外温度\\t功率值1,功率值2,...,功率值N";
    }
    
    @Override
    public String getStrategyType() {
        return "TRAIN_DATA";
    }
}