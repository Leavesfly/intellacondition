package com.leavesfly.iac.datasource;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.leavesfly.iac.config.AppContextConstant;
import com.leavesfly.iac.domain.GeoPoint;
import com.leavesfly.iac.domain.PtFitFunc;
import com.leavesfly.iac.domain.PowerRange;
import com.leavesfly.iac.evalute.EvaluteResult;
import com.leavesfly.iac.execute.domain.UserComfortFunc;

/**
 * 数据工厂类
 * 
 * 该类是系统的核心数据提供者和中转站，负责：
 * 1. 加载和管理用户舒适度函数
 * 2. 管理地理位置信息（用户和传感器）
 * 3. 管理温度预测模型函数
 * 4. 存储和提供评估结果
 * 
 * 采用单例模式确保全局唯一实例
 */
public final class DataFactory {

	/**
	 * DataFactory单例实例
	 */
	private static volatile DataFactory dataFactory;

	/**
	 * 用户舒适度函数集合
	 */
	private volatile Collection<UserComfortFunc> userComfortFuncSet;
	
	/**
	 * 用户舒适度函数映射表（用户ID -> 舒适度函数）
	 */
	private volatile Map<String, UserComfortFunc> userComfortFuncMap;
	
	/**
	 * 用户期望温度映射表（用户ID -> 期望温度）
	 */
	private volatile Map<String, Float> userWantTempMap;

	/**
	 * 用户地理位置映射表（用户ID -> 地理位置）
	 */
	private volatile Map<String, GeoPoint> userGeoTable;
	
	/**
	 * 传感器地理位置映射表（传感器ID -> 地理位置）
	 */
	private volatile Map<String, GeoPoint> sensorGeoTbale;

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
	 * 传感器温度预测函数集合
	 */
	private volatile Collection<PtFitFunc> sensorFitFuncSet;
	
	/**
	 * 传感器温度预测函数映射表（传感器ID -> 温度预测函数）
	 */
	private volatile Map<String, PtFitFunc> sensorFitFuncMap;

	/**
	 * 评估结果映射表（解决方案名称 -> 评估结果）
	 */
	private Map<String, EvaluteResult> evaluteResultMap = Collections
			.synchronizedMap(new LinkedHashMap<String, EvaluteResult>());

	/**
	 * 私有构造函数，防止外部实例化
	 */
	private DataFactory() {

	}

	/**
	 * 获取DataFactory单例实例
	 * 
	 * 采用双重检查锁定机制确保线程安全
	 * 
	 * @return DataFactory单例实例
	 */
	public static DataFactory getInstance() {
		if (dataFactory != null) {
			return dataFactory;
		} else {
			synchronized (DataFactory.class) {
				if (dataFactory == null) {
					dataFactory = new DataFactory();
				}
			}
		}
		return dataFactory;
	}

	/**
	 * 从配置文件中获取用户自定义的舒适度函数集合
	 * 
	 * 该方法会解析用户舒适度温度数据文件，生成用户舒适度函数集合
	 * 
	 * @return 用户舒适度函数集合
	 */
	public Collection<UserComfortFunc> getUserComfortFuncCollection() {
		if (userComfortFuncSet != null) {
			return userComfortFuncSet;
		}
		synchronized (this) {
			if (userComfortFuncSet == null) {
				try {
					userComfortFuncSet = genUserComfortFuncSet(AppContextConstant.USER_COMFORT_TEMP_DATA_FILE_NAME);
					userComfortFuncMap = new HashMap<String, UserComfortFunc>();
					for (UserComfortFunc func : userComfortFuncSet) {
						userComfortFuncMap.put(func.getUserId(), func);
					}
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException(e.getCause());
				}
			}
		}
		return userComfortFuncSet;
	}

	/**
	 * 生成用户舒适度函数集合
	 * 
	 * 从指定资源文件中读取用户舒适度数据，解析生成用户舒适度函数集合
	 * 
	 * @param resourceName 资源文件名
	 * @return 用户舒适度函数集合
	 * @throws IOException 文件读取异常
	 */
	private Collection<UserComfortFunc> genUserComfortFuncSet(String resourceName)
			throws IOException {
		Collection<UserComfortFunc> userComfortFuncCollection = new ArrayList<UserComfortFunc>();
		userWantTempMap = new HashMap<String, Float>();
		try (BufferedReader reader = ResourceUtil.loadTxtResource(resourceName)) {
			String strLine = null;
			while (StringUtils.isNotBlank(strLine = reader.readLine())) {
				UserComfortFunc userComfortFunc = DomainParser.parseUserComfortFunc(strLine);
				Map.Entry<String, Float> entry = DomainParser.parseUserWantTemp(strLine);
				userWantTempMap.put(entry.getKey(), entry.getValue());
				userComfortFuncCollection.add(userComfortFunc);
			}
		}
		return userComfortFuncCollection;
	}

	/**
	 * 获取用户舒适度函数映射表
	 * 
	 * @return 用户舒适度函数映射表（用户ID -> 舒适度函数）
	 */
	public Map<String, UserComfortFunc> getUserComfortFuncMap() {
		if (userComfortFuncMap != null) {
			return userComfortFuncMap;
		}
		synchronized (this) {
			getUserComfortFuncCollection();
		}
		return userComfortFuncMap;
	}

	/**
	 * 获取用户期望温度映射表
	 * 
	 * @return 用户期望温度映射表（用户ID -> 期望温度）
	 */
	public Map<String, Float> getUserWantTempMap() {
		if (userWantTempMap != null) {
			return userWantTempMap;
		}
		synchronized (this) {
			getUserComfortFuncCollection();
		}
		return userWantTempMap;
	}

	/**
	 * 注册传感器的温度预测函数集合
	 * 
	 * @param registerFitFuncOfPTSet 温度预测函数集合
	 */
	public void registerFitFunc(Collection<PtFitFunc> registerFitFuncOfPTSet) {
		sensorFitFuncSet = registerFitFuncOfPTSet;
		sensorFitFuncMap = new HashMap<String, PtFitFunc>();
		for (PtFitFunc ptfitFunc : registerFitFuncOfPTSet) {
			sensorFitFuncMap.put(ptfitFunc.getSensorId(), ptfitFunc);
		}
	}

	/**
	 * 获取传感器的温度预测函数集合
	 * 
	 * @return 温度预测函数集合
	 */
	public Collection<PtFitFunc> getSensorFitFuncSet() {
		if (sensorFitFuncSet == null) {
			throw new RuntimeException("DataFactory.getSensorFitFuncSet-sensorFitFuncSet is null!");
		}
		return sensorFitFuncSet;

	}

	/**
	 * 获取传感器的温度预测函数映射表
	 * 
	 * @return 温度预测函数映射表（传感器ID -> 温度预测函数）
	 */
	public Map<String, PtFitFunc> getSensorFitFuncMap() {
		if (sensorFitFuncMap == null) {
			throw new RuntimeException("DataFactory.getSensorFitFuncMap-sensorFitFuncMap is null!");
		}
		return sensorFitFuncMap;

	}

	/**
	 * 生成地理位置信息表
	 * 
	 * 从指定资源文件中读取地理位置数据，解析生成地理位置信息映射表
	 * 
	 * @param resourceName 资源文件名
	 * @return 地理位置信息映射表（ID -> 地理位置）
	 * @throws IOException 文件读取异常
	 */
	private Map<String, GeoPoint> genGeoInfoTable(String resourceName) throws IOException {
		Map<String, GeoPoint> geoInfoTable = new HashMap<String, GeoPoint>();
		try (BufferedReader reader = ResourceUtil.loadTxtResource(resourceName)) {
			String strLine = null;
			while (StringUtils.isNotBlank(strLine = reader.readLine())) {
				Map.Entry<String, GeoPoint> entry = DomainParser.parseGeoInfo(strLine);
				geoInfoTable.put(entry.getKey(), entry.getValue());
			}
		}
		return geoInfoTable;
	}

	/**
	 * 获取传感器ID集合
	 * 
	 * @return 传感器ID集合
	 */
	public Set<String> getSensorIdSet() {
		Map<String, GeoPoint> sensorGeoTbale = getSensorGeoInfo();
		return sensorGeoTbale.keySet();
	}

	/**
	 * 获取用户ID周围一定范围内的传感器ID列表的映射表
	 * 
	 * 根据用户和传感器的地理位置信息，计算每个用户周围范围内的传感器列表
	 * 
	 * @return 用户ID与周围传感器ID列表的映射表
	 */
	public Map<String, List<String>> getSensorIdsByUserId() {
		if (userIdSensorIdsMap != null) {
			return userIdSensorIdsMap;
		}
		synchronized (this) {
			Map<String, GeoPoint> userGeoTable = getUserGeoInfo();
			Map<String, GeoPoint> sensorGeoTbale = getSensorGeoInfo();
			userIdSensorIdsMap = new HashMap<String, List<String>>();

			for (Map.Entry<String, GeoPoint> userEntry : userGeoTable.entrySet()) {
				for (Map.Entry<String, GeoPoint> sensorEntry : sensorGeoTbale.entrySet()) {
					if (userEntry.getValue().getDistance(sensorEntry.getValue()) <= AppContextConstant.MAX_DISTANCE) {
						String userId = userEntry.getKey();
						if (userIdSensorIdsMap.containsKey(userId)) {
							userIdSensorIdsMap.get(userId).add(sensorEntry.getKey());
						} else {
							List<String> sersonIdList = new ArrayList<String>();
							sersonIdList.add(sensorEntry.getKey());
							userIdSensorIdsMap.put(userId, sersonIdList);
						}
					}
				}
			}
		}
		return userIdSensorIdsMap;
	}

	/**
	 * 获取用户ID周围一定范围内的传感器的功率-温度映射函数的映射表
	 * 
	 * 根据用户和传感器的地理位置信息，计算每个用户周围范围内的传感器温度预测函数列表
	 * 
	 * @return 用户ID与周围传感器温度预测函数列表的映射表
	 */
	public Map<String, List<PtFitFunc>> getSensorFuncByUserId() {
		if (userIdSensorFuncsMap != null) {
			return userIdSensorFuncsMap;
		}
		synchronized (this) {
			Map<String, GeoPoint> userGeoTable = getUserGeoInfo();
			Map<String, GeoPoint> sensorGeoTbale = getSensorGeoInfo();
			userIdSensorFuncsMap = new HashMap<String, List<PtFitFunc>>();

			for (Map.Entry<String, GeoPoint> userEntry : userGeoTable.entrySet()) {
				for (Map.Entry<String, GeoPoint> sensorEntry : sensorGeoTbale.entrySet()) {
					if (userEntry.getValue().getDistance(sensorEntry.getValue()) <= AppContextConstant.MAX_DISTANCE) {
						String userId = userEntry.getKey();
						if (userIdSensorFuncsMap.containsKey(userId)) {
							userIdSensorFuncsMap.get(userId).add(
									sensorFitFuncMap.get(sensorEntry.getKey()));
						} else {
							List<PtFitFunc> sersonFuncList = new ArrayList<PtFitFunc>();
							sersonFuncList.add(sensorFitFuncMap.get(sensorEntry.getKey()));
							userIdSensorFuncsMap.put(userId, sersonFuncList);
						}
					}
				}
			}
		}
		return userIdSensorFuncsMap;
	}

	/**
	 * 获取用户地理位置信息映射表
	 * 
	 * @return 用户地理位置信息映射表（用户ID -> 地理位置）
	 */
	private Map<String, GeoPoint> getUserGeoInfo() {
		if (userGeoTable != null) {
			return userGeoTable;
		}
		synchronized (this) {
			try {
				userGeoTable = genGeoInfoTable(AppContextConstant.USER_GEO_TABLE_FILE_NAME);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e.getCause());
			}
		}
		return userGeoTable;
	}

	/**
	 * 获取传感器地理位置信息映射表
	 * 
	 * @return 传感器地理位置信息映射表（传感器ID -> 地理位置）
	 */
	private Map<String, GeoPoint> getSensorGeoInfo() {
		if (sensorGeoTbale != null) {
			return sensorGeoTbale;
		}
		synchronized (this) {
			try {
				sensorGeoTbale = genGeoInfoTable(AppContextConstant.SENSOR_GEO_TABLE_FILE_NAME);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e.getCause());
			}
		}
		return sensorGeoTbale;
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
			powerRangeArray = new PowerRange[AppContextConstant.AIR_CONDITION_NUM];
			for (int i = 0; i < powerRangeArray.length; i++) {
				powerRangeArray[i] = new PowerRange(AppContextConstant.AIR_CONDITION_MIN_POWER,
						AppContextConstant.AIR_CONDITION_MAX_POWER);
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
		evaluteResultMap.put(evaluteResult.getSolutionName(), evaluteResult);
	}

	/**
	 * 主函数，用于测试
	 * 
	 * @param args 命令行参数
	 */
	public static void main(String[] args) {

	}
}