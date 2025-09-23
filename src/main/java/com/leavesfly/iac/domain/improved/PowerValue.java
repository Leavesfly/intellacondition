package com.leavesfly.iac.domain.improved;

import java.util.Objects;

import com.leavesfly.iac.exception.IntelliAirConditionException;

/**
 * 功率值（值对象）
 * 
 * 表示一个功率值及其有效范围，提供值验证和约束功能
 * 采用不可变设计，确保线程安全
 */
public final class PowerValue {
    
    private final float value;
    private final PowerRange range;
    
    /**
     * 使用指定值和范围构造功率值
     * 
     * @param value 功率值
     * @param range 功率范围
     * @throws IntelliAirConditionException 如果值超出范围
     */
    public PowerValue(float value, PowerRange range) {
        if (range == null) {
            throw new IntelliAirConditionException("IAC_DOMAIN", "功率范围不能为null");
        }
        if (!range.isValid(value)) {
            throw new IntelliAirConditionException("IAC_DOMAIN", 
                String.format("功率值%.2f超出有效范围%s", value, range));
        }
        
        this.value = value;
        this.range = range;
    }
    
    /**
     * 使用范围生成随机功率值
     * 
     * @param range 功率范围
     */
    public PowerValue(PowerRange range) {
        if (range == null) {
            throw new IntelliAirConditionException("IAC_DOMAIN", "功率范围不能为null");
        }
        
        this.range = range;
        this.value = range.randomValue();
    }
    
    /**
     * 获取功率值
     */
    public float getValue() {
        return value;
    }
    
    /**
     * 获取功率范围
     */
    public PowerRange getRange() {
        return range;
    }
    
    /**
     * 创建新的功率值（约束在当前范围内）
     * 
     * @param newValue 新的功率值
     * @return 新的PowerValue实例
     */
    public PowerValue withValue(float newValue) {
        return new PowerValue(range.constrain(newValue), range);
    }
    
    /**
     * 增加功率值
     * 
     * @param delta 增量
     * @return 新的PowerValue实例
     */
    public PowerValue add(float delta) {
        return withValue(value + delta);
    }
    
    /**
     * 乘以系数
     * 
     * @param factor 系数
     * @return 新的PowerValue实例
     */
    public PowerValue multiply(float factor) {
        return withValue(value * factor);
    }
    
    /**
     * 计算利用率（0-1之间）
     */
    public float getUtilization() {
        return (value - range.getMinPower()) / range.getRange();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        PowerValue that = (PowerValue) obj;
        return Float.compare(that.value, value) == 0 &&
               Objects.equals(range, that.range);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value, range);
    }
    
    @Override
    public String toString() {
        return String.format("PowerValue(%.1f in %s)", value, range);
    }
}