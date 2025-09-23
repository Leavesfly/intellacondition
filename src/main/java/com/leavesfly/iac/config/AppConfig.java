package com.leavesfly.iac.config;

/**
 * 应用配置类
 * 
 * 统一管理应用程序的所有配置参数，提供类型安全的配置访问
 * 采用不可变对象设计，确保配置的线程安全性
 */
public final class AppConfig {

    private final AreaConfig area;
    private final DeviceConfig device;
    private final FileConfig file;
    private final ComfortConfig comfort;
    private final PowerConfig power;
    private final SolutionConfig solution;

    private static final AppConfig INSTANCE = new AppConfig();

    private AppConfig() {
        this.area = new AreaConfig();
        this.device = new DeviceConfig();
        this.file = new FileConfig();
        this.comfort = new ComfortConfig();
        this.power = new PowerConfig();
        this.solution = new SolutionConfig();
    }

    /**
     * 获取应用配置实例
     */
    public static AppConfig getInstance() {
        return INSTANCE;
    }

    public AreaConfig getArea() { return area; }
    public DeviceConfig getDevice() { return device; }
    public FileConfig getFile() { return file; }
    public ComfortConfig getComfort() { return comfort; }
    public PowerConfig getPower() { return power; }
    public SolutionConfig getSolution() { return solution; }

    /**
     * 区域配置
     */
    public static final class AreaConfig {
        private final int length = 10;
        private final int width = 10;
        private final float maxDistance;

        private AreaConfig() {
            this.maxDistance = (float) Math.pow(
                ((length * width / DeviceConfig.SENSOR_NUM) * 3.0f) / Math.PI, 0.5f);
        }

        public int getLength() { return length; }
        public int getWidth() { return width; }
        public float getMaxDistance() { return maxDistance; }
    }

    /**
     * 设备配置
     */
    public static final class DeviceConfig {
        public static final int USER_NUM = 16;
        public static final int SENSOR_NUM = 10;
        public static final int AIR_CONDITION_NUM = 8;

        public int getUserNum() { return USER_NUM; }
        public int getSensorNum() { return SENSOR_NUM; }
        public int getAirConditionNum() { return AIR_CONDITION_NUM; }
    }

    /**
     * 文件配置
     */
    public static final class FileConfig {
        private final String userComfortTempDataFileName = "user_comfort_temp.txt";
        private final String userGeoTableFileName = "user_geo_table.txt";
        private final String sensorGeoTableFileName = "sensor_geo_table.txt";
        private final String trainDataFileName = "power_temp_train_data.txt";

        public String getUserComfortTempDataFileName() { return userComfortTempDataFileName; }
        public String getUserGeoTableFileName() { return userGeoTableFileName; }
        public String getSensorGeoTableFileName() { return sensorGeoTableFileName; }
        public String getTrainDataFileName() { return trainDataFileName; }
    }

    /**
     * 舒适度配置
     */
    public static final class ComfortConfig {
        private final float minValue = 0.2f;
        private final float upDownRangeValue = 1.5f;
        private final float satisfyWeight = 0.5f;
        private final float powerCostWeight = 0.5f;
        private final float ableAdjustFactor = 0.25f;

        public float getMinValue() { return minValue; }
        public float getUpDownRangeValue() { return upDownRangeValue; }
        public float getSatisfyWeight() { return satisfyWeight; }
        public float getPowerCostWeight() { return powerCostWeight; }
        public float getAbleAdjustFactor() { return ableAdjustFactor; }
    }

    /**
     * 功率配置
     */
    public static final class PowerConfig {
        private final float outsideTemp = 35.0f;
        private final float airConditionMinTemp = 15.0f;
        private final float airConditionMaxTemp = 35.0f;
        private final float airConditionMinPower = 0.0f;
        private final float airConditionMaxPower = 400.0f;
        private final float powerPrice = 1.0f;
        private final int powerUtilityUnit = 1_000;

        public float getOutsideTemp() { return outsideTemp; }
        public float getAirConditionMinTemp() { return airConditionMinTemp; }
        public float getAirConditionMaxTemp() { return airConditionMaxTemp; }
        public float getAirConditionMinPower() { return airConditionMinPower; }
        public float getAirConditionMaxPower() { return airConditionMaxPower; }
        public float getPowerPrice() { return powerPrice; }
        public int getPowerUtilityUnit() { return powerUtilityUnit; }
    }

    /**
     * 解决方案配置
     */
    public static final class SolutionConfig {
        private final String namePrefix = "solution_";
        private final String psoSuffix = "pso";
        private final String psoChaosSuffix = "pso_chaos";

        public String getNamePrefix() { return namePrefix; }
        public String getPsoSuffix() { return psoSuffix; }
        public String getPsoChaosSuffix() { return psoChaosSuffix; }
    }
}