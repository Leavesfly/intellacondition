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
 * 数据工厂提供程序需要运行的数据以及数据中转站
 * 
 * @author yefei.yf
 *
 */
public final class DataFactory {

	private static volatile DataFactory dataFactory;

	private volatile Collection<UserComfortFunc> userComfortFuncSet;
	private volatile Map<String, UserComfortFunc> userComfortFuncMap;
	private volatile Map<String, Float> userWantTempMap;

	private volatile Map<String, GeoPoint> userGeoTable;
	private volatile Map<String, GeoPoint> sensorGeoTbale;

	private volatile Map<String, List<String>> userIdSensorIdsMap;
	private volatile Map<String, List<PtFitFunc>> userIdSensorFuncsMap;
	private volatile PowerRange[] powerRangeArray;

	private volatile Collection<PtFitFunc> sensorFitFuncSet;
	private volatile Map<String, PtFitFunc> sensorFitFuncMap;

	private Map<String, EvaluteResult> evaluteResultMap = Collections
			.synchronizedMap(new LinkedHashMap<String, EvaluteResult>());

	private DataFactory() {

	}

	/**
	 * 单例获取实例
	 * 
	 * @return
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
	 * 从配置文件中获取用户自定义的舒适度函数的集合
	 * 
	 * @param resourceName
	 * @return
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
	 * 
	 * @return
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
	 * 注册节点的拟合函数集合
	 * 
	 * @param registerFitFuncOfPTSet
	 */
	public void registerFitFunc(Collection<PtFitFunc> registerFitFuncOfPTSet) {
		sensorFitFuncSet = registerFitFuncOfPTSet;
		sensorFitFuncMap = new HashMap<String, PtFitFunc>();
		for (PtFitFunc ptfitFunc : registerFitFuncOfPTSet) {
			sensorFitFuncMap.put(ptfitFunc.getSensorId(), ptfitFunc);
		}
	}

	/**
	 * 获取节点的拟合函数集合
	 * 
	 * @return
	 */
	public Collection<PtFitFunc> getSensorFitFuncSet() {
		if (sensorFitFuncSet == null) {
			throw new RuntimeException("DataFactory.getSensorFitFuncSet-sensorFitFuncSet is null!");
		}
		return sensorFitFuncSet;

	}

	/**
	 * 获取节点的拟合函数的Map
	 * 
	 * @return
	 */
	public Map<String, PtFitFunc> getSensorFitFuncMap() {
		if (sensorFitFuncMap == null) {
			throw new RuntimeException("DataFactory.getSensorFitFuncMap-sensorFitFuncMap is null!");
		}
		return sensorFitFuncMap;

	}

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
	 * 获取传感器id的集合
	 * 
	 * @return
	 */
	public Set<String> getSensorIdSet() {
		Map<String, GeoPoint> sensorGeoTbale = getSensorGeoInfo();
		return sensorGeoTbale.keySet();
	}

	/**
	 * 获取用户id周围一定范围内的传感器id列表的map表
	 * 
	 * @return
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
	 * 获取用户id周围一定范围内的传感器的功率-温度映射函数的map表
	 * 
	 * @return
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
	 * 获取功率调度的范围约束
	 * 
	 * @return
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
	 * 
	 * @return
	 */
	public Map<String, EvaluteResult> getEvaluteResultMap() {
		return evaluteResultMap;
	}

	/**
	 * 
	 * @param evaluteResult
	 */
	public void addEvaluteResult(EvaluteResult evaluteResult) {
		evaluteResultMap.put(evaluteResult.getSolutionName(), evaluteResult);
	}

	public static void main(String[] args) {

	}
}
