package com.leavesfly.iac.datasource.datagene.generator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.leavesfly.iac.config.AppContextConstant;
import com.leavesfly.iac.datasource.datagene.calculator.AirConditionCalculator;
import com.leavesfly.iac.datasource.datagene.calculator.TemperatureUtilityDistanceCalculator;
import com.leavesfly.iac.domain.GeoPoint;
import com.leavesfly.iac.domain.PowerVector;
import com.leavesfly.iac.train.domain.IntellacTrainDataItem;
import com.leavesfly.iac.util.MathUtil;

/**
 * 重构后的训练数据生成器
 * 
 * 该类负责生成用于机器学习训练的数据集，采用建造者模式和策略模式设计。
 * 主要功能：
 * 1. 生成空调地理位置数据
 * 2. 生成传感器地理位置数据
 * 3. 生成用户地理位置数据
 * 4. 生成用户舒适度数据
 * 5. 生成功率-温度训练数据
 */
public class RefactoredTrainDataGenerator {
    
    /**
     * 随机数生成器
     */
    private final Random random;
    
    /**
     * 空调地理位置列表
     */
    private final List<GeoPoint> airConditionGeoList;
    
    /**
     * 传感器地理位置映射表
     */
    private final Map<String, GeoPoint> sensorGeoMap;
    
    /**
     * 传感器到空调距离映射表
     */
    private final Map<String, Float[]> sensorToAirConditionDistanceMap;
    
    /**
     * 功率向量列表
     */
    private final List<PowerVector> powerVectors;
    
    /**
     * 效用向量列表
     */
    private final List<Float[]> utilityVectors;
    
    /**
     * 建造者类
     */
    public static class Builder {
        private Random random = new Random();
        private List<GeoPoint> airConditionGeoList;
        private Map<String, GeoPoint> sensorGeoMap;
        
        public Builder withRandomSeed(long seed) {
            this.random = new Random(seed);
            return this;
        }
        
        public Builder withAirConditionLocations(List<GeoPoint> locations) {
            this.airConditionGeoList = new ArrayList<>(locations);
            return this;
        }
        
        public Builder withSensorLocations(Map<String, GeoPoint> locations) {
            this.sensorGeoMap = new LinkedHashMap<>(locations);
            return this;
        }
        
        public RefactoredTrainDataGenerator build() {
            return new RefactoredTrainDataGenerator(this);
        }
    }
    
    /**
     * 私有构造函数
     */
    private RefactoredTrainDataGenerator(Builder builder) {
        this.random = builder.random;
        this.airConditionGeoList = builder.airConditionGeoList != null 
                ? builder.airConditionGeoList 
                : createDefaultAirConditionLocations();
        this.sensorGeoMap = builder.sensorGeoMap != null 
                ? builder.sensorGeoMap 
                : createDefaultSensorLocations();
        
        // 计算传感器到空调的距离映射
        this.sensorToAirConditionDistanceMap = calculateSensorToAirConditionDistances();
        
        // 生成功率向量和效用向量
        this.powerVectors = generatePowerVectors();
        this.utilityVectors = generateUtilityVectors();
    }
    
    /**
     * 创建默认空调位置列表
     */
    private List<GeoPoint> createDefaultAirConditionLocations() {
        List<GeoPoint> locations = new ArrayList<>();
        locations.add(new GeoPoint(1, 3));
        locations.add(new GeoPoint(1, 6));
        locations.add(new GeoPoint(3, 9));
        locations.add(new GeoPoint(7, 9));
        locations.add(new GeoPoint(8, 3));
        locations.add(new GeoPoint(8, 8));
        locations.add(new GeoPoint(4, 2));
        locations.add(new GeoPoint(7, 1));
        return locations;
    }
    
    /**
     * 创建默认传感器位置映射
     */
    private Map<String, GeoPoint> createDefaultSensorLocations() {
        Map<String, GeoPoint> locations = new LinkedHashMap<>();
        locations.put("0", new GeoPoint(5, 2));
        locations.put("1", new GeoPoint(4, 4));
        locations.put("2", new GeoPoint(4, 1));
        locations.put("3", new GeoPoint(8, 4));
        locations.put("4", new GeoPoint(2, 7));
        locations.put("5", new GeoPoint(7, 2));
        locations.put("6", new GeoPoint(3, 2));
        locations.put("7", new GeoPoint(4, 8));
        locations.put("8", new GeoPoint(2, 6));
        locations.put("9", new GeoPoint(9, 7));
        return locations;
    }
    
    /**
     * 计算传感器到空调的距离映射
     */
    private Map<String, Float[]> calculateSensorToAirConditionDistances() {
        Map<String, Float[]> distanceMap = new LinkedHashMap<>();
        
        for (Map.Entry<String, GeoPoint> sensorEntry : sensorGeoMap.entrySet()) {
            String sensorId = sensorEntry.getKey();
            GeoPoint sensorGeo = sensorEntry.getValue();
            
            Float[] distances = new Float[airConditionGeoList.size()];
            for (int i = 0; i < airConditionGeoList.size(); i++) {
                GeoPoint airConditionGeo = airConditionGeoList.get(i);
                distances[i] = sensorGeo.getDistance(airConditionGeo);
            }
            
            distanceMap.put(sensorId, distances);
        }
        
        return distanceMap;
    }
    
    /**
     * 生成功率向量列表
     */
    private List<PowerVector> generatePowerVectors() {
        List<PowerVector> vectors = new ArrayList<>();
        final int vectorCount = 50;
        
        // 添加低功率向量 (0-50)
        for (int i = 0; i < 10; i++) {
            vectors.add(PowerVector.getInstanceBySameRange(0, 50, 
                    AppContextConstant.AIR_CONDITION_NUM));
        }
        
        // 添加中功率向量 (150-350)
        for (int i = 0; i < 10; i++) {
            vectors.add(PowerVector.getInstanceBySameRange(150, 350, 
                    AppContextConstant.AIR_CONDITION_NUM));
        }
        
        // 添加高功率向量 (350-400)
        for (int i = 0; i < 10; i++) {
            vectors.add(PowerVector.getInstanceBySameRange(350, 400, 
                    AppContextConstant.AIR_CONDITION_NUM));
        }
        
        // 添加全范围随机功率向量
        for (int i = 0; i < 20; i++) {
            vectors.add(PowerVector.getInstanceBySameRange(
                    AppContextConstant.AIR_CONDITION_MIN_POWER,
                    AppContextConstant.AIR_CONDITION_MAX_POWER,
                    AppContextConstant.AIR_CONDITION_NUM));
        }
        
        return vectors;
    }
    
    /**
     * 生成效用向量列表
     */
    private List<Float[]> generateUtilityVectors() {
        List<Float[]> vectors = new ArrayList<>();
        
        for (PowerVector powerVector : powerVectors) {
            Float[] powerArray = powerVector.getPowerValueFloatArray();
            Float[] utilityArray = AirConditionCalculator.mapPowerToUtilityArray(powerArray);
            vectors.add(utilityArray);
        }
        
        return vectors;
    }
    
    /**
     * 生成训练数据
     * 
     * @return 训练数据项列表
     */
    public List<IntellacTrainDataItem> generateTrainData() {
        List<IntellacTrainDataItem> trainDataItems = new ArrayList<>();
        
        for (Map.Entry<String, Float[]> distanceEntry : sensorToAirConditionDistanceMap.entrySet()) {
            String sensorId = distanceEntry.getKey();
            Float[] distances = distanceEntry.getValue();
            
            for (int i = 0; i < utilityVectors.size(); i++) {
                Float[] utilityVector = utilityVectors.get(i);
                PowerVector powerVector = powerVectors.get(i);
                
                // 计算温度
                float temperature = AppContextConstant.OUTSIDE_TEMP 
                        - TemperatureUtilityDistanceCalculator
                                .calculateCombinedUtilityByDistanceArray(utilityVector, distances);
                
                // 创建训练数据项
                IntellacTrainDataItem trainDataItem = new IntellacTrainDataItem(
                        sensorId, powerVector, temperature, AppContextConstant.OUTSIDE_TEMP);
                
                trainDataItems.add(trainDataItem);
            }
        }
        
        return trainDataItems;
    }
    
    /**
     * 生成并输出训练数据到控制台
     */
    public void generateAndPrintTrainData() {
        List<IntellacTrainDataItem> trainDataItems = generateTrainData();
        
        System.out.println("=== 生成的训练数据 ===");
        System.out.println("总数据量: " + trainDataItems.size());
        System.out.println();
        
        for (IntellacTrainDataItem item : trainDataItems) {
            System.out.println(item.toString());
        }
    }
    
    /**
     * 生成并保存训练数据到文件
     * 
     * @param filePath 文件路径
     * @throws IOException 文件写入异常
     */
    public void generateAndSaveTrainData(String filePath) throws IOException {
        List<IntellacTrainDataItem> trainDataItems = generateTrainData();
        
        Path path = Paths.get(filePath);
        
        try (BufferedWriter writer = Files.newBufferedWriter(path, 
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            
            for (IntellacTrainDataItem item : trainDataItems) {
                writer.write(item.toString());
                writer.newLine();
            }
            
            writer.flush();
        }
        
        System.out.println("训练数据已保存到: " + filePath);
        System.out.println("数据量: " + trainDataItems.size());
    }
    
    /**
     * 生成传感器地理位置信息
     * 
     * @param sensorCount 传感器数量
     * @return 传感器地理位置列表
     */
    public List<String> generateSensorGeoInfo(int sensorCount) {
        if (sensorCount <= 0) {
            throw new IllegalArgumentException("传感器数量必须大于0");
        }
        
        List<String> geoInfoList = new ArrayList<>();
        
        for (int i = 0; i < sensorCount; i++) {
            int x = MathUtil.nextInt(0, AppContextConstant.AREA_LENGTH);
            int y = MathUtil.nextInt(0, AppContextConstant.AREA_WITCH);
            String geoInfo = String.format("%d\t%d,%d", i, x, y);
            geoInfoList.add(geoInfo);
        }
        
        return geoInfoList;
    }
    
    /**
     * 生成用户地理位置信息
     * 
     * @param userCount 用户数量
     * @return 用户地理位置列表
     */
    public List<String> generateUserGeoInfo(int userCount) {
        if (userCount <= 0) {
            throw new IllegalArgumentException("用户数量必须大于0");
        }
        
        List<String> geoInfoList = new ArrayList<>();
        
        for (int i = 0; i < userCount; i++) {
            int x = MathUtil.nextInt(0, AppContextConstant.AREA_LENGTH);
            int y = MathUtil.nextInt(0, AppContextConstant.AREA_WITCH);
            String geoInfo = String.format("%d\t%d,%d", i, x, y);
            geoInfoList.add(geoInfo);
        }
        
        return geoInfoList;
    }
    
    /**
     * 生成用户舒适度信息
     * 
     * @param userCount 用户数量
     * @return 用户舒适度信息列表
     */
    public List<String> generateUserComfortInfo(int userCount) {
        if (userCount <= 0) {
            throw new IllegalArgumentException("用户数量必须大于0");
        }
        
        List<String> comfortInfoList = new ArrayList<>();
        
        // 生成前一半用户的舒适度信息 (22-25度)
        int halfCount = userCount / 2;
        for (int i = 0; i < halfCount; i++) {
            int temperature = MathUtil.nextInt(22, 25);
            String comfortInfo = String.format("%d\t%d", i, temperature);
            comfortInfoList.add(comfortInfo);
        }
        
        // 生成后一半用户的舒适度信息 (19-24度)
        for (int i = halfCount; i < userCount; i++) {
            int temperature = MathUtil.nextInt(19, 24);
            String comfortInfo = String.format("%d\t%d", i, temperature);
            comfortInfoList.add(comfortInfo);
        }
        
        return comfortInfoList;
    }
    
    /**
     * 生成随机地理位置表并保存到文件
     * 
     * @param count 位置数量
     * @param fileName 文件名
     * @throws IOException 文件写入异常
     */
    public void generateRandomGeoTable(int count, String fileName) throws IOException {
        if (count <= 0) {
            throw new IllegalArgumentException("位置数量必须大于0");
        }
        
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        
        Path filePath = Paths.get(fileName);
        
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, 
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            
            for (int i = 1; i <= count; i++) {
                int x = random.nextInt(AppContextConstant.AREA_LENGTH);
                int y = random.nextInt(AppContextConstant.AREA_WITCH);
                String line = String.format("%d\t%d,%d", i, x, y);
                
                writer.write(line);
                writer.newLine();
            }
            
            writer.flush();
        }
        
        System.out.println("随机地理位置表已保存到: " + fileName);
        System.out.println("位置数量: " + count);
    }
    
    /**
     * 获取空调地理位置列表
     * 
     * @return 空调地理位置列表
     */
    public List<GeoPoint> getAirConditionGeoList() {
        return new ArrayList<>(airConditionGeoList);
    }
    
    /**
     * 获取传感器地理位置映射
     * 
     * @return 传感器地理位置映射
     */
    public Map<String, GeoPoint> getSensorGeoMap() {
        return new LinkedHashMap<>(sensorGeoMap);
    }
    
    /**
     * 获取功率向量数量
     * 
     * @return 功率向量数量
     */
    public int getPowerVectorCount() {
        return powerVectors.size();
    }
    
    /**
     * 创建建造者实例
     * 
     * @return 建造者实例
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * 创建默认实例
     * 
     * @return 默认数据生成器实例
     */
    public static RefactoredTrainDataGenerator createDefault() {
        return new Builder().build();
    }
}