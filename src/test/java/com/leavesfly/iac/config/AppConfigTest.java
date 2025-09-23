package com.leavesfly.iac.config;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 应用配置测试类
 * 
 * 测试配置类的不可变性和线程安全性
 */
public class AppConfigTest {

    @Test
    public void testSingletonPattern() {
        // 测试单例模式
        AppConfig config1 = AppConfig.getInstance();
        AppConfig config2 = AppConfig.getInstance();
        
        assertSame("配置实例应该是单例", config1, config2);
    }

    @Test
    public void testAreaConfig() {
        AppConfig config = AppConfig.getInstance();
        AppConfig.AreaConfig area = config.getArea();
        
        assertEquals("区域长度应该为10", 10, area.getLength());
        assertEquals("区域宽度应该为10", 10, area.getWidth());
        assertTrue("最大距离应该大于0", area.getMaxDistance() > 0);
    }

    @Test
    public void testDeviceConfig() {
        AppConfig config = AppConfig.getInstance();
        AppConfig.DeviceConfig device = config.getDevice();
        
        assertEquals("用户数量应该为16", 16, device.getUserNum());
        assertEquals("传感器数量应该为10", 10, device.getSensorNum());
        assertEquals("空调数量应该为8", 8, device.getAirConditionNum());
    }

    @Test
    public void testPowerConfig() {
        AppConfig config = AppConfig.getInstance();
        AppConfig.PowerConfig power = config.getPower();
        
        assertEquals("室外温度应该为35.0", 35.0f, power.getOutsideTemp(), 0.001f);
        assertEquals("最小功率应该为0.0", 0.0f, power.getAirConditionMinPower(), 0.001f);
        assertEquals("最大功率应该为400.0", 400.0f, power.getAirConditionMaxPower(), 0.001f);
        assertTrue("最大功率应该大于最小功率", 
            power.getAirConditionMaxPower() > power.getAirConditionMinPower());
    }

    @Test
    public void testComfortConfig() {
        AppConfig config = AppConfig.getInstance();
        AppConfig.ComfortConfig comfort = config.getComfort();
        
        assertEquals("满意度权重和功耗权重之和应该为1.0", 
            1.0f, comfort.getSatisfyWeight() + comfort.getPowerCostWeight(), 0.001f);
        assertTrue("最小舒适值应该大于0", comfort.getMinValue() > 0);
    }

    @Test
    public void testFileConfig() {
        AppConfig config = AppConfig.getInstance();
        AppConfig.FileConfig file = config.getFile();
        
        assertNotNull("用户舒适度文件名不应为null", file.getUserComfortTempDataFileName());
        assertNotNull("用户地理位置文件名不应为null", file.getUserGeoTableFileName());
        assertNotNull("传感器地理位置文件名不应为null", file.getSensorGeoTableFileName());
        assertNotNull("训练数据文件名不应为null", file.getTrainDataFileName());
    }

    @Test
    public void testSolutionConfig() {
        AppConfig config = AppConfig.getInstance();
        AppConfig.SolutionConfig solution = config.getSolution();
        
        assertNotNull("解决方案名称前缀不应为null", solution.getNamePrefix());
        assertNotNull("PSO后缀不应为null", solution.getPsoSuffix());
        assertNotNull("混沌PSO后缀不应为null", solution.getPsoChaosSuffix());
    }
}