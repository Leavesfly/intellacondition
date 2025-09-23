package com.leavesfly.iac.datasource;

import java.util.Map;

import com.leavesfly.iac.datasource.parser.DataParseStrategy;
import com.leavesfly.iac.datasource.parser.DataParseStrategyFactory;
import com.leavesfly.iac.datasource.parser.ParseException;
import com.leavesfly.iac.domain.GeoPoint;
import com.leavesfly.iac.execute.domain.UserComfortFunc;
import com.leavesfly.iac.train.domain.IntellacTrainDataItem;

/**
 * 重构后的领域对象解析器类
 * 
 * 该类采用策略模式和工厂模式设计，负责解析各种数据格式的字符串，
 * 将其转换为对应的领域对象。相比原版本，具有更好的扩展性和可维护性。
 * 
 * 主要改进：
 * 1. 采用策略模式，支持不同的解析策略
 * 2. 使用工厂模式创建解析策略实例
 * 3. 更完善的异常处理和错误信息
 * 4. 支持解析规则的验证
 */
public class RefactoredDomainParser {
    
    /**
     * 解析策略工厂
     */
    private final DataParseStrategyFactory strategyFactory;
    
    /**
     * 默认构造函数，使用默认策略工厂
     */
    public RefactoredDomainParser() {
        this.strategyFactory = DataParseStrategyFactory.getInstance();
    }
    
    /**
     * 构造函数，允许注入自定义策略工厂
     * 
     * @param strategyFactory 解析策略工厂
     */
    public RefactoredDomainParser(DataParseStrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory != null ? strategyFactory : DataParseStrategyFactory.getInstance();
    }
    
    /**
     * 解析训练数据
     * 
     * 输入格式：传感器ID\t温度\t室外温度\t功率值1,功率值2,...,功率值N
     * 示例：0\t22.5\t35.0\t20.4,120.3,114.9,297.6,298.3,190.0,198.7,83.5
     * 
     * @param strLine 训练数据字符串
     * @return 解析后的训练数据项
     * @throws ParseException 解析异常
     */
    public IntellacTrainDataItem parseTrainData(String strLine) throws ParseException {
        DataParseStrategy<IntellacTrainDataItem> strategy = 
                strategyFactory.createStrategy("TRAIN_DATA");
        return strategy.parse(strLine);
    }
    
    /**
     * 解析用户舒适度函数
     * 
     * 输入格式：用户ID\t期望温度
     * 示例：0\t23
     * 
     * @param strLine 用户舒适度数据字符串
     * @return 解析后的用户舒适度函数
     * @throws ParseException 解析异常
     */
    public UserComfortFunc parseUserComfortFunc(String strLine) throws ParseException {
        DataParseStrategy<UserComfortFunc> strategy = 
                strategyFactory.createStrategy("USER_COMFORT");
        return strategy.parse(strLine);
    }
    
    /**
     * 解析用户期望温度
     * 
     * 输入格式：用户ID\t期望温度
     * 示例：0\t23
     * 
     * @param strLine 用户期望温度数据字符串
     * @return 用户ID和期望温度的键值对
     * @throws ParseException 解析异常
     */
    public Map.Entry<String, Float> parseUserWantTemp(String strLine) throws ParseException {
        DataParseStrategy<Map.Entry<String, Float>> strategy = 
                strategyFactory.createStrategy("USER_WANT_TEMP");
        return strategy.parse(strLine);
    }
    
    /**
     * 解析地理位置信息
     * 
     * 输入格式：ID\tX坐标,Y坐标
     * 示例：0\t5,2
     * 
     * @param strLine 地理位置数据字符串
     * @return ID和地理位置的键值对
     * @throws ParseException 解析异常
     */
    public Map.Entry<String, GeoPoint> parseGeoInfo(String strLine) throws ParseException {
        DataParseStrategy<Map.Entry<String, GeoPoint>> strategy = 
                strategyFactory.createStrategy("GEO_INFO");
        return strategy.parse(strLine);
    }
    
    /**
     * 验证数据格式
     * 
     * @param strLine 数据字符串
     * @param dataType 数据类型
     * @return 如果格式正确返回true，否则返回false
     */
    public boolean validateFormat(String strLine, String dataType) {
        try {
            DataParseStrategy<?> strategy = strategyFactory.createStrategy(dataType);
            return strategy.validate(strLine);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 获取数据格式描述
     * 
     * @param dataType 数据类型
     * @return 格式描述
     */
    public String getFormatDescription(String dataType) {
        try {
            DataParseStrategy<?> strategy = strategyFactory.createStrategy(dataType);
            return strategy.getFormatDescription();
        } catch (Exception e) {
            return "未知数据类型: " + dataType;
        }
    }
    
    /**
     * 获取支持的数据类型
     * 
     * @return 支持的数据类型集合
     */
    public java.util.Set<String> getSupportedDataTypes() {
        return strategyFactory.getSupportedTypes();
    }
    
    /**
     * 静态方法，保持向后兼容性
     */
    
    /**
     * 解析训练数据（静态方法）
     * 
     * @param strLine 训练数据字符串
     * @return 解析后的训练数据项
     * @throws IllegalArgumentException 解析异常
     */
    public static IntellacTrainDataItem parseTrainData_Static(String strLine) {
        try {
            RefactoredDomainParser parser = new RefactoredDomainParser();
            return parser.parseTrainData(strLine);
        } catch (ParseException e) {
            throw new IllegalArgumentException("训练数据解析失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 解析用户舒适度函数（静态方法）
     * 
     * @param strLine 用户舒适度数据字符串
     * @return 解析后的用户舒适度函数
     * @throws IllegalArgumentException 解析异常
     */
    public static UserComfortFunc parseUserComfortFunc_Static(String strLine) {
        try {
            RefactoredDomainParser parser = new RefactoredDomainParser();
            return parser.parseUserComfortFunc(strLine);
        } catch (ParseException e) {
            throw new IllegalArgumentException("用户舒适度函数解析失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 解析用户期望温度（静态方法）
     * 
     * @param strLine 用户期望温度数据字符串
     * @return 用户ID和期望温度的键值对
     * @throws IllegalArgumentException 解析异常
     */
    public static Map.Entry<String, Float> parseUserWantTemp_Static(String strLine) {
        try {
            RefactoredDomainParser parser = new RefactoredDomainParser();
            return parser.parseUserWantTemp(strLine);
        } catch (ParseException e) {
            throw new IllegalArgumentException("用户期望温度解析失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 解析地理位置信息（静态方法）
     * 
     * @param strLine 地理位置数据字符串
     * @return ID和地理位置的键值对
     * @throws IllegalArgumentException 解析异常
     */
    public static Map.Entry<String, GeoPoint> parseGeoInfo_Static(String strLine) {
        try {
            RefactoredDomainParser parser = new RefactoredDomainParser();
            return parser.parseGeoInfo(strLine);
        } catch (ParseException e) {
            throw new IllegalArgumentException("地理位置信息解析失败: " + e.getMessage(), e);
        }
    }
}