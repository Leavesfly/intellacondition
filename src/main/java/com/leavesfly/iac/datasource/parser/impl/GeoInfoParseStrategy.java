package com.leavesfly.iac.datasource.parser.impl;

import java.util.AbstractMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.leavesfly.iac.datasource.parser.DataParseStrategy;
import com.leavesfly.iac.datasource.parser.ParseException;
import com.leavesfly.iac.domain.GeoPoint;

/**
 * 地理位置信息解析策略实现类
 * 
 * 该类负责解析地理位置数据格式的字符串，数据格式为：
 * ID\tX坐标,Y坐标
 * 
 * 示例：0\t5,2
 */
public class GeoInfoParseStrategy implements DataParseStrategy<Map.Entry<String, GeoPoint>> {
    
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
        "^[^\\t]+\\t[0-9]+\\.?[0-9]*,[0-9]+\\.?[0-9]*$"
    );
    
    @Override
    public Map.Entry<String, GeoPoint> parse(String dataLine) throws ParseException {
        if (StringUtils.isBlank(dataLine)) {
            throw new ParseException("地理位置数据行不能为空", dataLine, -1, getStrategyType());
        }
        
        String trimmedLine = dataLine.trim();
        
        if (!validate(trimmedLine)) {
            throw new ParseException("地理位置数据格式不正确", trimmedLine, -1, getStrategyType());
        }
        
        try {
            String[] segments = SPLITTER_TABLE.split(trimmedLine);
            
            // 解析ID
            String id = segments[0].trim();
            if (id.isEmpty()) {
                throw new ParseException("地理位置ID不能为空", trimmedLine, -1, getStrategyType());
            }
            
            // 解析坐标
            GeoPoint geoPoint = parseCoordinates(segments[1], trimmedLine);
            
            return new AbstractMap.SimpleEntry<>(id, geoPoint);
            
        } catch (ParseException e) {
            throw e;
        } catch (Exception e) {
            throw new ParseException("解析地理位置数据时发生未知错误: " + e.getMessage(), 
                    e, trimmedLine, -1, getStrategyType());
        }
    }
    
    /**
     * 解析坐标信息
     * 
     * @param coordinateStr 坐标字符串 (格式: x,y)
     * @param dataLine 数据行
     * @return 地理位置对象
     * @throws ParseException 解析异常
     */
    private GeoPoint parseCoordinates(String coordinateStr, String dataLine) throws ParseException {
        String[] coordinates = SPLITTER_COMMA.split(coordinateStr.trim());
        
        if (coordinates.length != 2) {
            throw new ParseException("坐标格式不正确，应为x,y格式: " + coordinateStr, 
                    dataLine, -1, getStrategyType());
        }
        
        // 解析X坐标
        int x = parseCoordinate(coordinates[0], "X坐标", dataLine);
        
        // 解析Y坐标
        int y = parseCoordinate(coordinates[1], "Y坐标", dataLine);
        
        // 验证坐标范围的合理性
        validateCoordinateRange(x, y, dataLine);
        
        return new GeoPoint(x, y);
    }
    
    /**
     * 解析单个坐标值
     * 
     * @param coordStr 坐标字符串
     * @param coordName 坐标名称
     * @param dataLine 数据行
     * @return 坐标整数值
     * @throws ParseException 解析异常
     */
    private int parseCoordinate(String coordStr, String coordName, String dataLine) throws ParseException {
        String trimmedCoord = coordStr.trim();
        
        if (!NumberUtils.isNumber(trimmedCoord)) {
            throw new ParseException(String.format("%s值格式不正确: %s", coordName, trimmedCoord), 
                    dataLine, -1, getStrategyType());
        }
        
        return NumberUtils.toInt(trimmedCoord);
    }
    
    /**
     * 验证坐标范围
     * 
     * @param x X坐标
     * @param y Y坐标
     * @param dataLine 数据行
     * @throws ParseException 如果坐标超出合理范围
     */
    private void validateCoordinateRange(int x, int y, String dataLine) throws ParseException {
        // 坐标值应为非负整数，并且在合理范围内
        if (x < 0 || y < 0) {
            throw new ParseException(String.format("坐标值不能为负数: x=%d, y=%d", x, y), 
                    dataLine, -1, getStrategyType());
        }
        
        // 可以根据实际应用场景设置坐标的最大值限制
        final int MAX_COORDINATE = 10000; // 可配置的最大坐标值
        if (x > MAX_COORDINATE || y > MAX_COORDINATE) {
            throw new ParseException(String.format("坐标值超出最大范围(%d): x=%d, y=%d", 
                    MAX_COORDINATE, x, y), dataLine, -1, getStrategyType());
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
        if (segments.length != 2) {
            return false;
        }
        
        // 检查坐标格式
        String[] coordinates = SPLITTER_COMMA.split(segments[1]);
        return coordinates.length == 2;
    }
    
    @Override
    public String getFormatDescription() {
        return "地理位置数据格式: ID\\tX坐标,Y坐标";
    }
    
    @Override
    public String getStrategyType() {
        return "GEO_INFO";
    }
}