package com.leavesfly.iac.datasource;

import java.util.AbstractMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.leavesfly.iac.config.AppContextConstant;
import com.leavesfly.iac.domain.GeoPoint;
import com.leavesfly.iac.domain.PowerVector;
import com.leavesfly.iac.execute.domain.ContiUserComfortFunc;
import com.leavesfly.iac.execute.domain.UserComfortFunc;
import com.leavesfly.iac.execute.domain.UserTempRange;
import com.leavesfly.iac.train.domain.IntellacTrainDataItem;

/**
 * 领域对象解析器类
 * 
 * 该类负责解析各种数据格式的字符串，将其转换为对应的领域对象。
 * 主要功能包括：
 * 1. 解析训练数据
 * 2. 解析用户舒适度函数
 * 3. 解析用户期望温度
 * 4. 解析地理位置信息
 */
public class DomainParser {

	/**
	 * 表格分隔符（制表符）
	 */
	private final static Pattern SPLITER_TABLE = Pattern.compile("\t");
	
	/**
	 * 逗号分隔符
	 */
	private final static Pattern SPLITER_COMMA = Pattern.compile(",");

	/**
	 * 解析训练数据
	 * 
	 * 输入格式：传感器ID\t温度\t室外温度\t功率值1,功率值2,...,功率值N
	 * 示例：0\t22.5\t35.0\t20.4,120.3,114.9,297.6,298.3,190.0,198.7,83.5
	 * 
	 * @param strLine 训练数据字符串
	 * @return 解析后的训练数据项
	 */
	public static IntellacTrainDataItem parseTrainData(String strLine) {
		if (StringUtils.isBlank(strLine)) {
			throw new IllegalArgumentException();
		}

		IntellacTrainDataItem result = null;
		String[] strArray = SPLITER_TABLE.split(strLine);
		if (strArray.length == 4) {

			String sensorId = strArray[0];
			float temperature = NumberUtils.toFloat(strArray[1]);
			float outsideTemp = NumberUtils.toFloat(strArray[2]);
			strArray = SPLITER_COMMA.split(strArray[3]);
			Float[] powerValueArray = new Float[strArray.length];

			for (int i = 0; i < strArray.length; i++) {
				float value = NumberUtils.toFloat(strArray[i]);
				powerValueArray[i] = value;
			}
			PowerVector powerVector = new PowerVector(powerValueArray,
					AppContextConstant.AIR_CONDITION_NUM);
			result = new IntellacTrainDataItem(sensorId, powerVector, temperature, outsideTemp);

		} else {
			throw new IllegalArgumentException();
		}
		return result;
	}

	/**
	 * 解析用户舒适度函数
	 * 
	 * 输入格式：用户ID\t期望温度
	 * 示例：0\t23
	 * 
	 * @param strLine 用户舒适度数据字符串
	 * @return 解析后的用户舒适度函数
	 */
	public static UserComfortFunc parseUserComfortFunc(String strLine) {
		if (StringUtils.isBlank(strLine)) {
			throw new IllegalArgumentException();
		}

		String[] strArray = SPLITER_TABLE.split(strLine);
		UserComfortFunc userComfortFunc = null;
		if (strArray.length == 2) {
			String userId = strArray[0];
			float wantTemp = NumberUtils.toFloat(strArray[1]);
			UserTempRange userTempRange = new UserTempRange(wantTemp
					- AppContextConstant.COMFORT_UP_DOWN_RANGE_VALUE, wantTemp
					+ AppContextConstant.COMFORT_UP_DOWN_RANGE_VALUE);
			userComfortFunc = new ContiUserComfortFunc(userId, userTempRange);
		} else {
			throw new IllegalArgumentException();
		}
		return userComfortFunc;
	}

	/**
	 * 解析用户期望温度
	 * 
	 * 输入格式：用户ID\t期望温度
	 * 示例：0\t23
	 * 
	 * @param strLine 用户期望温度数据字符串
	 * @return 用户ID和期望温度的键值对
	 */
	public static Map.Entry<String, Float> parseUserWantTemp(String strLine) {
		if (StringUtils.isBlank(strLine)) {
			throw new IllegalArgumentException();
		}
		String[] strArray = SPLITER_TABLE.split(strLine);
		Map.Entry<String, Float> entry = null;
		if (strArray.length == 2) {
			String userId = strArray[0];
			float wantTemp = NumberUtils.toFloat(strArray[1]);
			entry = new AbstractMap.SimpleEntry<String, Float>(userId, wantTemp);
		} else {
			throw new IllegalArgumentException();
		}
		return entry;
	}

	/**
	 * 解析地理位置信息
	 * 
	 * 输入格式：ID\tX坐标,Y坐标
	 * 示例：0\t5,2
	 * 
	 * @param strLine 地理位置数据字符串
	 * @return ID和地理位置的键值对
	 */
	public static Map.Entry<String, GeoPoint> parseGeoInfo(String strLine) {
		if (StringUtils.isBlank(strLine)) {
			throw new IllegalArgumentException();
		}
		String[] strArray = SPLITER_TABLE.split(strLine);
		Map.Entry<String, GeoPoint> entry = null;
		if (strArray.length == 2) {
			String id = strArray[0];
			strArray = SPLITER_COMMA.split(strArray[1]);
			entry = new AbstractMap.SimpleEntry<String, GeoPoint>(id, new GeoPoint(
					NumberUtils.toInt(strArray[0]), NumberUtils.toInt(strArray[1])));
		} else {
			throw new IllegalArgumentException();
		}
		return entry;
	}

}