package com.leavesfly.iac.datasource;

import java.io.IOException;

/**
 * 数据源访问抽象接口
 * 
 * 该接口定义了数据源访问的基本规范，支持不同类型的数据源实现。
 * 采用泛型设计以支持不同的数据类型。
 * 
 * @param <T> 数据类型
 */
public interface DataSource<T> {
    
    /**
     * 从数据源加载数据
     * 
     * @return 加载的数据对象
     * @throws IOException 数据加载异常
     */
    T load() throws IOException;
    
    /**
     * 保存数据到数据源
     * 
     * @param data 要保存的数据对象
     * @throws IOException 数据保存异常
     */
    void save(T data) throws IOException;
    
    /**
     * 检查数据源是否可用
     * 
     * @return 如果数据源可用返回true，否则返回false
     */
    boolean isAvailable();
    
    /**
     * 获取数据源类型描述
     * 
     * @return 数据源类型描述
     */
    String getType();
    
    /**
     * 刷新数据源（可选操作）
     * 
     * @throws IOException 刷新异常
     */
    default void refresh() throws IOException {
        // 默认实现为空，子类可以根据需要重写
    }
}