package com.leavesfly.iac.datasource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.leavesfly.iac.config.AppContextConstant;
import com.leavesfly.iac.domain.GeoPoint;
import com.leavesfly.iac.domain.PtFitFunc;
import com.leavesfly.iac.domain.PowerRange;
import com.leavesfly.iac.evalute.EvaluteResult;
import com.leavesfly.iac.execute.domain.UserComfortFunc;
import com.leavesfly.iac.datasource.impl.UserComfortDataSource;

/**
 * 重构后的数据工厂类
 * 
 * 该类采用建造者模式和依赖注入设计，提供系统所需的各种数据管理功能。
 * 主要职责：
 * 1. 管理各种数据源的访问
 * 2. 提供数据缓存和延迟加载
 * 3. 管理评估结果
 * 4. 计算用户与传感器的关系映射
 * 
 * 采用线程安全的设计确保多线程环境下的数据一致性。
 */
public class RefactoredDataFactory {
    
    /**
     * 数据源工厂
     */
    private final DataSourceFactory dataSourceFactory;
    
    /**
     * 传感器温度预测函数集合
     */
    private volatile Collection<PtFitFunc> sensorFitFuncSet;
    
    /**
     * 传感器温度预测函数映射表（传感器ID -> 温度预测函数）
     */
    private volatile Map<String, PtFitFunc> sensorFitFuncMap;
    
    /**
     * 用户ID与周围传感器ID列表的映射表
     */
    private volatile Map<String, List<String>> userIdSensorIdsMap;
    
    /**
     * 用户ID与周围传感器温度预测函数列表的映射表
     */
    private volatile Map<String, List<PtFitFunc>> userIdSensorFuncsMap;
    
    /**
     * 功率范围数组
     */
    private volatile PowerRange[] powerRangeArray;
    
    /**
     * 评估结果映射表（解决方案名称 -> 评估结果）
     */
    private final Map<String, EvaluteResult> evaluteResultMap = Collections
            .synchronizedMap(new LinkedHashMap<String, EvaluteResult>());
    
    /**
     * 建造者类
     */
    public static class Builder {
        private DataSourceFactory dataSourceFactory = DataSourceFactory.getInstance();
        
        public Builder withDataSourceFactory(DataSourceFactory dataSourceFactory) {
            this.dataSourceFactory = dataSourceFactory;
            return this;
        }
        
        public RefactoredDataFactory build() {
            return new RefactoredDataFactory(this);
        }
    }
    
    /**
     * 私有构造函数
     */
    private RefactoredDataFactory(Builder builder) {
        this.dataSourceFactory = builder.dataSourceFactory;
    }
    
    /**
     * 获取用户舒适度函数集合
     * 
     * @return 用户舒适度函数集合
     * @throws RuntimeException 数据加载异常
     */
    public Collection<UserComfortFunc> getUserComfortFuncCollection() {
        try {
            DataSource<UserComfortDataSource.UserComfortData> userComfortDataSource = 
                    dataSourceFactory.createDataSource("user_comfort");
            UserComfortDataSource.UserComfortData data = userComfortDataSource.load();
            return data.getUserComfortFuncCollection();
        } catch (IOException e) {
            throw new RuntimeException("加载用户舒适度函数失败", e);
        }
    }
    
    /**
     * 获取用户舒适度函数映射表
     * 
     * @return 用户舒适度函数映射表（用户ID -> 舒适度函数）
     * @throws RuntimeException 数据加载异常
     */
    public Map<String, UserComfortFunc> getUserComfortFuncMap() {
        try {
            DataSource<UserComfortDataSource.UserComfortData> userComfortDataSource = 
                    dataSourceFactory.createDataSource("user_comfort");
            UserComfortDataSource.UserComfortData data = userComfortDataSource.load();
            return data.getUserComfortFuncMap();
        } catch (IOException e) {
            throw new RuntimeException("加载用户舒适度函数映射表失败", e);
        }
    }
    
    /**
     * 获取用户期望温度映射表
     * 
     * @return 用户期望温度映射表（用户ID -> 期望温度）
     * @throws RuntimeException 数据加载异常
     */
    public Map<String, Float> getUserWantTempMap() {
        try {
            DataSource<UserComfortDataSource.UserComfortData> userComfortDataSource = 
                    dataSourceFactory.createDataSource("user_comfort");
            UserComfortDataSource.UserComfortData data = userComfortDataSource.load();
            return data.getUserWantTempMap();
        } catch (IOException e) {
            throw new RuntimeException("加载用户期望温度映射表失败", e);
        }
    }
    
    /**
     * 注册传感器的温度预测函数集合
     * 
     * @param registerFitFuncOfPTSet 温度预测函数集合
     */
    public void registerFitFunc(Collection<PtFitFunc> registerFitFuncOfPTSet) {
        if (registerFitFuncOfPTSet == null) {
            throw new IllegalArgumentException("温度预测函数集合不能为空");
        }
        
        this.sensorFitFuncSet = registerFitFuncOfPTSet;
        this.sensorFitFuncMap = new HashMap<>();
        
        for (PtFitFunc ptfitFunc : registerFitFuncOfPTSet) {
            if (ptfitFunc != null && ptfitFunc.getSensorId() != null) {
                sensorFitFuncMap.put(ptfitFunc.getSensorId(), ptfitFunc);
            }
        }
        
        // 清除相关缓存
        clearRelatedCache();
    }
    
    /**
     * 获取传感器的温度预测函数集合
     * 
     * @return 温度预测函数集合
     * @throws RuntimeException 如果函数集合未注册
     */
    public Collection<PtFitFunc> getSensorFitFuncSet() {
        if (sensorFitFuncSet == null) {
            throw new RuntimeException("传感器温度预测函数集合未注册");
        }
        return sensorFitFuncSet;
    }
    
    /**
     * 获取传感器的温度预测函数映射表
     * 
     * @return 温度预测函数映射表（传感器ID -> 温度预测函数）
     * @throws RuntimeException 如果函数映射表未注册
     */
    public Map<String, PtFitFunc> getSensorFitFuncMap() {
        if (sensorFitFuncMap == null) {
            throw new RuntimeException("传感器温度预测函数映射表未注册");
        }
        return sensorFitFuncMap;
    }
    
    /**
     * 获取传感器ID集合
     * 
     * @return 传感器ID集合
     * @throws RuntimeException 数据加载异常
     */
    public Set<String> getSensorIdSet() {
        Map<String, GeoPoint> sensorGeoTable = getSensorGeoInfo();
        return sensorGeoTable.keySet();
    }
    
    /**
     * 获取用户ID周围一定范围内的传感器ID列表的映射表
     * 
     * @return 用户ID与周围传感器ID列表的映射表
     * @throws RuntimeException 数据加载异常
     */
    public Map<String, List<String>> getSensorIdsByUserId() {
        if (userIdSensorIdsMap != null) {
            return userIdSensorIdsMap;
        }
        
        synchronized (this) {
            if (userIdSensorIdsMap == null) {
                userIdSensorIdsMap = calculateUserSensorMapping();
            }
        }
        
        return userIdSensorIdsMap;
    }
    
    /**
     * 计算用户与传感器的映射关系
     * 
     * @return 用户ID与周围传感器ID列表的映射表
     */
    private Map<String, List<String>> calculateUserSensorMapping() {
        Map<String, GeoPoint> userGeoTable = getUserGeoInfo();
        Map<String, GeoPoint> sensorGeoTable = getSensorGeoInfo();
        Map<String, List<String>> mapping = new HashMap<>();
        
        for (Map.Entry<String, GeoPoint> userEntry : userGeoTable.entrySet()) {
            String userId = userEntry.getKey();
            GeoPoint userGeo = userEntry.getValue();
            
            for (Map.Entry<String, GeoPoint> sensorEntry : sensorGeoTable.entrySet()) {
                String sensorId = sensorEntry.getKey();
                GeoPoint sensorGeo = sensorEntry.getValue();
                
                if (userGeo.getDistance(sensorGeo) <= AppContextConstant.MAX_DISTANCE) {
                    mapping.computeIfAbsent(userId, k -> new ArrayList<>()).add(sensorId);
                }
            }
        }
        
        return mapping;
    }
    
    /**
     * 获取用户ID周围一定范围内的传感器的功率-温度映射函数的映射表
     * 
     * @return 用户ID与周围传感器温度预测函数列表的映射表
     * @throws RuntimeException 数据加载异常
     */
    public Map<String, List<PtFitFunc>> getSensorFuncByUserId() {
        if (userIdSensorFuncsMap != null) {
            return userIdSensorFuncsMap;
        }
        
        synchronized (this) {
            if (userIdSensorFuncsMap == null) {
                userIdSensorFuncsMap = calculateUserSensorFuncMapping();
            }
        }
        
        return userIdSensorFuncsMap;
    }
    
    /**
     * 计算用户与传感器函数的映射关系
     * 
     * @return 用户ID与周围传感器温度预测函数列表的映射表
     */
    private Map<String, List<PtFitFunc>> calculateUserSensorFuncMapping() {
        if (sensorFitFuncMap == null) {
            throw new RuntimeException("传感器温度预测函数映射表未注册");
        }
        
        Map<String, List<String>> userSensorIds = getSensorIdsByUserId();
        Map<String, List<PtFitFunc>> mapping = new HashMap<>();
        
        for (Map.Entry<String, List<String>> entry : userSensorIds.entrySet()) {
            String userId = entry.getKey();
            List<String> sensorIds = entry.getValue();
            
            List<PtFitFunc> sensorFuncs = new ArrayList<>();
            for (String sensorId : sensorIds) {
                PtFitFunc func = sensorFitFuncMap.get(sensorId);
                if (func != null) {
                    sensorFuncs.add(func);
                }
            }
            
            mapping.put(userId, sensorFuncs);
        }
        
        return mapping;
    }
    
    /**
     * 获取用户地理位置信息映射表
     * 
     * @return 用户地理位置信息映射表（用户ID -> 地理位置）
     * @throws RuntimeException 数据加载异常
     */
    private Map<String, GeoPoint> getUserGeoInfo() {
        try {
            DataSource<Map<String, GeoPoint>> userGeoDataSource = 
                    dataSourceFactory.createDataSource("user_geo");
            return userGeoDataSource.load();
        } catch (IOException e) {
            throw new RuntimeException("加载用户地理位置信息失败", e);
        }
    }
    
    /**
     * 获取传感器地理位置信息映射表
     * 
     * @return 传感器地理位置信息映射表（传感器ID -> 地理位置）
     * @throws RuntimeException 数据加载异常
     */
    private Map<String, GeoPoint> getSensorGeoInfo() {
        try {
            DataSource<Map<String, GeoPoint>> sensorGeoDataSource = 
                    dataSourceFactory.createDataSource("sensor_geo");
            return sensorGeoDataSource.load();
        } catch (IOException e) {
            throw new RuntimeException("加载传感器地理位置信息失败", e);
        }
    }
    
    /**
     * 获取功率调度的范围约束数组
     * 
     * @return 功率范围数组
     */
    public PowerRange[] getPowerRangeArray() {
        if (powerRangeArray != null) {
            return powerRangeArray;
        }
        
        synchronized (this) {
            if (powerRangeArray == null) {
                powerRangeArray = new PowerRange[AppContextConstant.AIR_CONDITION_NUM];
                for (int i = 0; i < powerRangeArray.length; i++) {
                    powerRangeArray[i] = new PowerRange(
                            AppContextConstant.AIR_CONDITION_MIN_POWER,
                            AppContextConstant.AIR_CONDITION_MAX_POWER);
                }
            }
        }
        
        return powerRangeArray;
    }
    
    /**
     * 获取评估结果映射表
     * 
     * @return 评估结果映射表（解决方案名称 -> 评估结果）
     */
    public Map<String, EvaluteResult> getEvaluteResultMap() {
        return evaluteResultMap;
    }
    
    /**
     * 添加评估结果
     * 
     * @param evaluteResult 评估结果
     */
    public void addEvaluteResult(EvaluteResult evaluteResult) {
        if (evaluteResult != null && evaluteResult.getSolutionName() != null) {
            evaluteResultMap.put(evaluteResult.getSolutionName(), evaluteResult);
        }
    }
    
    /**
     * 清除相关缓存
     */
    private void clearRelatedCache() {
        userIdSensorIdsMap = null;
        userIdSensorFuncsMap = null;
    }
    
    /**
     * 刷新所有数据源缓存
     */
    public void refreshAllCache() {
        dataSourceFactory.clearCache();
        clearRelatedCache();
    }
    
    /**
     * 创建建造者实例
     * 
     * @return 建造者实例
     */
    public static Builder builder() {
        return new Builder();
    }
}