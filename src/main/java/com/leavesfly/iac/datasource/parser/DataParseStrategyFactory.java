package com.leavesfly.iac.datasource.parser;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import com.leavesfly.iac.datasource.parser.impl.GeoInfoParseStrategy;
import com.leavesfly.iac.datasource.parser.impl.TrainDataParseStrategy;
import com.leavesfly.iac.datasource.parser.impl.UserComfortParseStrategy;

/**
 * 数据解析策略工厂类
 * 
 * 该类负责创建和管理各种数据解析策略实例，采用工厂模式和单例模式。
 * 支持解析策略的注册、创建和缓存。
 */
public class DataParseStrategyFactory {
    
    /**
     * 解析策略工厂单例实例
     */
    private static volatile DataParseStrategyFactory instance;
    
    /**
     * 解析策略创建器映射表
     */
    private final Map<String, Supplier<DataParseStrategy<?>>> strategyCreators;
    
    /**
     * 解析策略实例缓存
     */
    private final Map<String, DataParseStrategy<?>> strategyCache;
    
    /**
     * 私有构造函数
     */
    private DataParseStrategyFactory() {
        this.strategyCreators = new ConcurrentHashMap<>();
        this.strategyCache = new ConcurrentHashMap<>();
        initDefaultStrategies();
    }
    
    /**
     * 获取工厂单例实例
     * 
     * @return 解析策略工厂实例
     */
    public static DataParseStrategyFactory getInstance() {
        if (instance == null) {
            synchronized (DataParseStrategyFactory.class) {
                if (instance == null) {
                    instance = new DataParseStrategyFactory();
                }
            }
        }
        return instance;
    }
    
    /**
     * 初始化默认解析策略
     */
    private void initDefaultStrategies() {
        // 注册训练数据解析策略
        registerStrategy("TRAIN_DATA", TrainDataParseStrategy::new);
        
        // 注册用户舒适度数据解析策略
        registerStrategy("USER_COMFORT", UserComfortParseStrategy::new);
        
        // 注册地理位置信息解析策略
        registerStrategy("GEO_INFO", GeoInfoParseStrategy::new);
        
        // 注册用户期望温度解析策略
        registerStrategy("USER_WANT_TEMP", com.leavesfly.iac.datasource.parser.impl.UserWantTempParseStrategy::new);
    }
    
    /**
     * 注册解析策略创建器
     * 
     * @param strategyType 策略类型
     * @param creator 策略创建器
     */
    public void registerStrategy(String strategyType, Supplier<DataParseStrategy<?>> creator) {
        if (strategyType == null || strategyType.trim().isEmpty()) {
            throw new IllegalArgumentException("策略类型不能为空");
        }
        if (creator == null) {
            throw new IllegalArgumentException("策略创建器不能为空");
        }
        strategyCreators.put(strategyType, creator);
    }
    
    /**
     * 创建解析策略实例
     * 
     * @param strategyType 策略类型
     * @return 解析策略实例
     * @throws IllegalArgumentException 如果策略类型不支持
     */
    @SuppressWarnings("unchecked")
    public <T> DataParseStrategy<T> createStrategy(String strategyType) {
        if (strategyType == null || strategyType.trim().isEmpty()) {
            throw new IllegalArgumentException("策略类型不能为空");
        }
        
        // 从缓存中获取
        DataParseStrategy<?> cachedStrategy = strategyCache.get(strategyType);
        if (cachedStrategy != null) {
            return (DataParseStrategy<T>) cachedStrategy;
        }
        
        // 创建新实例
        Supplier<DataParseStrategy<?>> creator = strategyCreators.get(strategyType);
        if (creator == null) {
            throw new IllegalArgumentException("不支持的解析策略类型: " + strategyType);
        }
        
        DataParseStrategy<?> strategy = creator.get();
        strategyCache.put(strategyType, strategy);
        return (DataParseStrategy<T>) strategy;
    }
    
    /**
     * 获取所有已注册的策略类型
     * 
     * @return 策略类型集合
     */
    public java.util.Set<String> getSupportedTypes() {
        return strategyCreators.keySet();
    }
    
    /**
     * 清除策略缓存
     */
    public void clearCache() {
        strategyCache.clear();
    }
    
    /**
     * 清除指定类型的策略缓存
     * 
     * @param strategyType 策略类型
     */
    public void clearCache(String strategyType) {
        if (strategyType != null) {
            strategyCache.remove(strategyType);
        }
    }
    
    /**
     * 检查是否支持指定的策略类型
     * 
     * @param strategyType 策略类型
     * @return 如果支持返回true，否则返回false
     */
    public boolean isSupported(String strategyType) {
        return strategyCreators.containsKey(strategyType);
    }
}