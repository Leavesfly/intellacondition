package com.leavesfly.iac.datasource;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.leavesfly.iac.domain.GeoPoint;
import com.leavesfly.iac.datasource.impl.GeoPointDataSource;

/**
 * 地理位置数据源测试类
 */
public class GeoPointDataSourceTest {
    
    private GeoPointDataSource userGeoDataSource;
    private GeoPointDataSource sensorGeoDataSource;
    
    @Before
    public void setUp() {
        userGeoDataSource = new GeoPointDataSource("user_geo_table.txt", "用户地理位置");
        sensorGeoDataSource = new GeoPointDataSource("sensor_geo_table.txt", "传感器地理位置");
    }
    
    @Test
    public void testLoadUserGeoData() throws IOException {
        // 测试加载用户地理位置数据
        Map<String, GeoPoint> userGeoData = userGeoDataSource.load();
        
        assertNotNull("用户地理位置数据不应为空", userGeoData);
        assertFalse("用户地理位置数据不应为空集合", userGeoData.isEmpty());
        
        // 验证数据格式
        for (Map.Entry<String, GeoPoint> entry : userGeoData.entrySet()) {
            assertNotNull("用户ID不应为空", entry.getKey());
            assertNotNull("地理位置不应为空", entry.getValue());
            
            GeoPoint geoPoint = entry.getValue();
            assertTrue("X坐标应为非负数", geoPoint.getX() >= 0);
            assertTrue("Y坐标应为非负数", geoPoint.getY() >= 0);
        }
    }
    
    @Test
    public void testLoadSensorGeoData() throws IOException {
        // 测试加载传感器地理位置数据
        Map<String, GeoPoint> sensorGeoData = sensorGeoDataSource.load();
        
        assertNotNull("传感器地理位置数据不应为空", sensorGeoData);
        assertFalse("传感器地理位置数据不应为空集合", sensorGeoData.isEmpty());
        
        // 验证特定传感器的位置
        assertTrue("应包含传感器0", sensorGeoData.containsKey("0"));
        
        GeoPoint sensor0Location = sensorGeoData.get("0");
        assertNotNull("传感器0的位置不应为空", sensor0Location);
    }
    
    @Test
    public void testGetGeoPoint() throws IOException {
        // 测试获取指定ID的地理位置
        GeoPoint sensor0Location = sensorGeoDataSource.getGeoPoint("0");
        assertNotNull("传感器0的位置不应为空", sensor0Location);
        
        // 测试不存在的ID
        GeoPoint nonExistentLocation = sensorGeoDataSource.getGeoPoint("999");
        assertNull("不存在的传感器位置应为空", nonExistentLocation);
    }
    
    @Test
    public void testGetSize() throws IOException {
        // 测试获取数据记录数量
        int sensorCount = sensorGeoDataSource.getSize();
        assertTrue("传感器数量应大于0", sensorCount > 0);
        
        int userCount = userGeoDataSource.getSize();
        assertTrue("用户数量应大于0", userCount > 0);
    }
    
    @Test
    public void testIsAvailable() {
        // 测试数据源可用性
        assertTrue("用户地理位置数据源应可用", userGeoDataSource.isAvailable());
        assertTrue("传感器地理位置数据源应可用", sensorGeoDataSource.isAvailable());
    }
    
    @Test
    public void testGetType() {
        // 测试获取数据源类型
        assertEquals("用户地理位置数据源类型", "用户地理位置", userGeoDataSource.getType());
        assertEquals("传感器地理位置数据源类型", "传感器地理位置", sensorGeoDataSource.getType());
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testSaveNotSupported() throws IOException {
        // 测试保存操作不被支持
        userGeoDataSource.save(null);
    }
    
    @Test
    public void testDataCaching() throws IOException {
        // 测试数据缓存功能
        Map<String, GeoPoint> data1 = userGeoDataSource.load();
        Map<String, GeoPoint> data2 = userGeoDataSource.load();
        
        // 虽然返回的是新Map实例，但数据应该一致
        assertEquals("缓存的数据应该一致", data1.size(), data2.size());
        
        for (String key : data1.keySet()) {
            assertTrue("缓存数据应包含相同的键", data2.containsKey(key));
            assertEquals("缓存数据应包含相同的值", data1.get(key), data2.get(key));
        }
    }
    
    @Test
    public void testRefresh() throws IOException {
        // 测试刷新功能
        Map<String, GeoPoint> dataBefore = userGeoDataSource.load();
        
        // 刷新数据源
        userGeoDataSource.refresh();
        
        Map<String, GeoPoint> dataAfter = userGeoDataSource.load();
        
        // 数据应该保持一致（因为底层文件没有变化）
        assertEquals("刷新后数据大小应一致", dataBefore.size(), dataAfter.size());
    }
}