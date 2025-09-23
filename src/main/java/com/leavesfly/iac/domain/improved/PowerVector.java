package com.leavesfly.iac.domain.improved;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import com.leavesfly.iac.exception.IntelliAirConditionException;

/**
 * 功率向量（值对象）
 * 
 * 表示多个空调设备的功率值集合，提供向量运算和统计功能
 * 采用不可变设计，确保线程安全
 */
public final class PowerVector {
    
    private final List<PowerValue> powers;
    
    /**
     * 构造函数
     * 
     * @param powers 功率值数组
     * @throws IntelliAirConditionException 如果参数无效
     */
    public PowerVector(PowerValue... powers) {
        if (powers == null || powers.length == 0) {
            throw new IntelliAirConditionException("IAC_DOMAIN", "功率向量不能为空");
        }
        
        for (PowerValue power : powers) {
            if (power == null) {
                throw new IntelliAirConditionException("IAC_DOMAIN", "功率值不能为null");
            }
        }
        
        this.powers = Collections.unmodifiableList(Arrays.asList(powers.clone()));
    }
    
    /**
     * 从浮点数组和范围数组构造
     * 
     * @param values 功率值数组
     * @param ranges 功率范围数组
     */
    public PowerVector(float[] values, PowerRange[] ranges) {
        if (values == null || ranges == null || values.length != ranges.length) {
            throw new IntelliAirConditionException("IAC_DOMAIN", "值数组和范围数组长度必须相等");
        }
        
        PowerValue[] powerValues = IntStream.range(0, values.length)
            .mapToObj(i -> new PowerValue(values[i], ranges[i]))
            .toArray(PowerValue[]::new);
            
        this.powers = Collections.unmodifiableList(Arrays.asList(powerValues));
    }
    
    /**
     * 获取功率值列表（只读）
     */
    public List<PowerValue> getPowers() {
        return powers;
    }
    
    /**
     * 获取向量大小
     */
    public int size() {
        return powers.size();
    }
    
    /**
     * 获取指定索引的功率值
     * 
     * @param index 索引
     * @return 功率值
     */
    public PowerValue get(int index) {
        return powers.get(index);
    }
    
    /**
     * 获取功率值数组
     */
    public float[] getValues() {
        return powers.stream()
            .mapToDouble(PowerValue::getValue)
            .collect(
                () -> new float[powers.size()],
                (array, value) -> {
                    int index = IntStream.range(0, array.length)
                        .filter(i -> array[i] == 0)
                        .findFirst()
                        .orElse(array.length - 1);
                    array[index] = (float) value;
                },
                (a1, a2) -> {}
            );
    }
    
    /**
     * 计算总功率
     */
    public float getTotalPower() {
        return (float) powers.stream()
            .mapToDouble(PowerValue::getValue)
            .sum();
    }
    
    /**
     * 计算平均功率
     */
    public float getAveragePower() {
        return getTotalPower() / powers.size();
    }
    
    /**
     * 计算功率方差
     */
    public float getPowerVariance() {
        float avg = getAveragePower();
        return (float) powers.stream()
            .mapToDouble(p -> Math.pow(p.getValue() - avg, 2))
            .average()
            .orElse(0.0);
    }
    
    /**
     * 计算平均利用率
     */
    public float getAverageUtilization() {
        return (float) powers.stream()
            .mapToDouble(PowerValue::getUtilization)
            .average()
            .orElse(0.0);
    }
    
    /**
     * 向量加法
     * 
     * @param other 另一个功率向量
     * @return 新的功率向量
     */
    public PowerVector add(PowerVector other) {
        if (other.size() != this.size()) {
            throw new IntelliAirConditionException("IAC_DOMAIN", "向量维度不匹配");
        }
        
        PowerValue[] result = IntStream.range(0, powers.size())
            .mapToObj(i -> powers.get(i).add(other.get(i).getValue()))
            .toArray(PowerValue[]::new);
            
        return new PowerVector(result);
    }
    
    /**
     * 标量乘法
     * 
     * @param scalar 标量值
     * @return 新的功率向量
     */
    public PowerVector multiply(float scalar) {
        PowerValue[] result = powers.stream()
            .map(p -> p.multiply(scalar))
            .toArray(PowerValue[]::new);
            
        return new PowerVector(result);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        PowerVector that = (PowerVector) obj;
        return Objects.equals(powers, that.powers);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(powers);
    }
    
    @Override
    public String toString() {
        return String.format("PowerVector%s [total=%.1f, avg=%.1f]", 
            powers.stream()
                .map(p -> String.format("%.1f", p.getValue()))
                .reduce((a, b) -> a + "," + b)
                .map(s -> "[" + s + "]")
                .orElse("[]"),
            getTotalPower(),
            getAveragePower()
        );
    }
}