package com.leavesfly.iac.datasource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import com.leavesfly.iac.datasource.impl.GeoPointDataSource;
import com.leavesfly.iac.datasource.impl.UserComfortDataSource;

/**
 * 数据源工厂类
 * 
 * 该类负责创建和管理各种类型的数据源实例，采用工厂模式和单例模式。
 * 支持数据源的注册、创建和缓存。
 */
public class DataSourceFactory {
    
    /**
     * 数据源工厂单例实例
     */
    private static volatile DataSourceFactory instance;
    
    /**
     * 数据源创建器映射表
     */
    private final Map<String, Supplier<DataSource<?>>> dataSourceCreators;
    
    /**
     * 数据源实例缓存
     */
    private final Map<String, DataSource<?>> dataSourceCache;
    
    /**
     * 私有构造函数
     */
    private DataSourceFactory() {
        this.dataSourceCreators = new ConcurrentHashMap<>();
        this.dataSourceCache = new ConcurrentHashMap<>();
        initDefaultDataSources();
    }
    
    /**
     * 获取工厂单例实例
     * 
     * @return 数据源工厂实例
     */
    public static DataSourceFactory getInstance() {
        if (instance == null) {
            synchronized (DataSourceFactory.class) {
                if (instance == null) {
                    instance = new DataSourceFactory();
                }
            }
        }
        return instance;
    }
    
    /**
     * 初始化默认数据源
     */
    private void initDefaultDataSources() {
        // 注册用户舒适度数据源
        registerDataSource("user_comfort", () -> 
            new UserComfortDataSource("user_comfort_temp.txt"));
        
        // 注册用户地理位置数据源
        registerDataSource("user_geo", () -> 
            new GeoPointDataSource("user_geo_table.txt", "用户地理位置"));
        
        // 注册传感器地理位置数据源
        registerDataSource("sensor_geo", () -> 
            new GeoPointDataSource("sensor_geo_table.txt", "传感器地理位置"));
    }
    
    /**
     * 注册数据源创建器
     * 
     * @param type 数据源类型
     * @param creator 数据源创建器
     */
    public void registerDataSource(String type, Supplier<DataSource<?>> creator) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("数据源类型不能为空");
        }
        if (creator == null) {
            throw new IllegalArgumentException("数据源创建器不能为空");
        }
        dataSourceCreators.put(type, creator);
    }
    
    /**
     * 创建数据源实例
     * 
     * @param type 数据源类型
     * @return 数据源实例
     * @throws IllegalArgumentException 如果数据源类型不支持
     */
    @SuppressWarnings("unchecked")
    public <T> DataSource<T> createDataSource(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("数据源类型不能为空");
        }
        
        // 从缓存中获取
        DataSource<?> cachedDataSource = dataSourceCache.get(type);
        if (cachedDataSource != null) {
            return (DataSource<T>) cachedDataSource;
        }
        
        // 创建新实例
        Supplier<DataSource<?>> creator = dataSourceCreators.get(type);
        if (creator == null) {
            throw new IllegalArgumentException("不支持的数据源类型: " + type);
        }
        
        DataSource<?> dataSource = creator.get();
        dataSourceCache.put(type, dataSource);
        return (DataSource<T>) dataSource;
    }
    
    /**
     * 获取所有已注册的数据源类型
     * 
     * @return 数据源类型集合
     */
    public java.util.Set<String> getSupportedTypes() {
        return dataSourceCreators.keySet();
    }
    
    /**
     * 清除数据源缓存
     */
    public void clearCache() {
        dataSourceCache.clear();
    }
    
    /**
     * 清除指定类型的数据源缓存
     * 
     * @param type 数据源类型
     */
    public void clearCache(String type) {
        if (type != null) {
            dataSourceCache.remove(type);
        }
    }
}