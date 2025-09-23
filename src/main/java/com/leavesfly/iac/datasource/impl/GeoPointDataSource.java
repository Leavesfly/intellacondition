package com.leavesfly.iac.datasource.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.leavesfly.iac.datasource.AbstractFileDataSource;
import com.leavesfly.iac.datasource.ResourceUtil;
import com.leavesfly.iac.domain.GeoPoint;
import com.leavesfly.iac.datasource.DomainParser;

/**
 * 地理位置数据源实现类
 * 
 * 该类负责加载和管理地理位置信息，支持用户位置和传感器位置数据的读取。
 * 数据格式：ID\tX坐标,Y坐标
 */
public class GeoPointDataSource extends AbstractFileDataSource<Map<String, GeoPoint>> {
    
    /**
     * 缓存的地理位置数据
     */
    private volatile Map<String, GeoPoint> geoPointCache;
    
    /**
     * 构造函数
     * 
     * @param resourceName 资源文件名
     * @param description 数据描述
     */
    public GeoPointDataSource(String resourceName, String description) {
        super(resourceName, description);
    }
    
    @Override
    public Map<String, GeoPoint> load() throws IOException {
        if (geoPointCache != null) {
            return new HashMap<>(geoPointCache);
        }
        
        synchronized (this) {
            if (geoPointCache == null) {
                validateFileAccess();
                geoPointCache = loadGeoPointData();
            }
        }
        
        return new HashMap<>(geoPointCache);
    }
    
    /**
     * 从文件加载地理位置数据
     * 
     * @return 地理位置数据映射表
     * @throws IOException 文件读取异常
     */
    private Map<String, GeoPoint> loadGeoPointData() throws IOException {
        Map<String, GeoPoint> geoPointMap = new HashMap<>();
        
        try (BufferedReader reader = ResourceUtil.loadTxtResource(resourceName)) {
            String line;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                if (StringUtils.isBlank(line) || line.trim().startsWith("#")) {
                    continue; // 跳过空行和注释行
                }
                
                try {
                    Map.Entry<String, GeoPoint> entry = DomainParser.parseGeoInfo(line.trim());
                    if (entry != null) {
                        geoPointMap.put(entry.getKey(), entry.getValue());
                    }
                } catch (Exception e) {
                    throw new IOException(String.format(
                            "解析地理位置数据失败，文件: %s, 行号: %d, 内容: %s, 错误: %s", 
                            resourceName, lineNumber, line, e.getMessage()), e);
                }
            }
        }
        
        return geoPointMap;
    }
    
    @Override
    public void save(Map<String, GeoPoint> data) throws IOException {
        throw new UnsupportedOperationException("地理位置数据源不支持保存操作");
    }
    
    @Override
    public void refresh() throws IOException {
        synchronized (this) {
            geoPointCache = null;
        }
    }
    
    /**
     * 获取指定ID的地理位置
     * 
     * @param id 位置ID
     * @return 地理位置，如果不存在返回null
     * @throws IOException 数据加载异常
     */
    public GeoPoint getGeoPoint(String id) throws IOException {
        Map<String, GeoPoint> data = load();
        return data.get(id);
    }
    
    /**
     * 获取所有地理位置ID
     * 
     * @return 地理位置ID集合
     * @throws IOException 数据加载异常
     */
    public Collection<String> getAllIds() throws IOException {
        Map<String, GeoPoint> data = load();
        return data.keySet();
    }
    
    /**
     * 获取数据记录数量
     * 
     * @return 记录数量
     * @throws IOException 数据加载异常
     */
    public int getSize() throws IOException {
        Map<String, GeoPoint> data = load();
        return data.size();
    }
}