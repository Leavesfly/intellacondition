package com.leavesfly.iac.datasource.parser;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.leavesfly.iac.datasource.parser.impl.TrainDataParseStrategy;
import com.leavesfly.iac.datasource.parser.impl.UserComfortParseStrategy;
import com.leavesfly.iac.datasource.parser.impl.GeoInfoParseStrategy;
import com.leavesfly.iac.datasource.parser.impl.UserWantTempParseStrategy;
import com.leavesfly.iac.domain.GeoPoint;
import com.leavesfly.iac.execute.domain.UserComfortFunc;
import com.leavesfly.iac.train.domain.IntellacTrainDataItem;

/**
 * 数据解析策略测试类
 */
public class DataParseStrategyTest {
    
    private TrainDataParseStrategy trainDataStrategy;
    private UserComfortParseStrategy userComfortStrategy;
    private GeoInfoParseStrategy geoInfoStrategy;
    private UserWantTempParseStrategy userWantTempStrategy;
    
    @Before
    public void setUp() {
        trainDataStrategy = new TrainDataParseStrategy();
        userComfortStrategy = new UserComfortParseStrategy();
        geoInfoStrategy = new GeoInfoParseStrategy();
        userWantTempStrategy = new UserWantTempParseStrategy();
    }
    
    @Test
    public void testTrainDataParseStrategy() throws ParseException {
        // 测试训练数据解析
        String validTrainData = "0\t22.5\t35.0\t20.4,120.3,114.9,297.6,298.3,190.0,198.7,83.5";
        
        IntellacTrainDataItem result = trainDataStrategy.parse(validTrainData);
        
        assertNotNull("解析结果不应为空", result);
        assertEquals("传感器ID应正确", "0", result.getSensorId());
        assertEquals("温度值应正确", 22.5f, result.getTemperature(), 0.01f);
        assertEquals("室外温度应正确", 35.0f, result.getOutsideTemp(), 0.01f);
        assertNotNull("功率向量不应为空", result.getPowerVector());
    }
    
    @Test
    public void testTrainDataValidation() {
        // 测试有效数据验证
        String validData = "0\t22.5\t35.0\t20.4,120.3,114.9";
        assertTrue("有效数据应通过验证", trainDataStrategy.validate(validData));
        
        // 测试无效数据验证
        String invalidData1 = "0\t22.5\t35.0"; // 缺少功率值
        assertFalse("缺少功率值的数据应不通过验证", trainDataStrategy.validate(invalidData1));
        
        String invalidData2 = "0\tabc\t35.0\t20.4"; // 温度值无效
        assertFalse("温度值无效的数据应不通过验证", trainDataStrategy.validate(invalidData2));
        
        String invalidData3 = ""; // 空字符串
        assertFalse("空字符串应不通过验证", trainDataStrategy.validate(invalidData3));
        
        String invalidData4 = null; // null值
        assertFalse("null值应不通过验证", trainDataStrategy.validate(invalidData4));
    }
    
    @Test
    public void testUserComfortParseStrategy() throws ParseException {
        // 测试用户舒适度解析
        String validComfortData = "0\t23";
        
        UserComfortFunc result = userComfortStrategy.parse(validComfortData);
        
        assertNotNull("解析结果不应为空", result);
        assertEquals("用户ID应正确", "0", result.getUserId());
        assertNotNull("温度范围不应为空", result.getUserTempRange());
    }
    
    @Test
    public void testUserComfortValidation() {
        // 测试有效数据验证
        String validData = "0\t23";
        assertTrue("有效数据应通过验证", userComfortStrategy.validate(validData));
        
        // 测试无效数据验证
        String invalidData1 = "0"; // 缺少温度值
        assertFalse("缺少温度值的数据应不通过验证", userComfortStrategy.validate(invalidData1));
        
        String invalidData2 = "0\tabc"; // 温度值无效
        assertFalse("温度值无效的数据应不通过验证", userComfortStrategy.validate(invalidData2));
    }
    
    @Test
    public void testGeoInfoParseStrategy() throws ParseException {
        // 测试地理位置信息解析
        String validGeoData = "0\t5,2";
        
        Map.Entry<String, GeoPoint> result = geoInfoStrategy.parse(validGeoData);
        
        assertNotNull("解析结果不应为空", result);
        assertEquals("ID应正确", "0", result.getKey());
        
        GeoPoint geoPoint = result.getValue();
        assertNotNull("地理位置不应为空", geoPoint);
        assertEquals("X坐标应正确", 5, geoPoint.getX());
        assertEquals("Y坐标应正确", 2, geoPoint.getY());
    }
    
    @Test
    public void testGeoInfoValidation() {
        // 测试有效数据验证
        String validData = "0\t5,2";
        assertTrue("有效数据应通过验证", geoInfoStrategy.validate(validData));
        
        // 测试无效数据验证
        String invalidData1 = "0\t5"; // 缺少Y坐标
        assertFalse("缺少Y坐标的数据应不通过验证", geoInfoStrategy.validate(invalidData1));
        
        String invalidData2 = "0\tabc,def"; // 坐标值无效
        assertFalse("坐标值无效的数据应不通过验证", geoInfoStrategy.validate(invalidData2));
        
        String invalidData3 = "0\t5,2,3"; // 坐标过多
        assertFalse("坐标过多的数据应不通过验证", geoInfoStrategy.validate(invalidData3));
    }
    
    @Test
    public void testUserWantTempParseStrategy() throws ParseException {
        // 测试用户期望温度解析
        String validTempData = "0\t23.5";
        
        Map.Entry<String, Float> result = userWantTempStrategy.parse(validTempData);
        
        assertNotNull("解析结果不应为空", result);
        assertEquals("用户ID应正确", "0", result.getKey());
        assertEquals("期望温度应正确", 23.5f, result.getValue(), 0.01f);
    }
    
    @Test(expected = ParseException.class)
    public void testParseExceptionHandling() throws ParseException {
        // 测试解析异常处理
        String invalidData = "invalid\tdata\tformat";
        trainDataStrategy.parse(invalidData);
    }
    
    @Test
    public void testStrategyTypes() {
        // 测试策略类型标识
        assertEquals("训练数据策略类型", "TRAIN_DATA", trainDataStrategy.getStrategyType());
        assertEquals("用户舒适度策略类型", "USER_COMFORT", userComfortStrategy.getStrategyType());
        assertEquals("地理位置策略类型", "GEO_INFO", geoInfoStrategy.getStrategyType());
        assertEquals("用户期望温度策略类型", "USER_WANT_TEMP", userWantTempStrategy.getStrategyType());
    }
    
    @Test
    public void testFormatDescriptions() {
        // 测试格式描述
        assertNotNull("训练数据格式描述不应为空", trainDataStrategy.getFormatDescription());
        assertNotNull("用户舒适度格式描述不应为空", userComfortStrategy.getFormatDescription());
        assertNotNull("地理位置格式描述不应为空", geoInfoStrategy.getFormatDescription());
        assertNotNull("用户期望温度格式描述不应为空", userWantTempStrategy.getFormatDescription());
        
        assertTrue("格式描述应包含有用信息", 
                trainDataStrategy.getFormatDescription().contains("传感器ID"));
        assertTrue("格式描述应包含有用信息", 
                userComfortStrategy.getFormatDescription().contains("用户ID"));
    }
    
    @Test
    public void testBoundaryValues() throws ParseException {
        // 测试边界值
        
        // 测试极端温度值
        String extremeTempData = "0\t0.0";
        UserComfortFunc extremeResult = userComfortStrategy.parse(extremeTempData);
        assertNotNull("极端温度值应能正确解析", extremeResult);
        
        // 测试极端坐标值
        String extremeGeoData = "0\t0,0";
        Map.Entry<String, GeoPoint> extremeGeoResult = geoInfoStrategy.parse(extremeGeoData);
        assertNotNull("极端坐标值应能正确解析", extremeGeoResult);
        assertEquals("极端X坐标应正确", 0, extremeGeoResult.getValue().getX());
        assertEquals("极端Y坐标应正确", 0, extremeGeoResult.getValue().getY());
    }
}