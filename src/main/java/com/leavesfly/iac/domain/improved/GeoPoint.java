package com.leavesfly.iac.domain.improved;

import java.util.Objects;

import com.leavesfly.iac.exception.IntelliAirConditionException;

/**
 * 地理坐标点（值对象）
 * 
 * 表示二维空间中的一个点，具有不可变性和线程安全性
 * 提供距离计算等几何操作方法
 */
public final class GeoPoint {
    
    private final float x;
    private final float y;
    
    /**
     * 构造函数
     * 
     * @param x X坐标
     * @param y Y坐标
     */
    public GeoPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * 获取X坐标
     */
    public float getX() {
        return x;
    }
    
    /**
     * 获取Y坐标
     */
    public float getY() {
        return y;
    }
    
    /**
     * 计算到另一个点的欧几里得距离
     * 
     * @param other 另一个地理点
     * @return 距离值
     * @throws IntelliAirConditionException 如果参数为null
     */
    public float distanceTo(GeoPoint other) {
        if (other == null) {
            throw new IntelliAirConditionException("IAC_DOMAIN", "GeoPoint cannot be null");
        }
        
        float dx = this.x - other.x;
        float dy = this.y - other.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * 判断是否在指定距离范围内
     * 
     * @param other 另一个地理点
     * @param maxDistance 最大距离
     * @return 是否在范围内
     */
    public boolean isWithinDistance(GeoPoint other, float maxDistance) {
        return distanceTo(other) <= maxDistance;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        GeoPoint geoPoint = (GeoPoint) obj;
        return Float.compare(geoPoint.x, x) == 0 && 
               Float.compare(geoPoint.y, y) == 0;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
    
    @Override
    public String toString() {
        return String.format("GeoPoint(%.2f, %.2f)", x, y);
    }
}