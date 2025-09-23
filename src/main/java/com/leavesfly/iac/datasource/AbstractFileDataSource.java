package com.leavesfly.iac.datasource;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * 基于文件的数据源抽象基类
 * 
 * 该类提供了文件数据源的通用功能实现，包括文件存在性检查、
 * 路径解析等基础功能。
 * 
 * @param <T> 数据类型
 */
public abstract class AbstractFileDataSource<T> implements DataSource<T> {
    
    /**
     * 资源文件名
     */
    protected final String resourceName;
    
    /**
     * 数据源类型
     */
    protected final String type;
    
    /**
     * 构造函数
     * 
     * @param resourceName 资源文件名
     * @param type 数据源类型
     */
    protected AbstractFileDataSource(String resourceName, String type) {
        if (resourceName == null || resourceName.trim().isEmpty()) {
            throw new IllegalArgumentException("资源文件名不能为空");
        }
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("数据源类型不能为空");
        }
        this.resourceName = resourceName.trim();
        this.type = type.trim();
    }
    
    @Override
    public boolean isAvailable() {
        try {
            URL resourceUrl = getResourceUrl();
            if (resourceUrl != null) {
                File file = new File(resourceUrl.toURI());
                return file.exists() && file.canRead();
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public String getType() {
        return type;
    }
    
    /**
     * 获取资源文件的URL
     * 
     * @return 资源文件URL
     */
    protected URL getResourceUrl() {
        return ClassLoader.getSystemResource(resourceName);
    }
    
    /**
     * 获取资源文件名
     * 
     * @return 资源文件名
     */
    public String getResourceName() {
        return resourceName;
    }
    
    /**
     * 验证文件是否存在并可读
     * 
     * @throws IOException 如果文件不存在或不可读
     */
    protected void validateFileAccess() throws IOException {
        if (!isAvailable()) {
            throw new IOException("资源文件不存在或不可读: " + resourceName);
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s[resourceName=%s, type=%s, available=%s]", 
                getClass().getSimpleName(), resourceName, type, isAvailable());
    }
}