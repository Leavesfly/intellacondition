package com.leavesfly.iac.datasource.datagene;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.leavesfly.iac.config.AppContextConstant;
import com.leavesfly.iac.datasource.ResourceUtil;
import com.leavesfly.iac.domain.GeoPoint;
import com.leavesfly.iac.domain.PowerVector;
import com.leavesfly.iac.train.domain.IntellacTrainDataItem;
import com.leavesfly.iac.util.MathUtil;

public class TrainDataGenerator {

	private static List<GeoPoint> AirConditionGeoList = new ArrayList<GeoPoint>(
			AppContextConstant.AIR_CONDITION_NUM);

	static {
		// 0 1,3
		// 1 1,6
		// 2 3,9
		// 3 7,9
		// 4 8,3
		// 5 8,8
		// 6 4,2
		// 7 7,1
		AirConditionGeoList.add(new GeoPoint(1, 3));
		AirConditionGeoList.add(new GeoPoint(1, 6));
		AirConditionGeoList.add(new GeoPoint(3, 9));
		AirConditionGeoList.add(new GeoPoint(7, 9));
		AirConditionGeoList.add(new GeoPoint(8, 3));
		AirConditionGeoList.add(new GeoPoint(8, 8));
		AirConditionGeoList.add(new GeoPoint(4, 2));
		AirConditionGeoList.add(new GeoPoint(7, 1));
	}

	private static Map<String, GeoPoint> SensorGeoMap = new LinkedHashMap<String, GeoPoint>();
	// 0 5,2
	// 1 4,4
	// 2 4,1
	// 3 8,4
	// 4 2,7
	// 5 7,2
	// 6 3,2
	// 7 4,8
	// 8 2,6
	// 9 9,7
	static {
		SensorGeoMap.put("0", new GeoPoint(5, 2));
		SensorGeoMap.put("1", new GeoPoint(4, 4));
		SensorGeoMap.put("2", new GeoPoint(4, 1));
		SensorGeoMap.put("3", new GeoPoint(8, 4));
		SensorGeoMap.put("4", new GeoPoint(2, 7));
		SensorGeoMap.put("5", new GeoPoint(7, 2));
		SensorGeoMap.put("6", new GeoPoint(3, 2));
		SensorGeoMap.put("7", new GeoPoint(4, 8));
		SensorGeoMap.put("8", new GeoPoint(2, 6));
		SensorGeoMap.put("9", new GeoPoint(9, 7));

	}

	private static Map<String, Float[]> SensorGeo2AirConditionMap = new LinkedHashMap<String, Float[]>();

	static {

		for (Map.Entry<String, GeoPoint> entry : SensorGeoMap.entrySet()) {
			GeoPoint sensorGeo = entry.getValue();
			Float[] distances = new Float[AppContextConstant.AIR_CONDITION_NUM];
			int i = 0;
			for (GeoPoint airConditionGeo : AirConditionGeoList) {
				float diatance = sensorGeo.getDistance(airConditionGeo);
				distances[i] = diatance;
				++i;
			}
			SensorGeo2AirConditionMap.put(entry.getKey(), distances);
		}
	}

	private final static int PowerVectorNum = 50;
	private static List<PowerVector> PowerVectors = new ArrayList<PowerVector>(PowerVectorNum);

	static {

		for (int i = 0; i < 10; i++) {
			PowerVectors.add(PowerVector.getInstanceBySameRange(0, 50,
					AppContextConstant.AIR_CONDITION_NUM));
		}
		for (int i = 0; i < 10; i++) {
			PowerVectors.add(PowerVector.getInstanceBySameRange(150, 350,
					AppContextConstant.AIR_CONDITION_NUM));
		}
		for (int i = 0; i < 10; i++) {
			PowerVectors.add(PowerVector.getInstanceBySameRange(350, 400,
					AppContextConstant.AIR_CONDITION_NUM));
		}
		for (int i = 0; i < 20; i++) {
			PowerVectors.add(PowerVector.getInstanceBySameRange(
					AppContextConstant.AIR_CONDITION_MIN_POWER,
					AppContextConstant.AIR_CONDITION_MAX_POWER,
					AppContextConstant.AIR_CONDITION_NUM));
		}
	}

	private static List<Float[]> UtilityVectors = new ArrayList<Float[]>(PowerVectorNum);

	static {
		for (PowerVector powerVector : PowerVectors) {
			Float[] powerArray = powerVector.getPowerValueFloatArray();
			UtilityVectors.add(AirConditionUtil.mapPower2UtilityByArray(powerArray));
		}
	}

	private static List<IntellacTrainDataItem> TrainDataItems = new ArrayList<IntellacTrainDataItem>(
			PowerVectorNum * AppContextConstant.SENSOR_NUM);

	public static void genTrainData() {
		for (Map.Entry<String, Float[]> entry : SensorGeo2AirConditionMap.entrySet()) {
			String sensorId = entry.getKey();
			Float[] distances = entry.getValue();
			int i = 0;
			for (Float[] utilityVector : UtilityVectors) {
				float temperature = AppContextConstant.OUTSIDE_TEMP
						- TempUtilityDistanceUtil.calcTemUtilByDistanceArray(utilityVector,
								distances);
				PowerVector powerVector = PowerVectors.get(i);
				TrainDataItems.add(new IntellacTrainDataItem(sensorId, powerVector, temperature,
						AppContextConstant.OUTSIDE_TEMP));
				++i;
			}
		}

		for (IntellacTrainDataItem trainDataItem : TrainDataItems) {
			System.out.println(trainDataItem.toString());
		}
	}

	public static void genSensorGeoInfo(int sensorNum) {
		for (int i = 0; i < sensorNum; i++) {
			System.out.println(i + "\t" + MathUtil.nextInt(0, AppContextConstant.AREA_LENGTH) + ","
					+ MathUtil.nextInt(0, AppContextConstant.AREA_WITCH));
		}
	}

	public static void genUserGeoInfo(int userNum) {
		for (int i = 0; i < userNum; i++) {
			System.out.println(i + "\t" + MathUtil.nextInt(0, AppContextConstant.AREA_LENGTH) + ","
					+ MathUtil.nextInt(0, AppContextConstant.AREA_WITCH));
		}
	}

	public static void genUserComfortInfo() {
		for (int i = 0; i < 8; i++) {
			System.out.println(i + "\t" + MathUtil.nextInt(22, 25));
		}
		for (int i = 8; i < 16; i++) {
			System.out.println(i + "\t" + MathUtil.nextInt(19, 24));
		}
	}

	/**
	 * 构造随机的地理位置表
	 * 
	 * @param number
	 * @param fileName
	 */
	public static void strucRandGeoTable(int number, String fileName) {
		String filePath = ResourceUtil.getResourceUrl(fileName).getPath();

		try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(new File(filePath))))) {
			Random random = new Random();
			for (int i = 1; i <= number; i++) {
				bufferedWriter.write(i + "\t" + random.nextInt(AppContextConstant.AREA_LENGTH)
						+ "," + random.nextInt(AppContextConstant.AREA_WITCH) + "\n");
			}
			bufferedWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getCause());
		}
	}

	public static void main(String[] args) {
		// TrainDataGenerator.genSensorGeoInfo(AppContextConstant.SENSOR_NUM);
		// TrainDataGenerator.genUserGeoInfo(AppContextConstant.USER_NUM);
		// TrainDataGenerator.genUserComfortInfo();
		TrainDataGenerator.genTrainData();
	}

}
