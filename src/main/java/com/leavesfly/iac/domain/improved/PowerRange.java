package com.leavesfly.iac.domain.improved;

import java.util.Objects;

import com.leavesfly.iac.exception.IntelliAirConditionException;

/**
 * 功率范围（值对象）
 * 
 * 表示功率的有效取值范围，提供范围验证和约束功能
 * 采用不可变设计，确保线程安全
 */
public final class PowerRange {
    
    private final float minPower;
    private final float maxPower;
    
    /**
     * 构造函数
     * 
     * @param minPower 最小功率
     * @param maxPower 最大功率
     * @throws IntelliAirConditionException 如果范围无效
     */
    public PowerRange(float minPower, float maxPower) {
        if (minPower < 0) {
            throw new IntelliAirConditionException("IAC_DOMAIN", "最小功率不能为负数");
        }
        if (maxPower <= minPower) {
            throw new IntelliAirConditionException("IAC_DOMAIN", "最大功率必须大于最小功率");
        }
        
        this.minPower = minPower;
        this.maxPower = maxPower;
    }
    
    /**
     * 获取最小功率
     */
    public float getMinPower() {
        return minPower;
    }
    
    /**
     * 获取最大功率
     */
    public float getMaxPower() {
        return maxPower;
    }
    
    /**
     * 获取功率范围跨度
     */
    public float getRange() {
        return maxPower - minPower;
    }
    
    /**
     * 验证功率值是否在有效范围内
     * 
     * @param power 功率值
     * @return 是否有效
     */
    public boolean isValid(float power) {
        return power >= minPower && power <= maxPower;
    }
    
    /**
     * 约束功率值在有效范围内
     * 
     * @param power 原始功率值
     * @return 约束后的功率值
     */
    public float constrain(float power) {
        if (power < minPower) return minPower;
        if (power > maxPower) return maxPower;
        return power;
    }
    
    /**
     * 生成范围内的随机功率值
     * 
     * @return 随机功率值
     */
    public float randomValue() {
        return minPower + (float) Math.random() * getRange();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        PowerRange that = (PowerRange) obj;
        return Float.compare(that.minPower, minPower) == 0 &&
               Float.compare(that.maxPower, maxPower) == 0;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(minPower, maxPower);
    }
    
    @Override
    public String toString() {
        return String.format("PowerRange[%.1f, %.1f]", minPower, maxPower);
    }
}