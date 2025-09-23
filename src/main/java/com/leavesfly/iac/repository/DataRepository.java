package com.leavesfly.iac.repository;

import java.util.Collection;
import java.util.Map;

import com.leavesfly.iac.domain.improved.GeoPoint;
import com.leavesfly.iac.domain.improved.PowerRange;
import com.leavesfly.iac.domain.PtFitFunc;
import com.leavesfly.iac.execute.domain.UserComfortFunc;

/**
 * 数据仓库接口
 * 
 * 抽象数据访问层，支持依赖注入和单元测试
 * 替代原有的DataFactory单例模式
 */
public interface DataRepository {

    /**
     * 获取用户舒适度函数集合
     */
    Collection<UserComfortFunc> getUserComfortFunctions();

    /**
     * 获取用户舒适度函数映射
     */
    Map<String, UserComfortFunc> getUserComfortFunctionMap();

    /**
     * 获取用户期望温度映射
     */
    Map<String, Float> getUserWantTemperatureMap();

    /**
     * 获取用户地理位置映射
     */
    Map<String, GeoPoint> getUserGeoLocations();

    /**
     * 获取传感器地理位置映射
     */
    Map<String, GeoPoint> getSensorGeoLocations();

    /**
     * 获取传感器ID集合
     */
    Collection<String> getSensorIds();

    /**
     * 获取用户周围的传感器ID映射
     */
    Map<String, Collection<String>> getUserSensorMapping();

    /**
     * 获取功率范围数组
     */
    PowerRange[] getPowerRanges();

    /**
     * 注册传感器的温度预测函数集合
     */
    void registerFitFunctions(Collection<PtFitFunc> fitFunctions);

    /**
     * 获取传感器的温度预测函数集合
     */
    Collection<PtFitFunc> getSensorFitFunctions();

    /**
     * 获取传感器的温度预测函数映射
     */
    Map<String, PtFitFunc> getSensorFitFunctionMap();

    /**
     * 获取用户周围的传感器温度预测函数映射
     */
    Map<String, Collection<PtFitFunc>> getUserSensorFunctionMapping();
}