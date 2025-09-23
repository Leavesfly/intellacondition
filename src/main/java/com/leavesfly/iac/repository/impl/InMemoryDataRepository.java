package com.leavesfly.iac.repository.impl;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.leavesfly.iac.config.AppConfig;
import com.leavesfly.iac.domain.improved.GeoPoint;
import com.leavesfly.iac.domain.improved.PowerRange;
import com.leavesfly.iac.domain.PtFitFunc;
import com.leavesfly.iac.exception.IntelliAirConditionException;
import com.leavesfly.iac.execute.domain.UserComfortFunc;
import com.leavesfly.iac.repository.DataRepository;

/**
 * 内存数据仓库实现
 * 
 * 基于内存的数据仓库实现，提供线程安全的数据访问
 * 重构自原有的DataFactory，职责更加单一
 */
public class InMemoryDataRepository implements DataRepository {

    private final AppConfig config;
    
    // 缓存映射 - 线程安全
    private final Map<String, Object> cache = new ConcurrentHashMap<>();
    
    // 温度预测函数相关
    private volatile Collection<PtFitFunc> sensorFitFunctions;
    private volatile Map<String, PtFitFunc> sensorFitFunctionMap;

    public InMemoryDataRepository() {
        this.config = AppConfig.getInstance();
    }

    @Override
    public Collection<UserComfortFunc> getUserComfortFunctions() {
        // TODO: 实现用户舒适度函数加载逻辑
        // 这里需要整合原有的数据解析逻辑
        throw new IntelliAirConditionException("IAC_DATA", "尚未实现用户舒适度函数加载");
    }

    @Override
    public Map<String, UserComfortFunc> getUserComfortFunctionMap() {
        // TODO: 实现用户舒适度函数映射
        throw new IntelliAirConditionException("IAC_DATA", "尚未实现用户舒适度函数映射");
    }

    @Override
    public Map<String, Float> getUserWantTemperatureMap() {
        // TODO: 实现用户期望温度映射
        throw new IntelliAirConditionException("IAC_DATA", "尚未实现用户期望温度映射");
    }

    @Override
    public Map<String, GeoPoint> getUserGeoLocations() {
        // TODO: 实现用户地理位置映射
        throw new IntelliAirConditionException("IAC_DATA", "尚未实现用户地理位置映射");
    }

    @Override
    public Map<String, GeoPoint> getSensorGeoLocations() {
        // TODO: 实现传感器地理位置映射
        throw new IntelliAirConditionException("IAC_DATA", "尚未实现传感器地理位置映射");
    }

    @Override
    public Collection<String> getSensorIds() {
        return getSensorGeoLocations().keySet();
    }

    @Override
    public Map<String, Collection<String>> getUserSensorMapping() {
        // TODO: 实现用户-传感器映射
        throw new IntelliAirConditionException("IAC_DATA", "尚未实现用户-传感器映射");
    }

    @Override
    public PowerRange[] getPowerRanges() {
        return computeIfAbsent("power_ranges", () -> {
            AppConfig.PowerConfig powerConfig = config.getPower();
            PowerRange[] ranges = new PowerRange[config.getDevice().getAirConditionNum()];
            
            for (int i = 0; i < ranges.length; i++) {
                ranges[i] = new PowerRange(
                    powerConfig.getAirConditionMinPower(),
                    powerConfig.getAirConditionMaxPower()
                );
            }
            
            return ranges;
        });
    }

    @Override
    public void registerFitFunctions(Collection<PtFitFunc> fitFunctions) {
        if (fitFunctions == null) {
            throw new IntelliAirConditionException("IAC_DATA", "温度预测函数集合不能为null");
        }
        
        this.sensorFitFunctions = fitFunctions;
        
        // 构建映射
        this.sensorFitFunctionMap = new ConcurrentHashMap<>();
        for (PtFitFunc fitFunc : fitFunctions) {
            sensorFitFunctionMap.put(fitFunc.getSensorId(), fitFunc);
        }
    }

    @Override
    public Collection<PtFitFunc> getSensorFitFunctions() {
        if (sensorFitFunctions == null) {
            throw new IntelliAirConditionException("IAC_DATA", "温度预测函数尚未注册");
        }
        return sensorFitFunctions;
    }

    @Override
    public Map<String, PtFitFunc> getSensorFitFunctionMap() {
        if (sensorFitFunctionMap == null) {
            throw new IntelliAirConditionException("IAC_DATA", "温度预测函数映射尚未初始化");
        }
        return sensorFitFunctionMap;
    }

    @Override
    public Map<String, Collection<PtFitFunc>> getUserSensorFunctionMapping() {
        // TODO: 实现用户-传感器函数映射
        throw new IntelliAirConditionException("IAC_DATA", "尚未实现用户-传感器函数映射");
    }

    /**
     * 线程安全的缓存计算
     */
    @SuppressWarnings("unchecked")
    private <T> T computeIfAbsent(String key, java.util.function.Supplier<T> supplier) {
        return (T) cache.computeIfAbsent(key, k -> supplier.get());
    }
}