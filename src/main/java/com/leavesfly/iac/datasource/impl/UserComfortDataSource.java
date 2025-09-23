package com.leavesfly.iac.datasource.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.leavesfly.iac.datasource.AbstractFileDataSource;
import com.leavesfly.iac.datasource.ResourceUtil;
import com.leavesfly.iac.datasource.DomainParser;
import com.leavesfly.iac.execute.domain.UserComfortFunc;

/**
 * 用户舒适度数据源实现类
 * 
 * 该类负责加载和管理用户舒适度函数和期望温度数据。
 * 数据格式：用户ID\t期望温度
 */
public class UserComfortDataSource extends AbstractFileDataSource<UserComfortDataSource.UserComfortData> {
    
    /**
     * 用户舒适度数据容器类
     */
    public static class UserComfortData {
        private final Map<String, UserComfortFunc> userComfortFuncMap;
        private final Map<String, Float> userWantTempMap;
        
        public UserComfortData(Map<String, UserComfortFunc> userComfortFuncMap, 
                               Map<String, Float> userWantTempMap) {
            this.userComfortFuncMap = new HashMap<>(userComfortFuncMap);
            this.userWantTempMap = new HashMap<>(userWantTempMap);
        }
        
        public Map<String, UserComfortFunc> getUserComfortFuncMap() {
            return new HashMap<>(userComfortFuncMap);
        }
        
        public Map<String, Float> getUserWantTempMap() {
            return new HashMap<>(userWantTempMap);
        }
        
        public Collection<UserComfortFunc> getUserComfortFuncCollection() {
            return userComfortFuncMap.values();
        }
        
        public UserComfortFunc getUserComfortFunc(String userId) {
            return userComfortFuncMap.get(userId);
        }
        
        public Float getUserWantTemp(String userId) {
            return userWantTempMap.get(userId);
        }
        
        public int getUserCount() {
            return userComfortFuncMap.size();
        }
    }
    
    /**
     * 缓存的用户舒适度数据
     */
    private volatile UserComfortData userComfortCache;
    
    /**
     * 构造函数
     * 
     * @param resourceName 资源文件名
     */
    public UserComfortDataSource(String resourceName) {
        super(resourceName, "用户舒适度数据");
    }
    
    @Override
    public UserComfortData load() throws IOException {
        if (userComfortCache != null) {
            return userComfortCache;
        }
        
        synchronized (this) {
            if (userComfortCache == null) {
                validateFileAccess();
                userComfortCache = loadUserComfortData();
            }
        }
        
        return userComfortCache;
    }
    
    /**
     * 从文件加载用户舒适度数据
     * 
     * @return 用户舒适度数据
     * @throws IOException 文件读取异常
     */
    private UserComfortData loadUserComfortData() throws IOException {
        Map<String, UserComfortFunc> userComfortFuncMap = new LinkedHashMap<>();
        Map<String, Float> userWantTempMap = new LinkedHashMap<>();
        
        try (BufferedReader reader = ResourceUtil.loadTxtResource(resourceName)) {
            String line;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                if (StringUtils.isBlank(line) || line.trim().startsWith("#")) {
                    continue; // 跳过空行和注释行
                }
                
                try {
                    String trimmedLine = line.trim();
                    
                    // 解析用户舒适度函数
                    UserComfortFunc userComfortFunc = DomainParser.parseUserComfortFunc(trimmedLine);
                    if (userComfortFunc != null) {
                        userComfortFuncMap.put(userComfortFunc.getUserId(), userComfortFunc);
                    }
                    
                    // 解析用户期望温度
                    Map.Entry<String, Float> tempEntry = DomainParser.parseUserWantTemp(trimmedLine);
                    if (tempEntry != null) {
                        userWantTempMap.put(tempEntry.getKey(), tempEntry.getValue());
                    }
                    
                } catch (Exception e) {
                    throw new IOException(String.format(
                            "解析用户舒适度数据失败，文件: %s, 行号: %d, 内容: %s, 错误: %s", 
                            resourceName, lineNumber, line, e.getMessage()), e);
                }
            }
        }
        
        return new UserComfortData(userComfortFuncMap, userWantTempMap);
    }
    
    @Override
    public void save(UserComfortData data) throws IOException {
        throw new UnsupportedOperationException("用户舒适度数据源不支持保存操作");
    }
    
    @Override
    public void refresh() throws IOException {
        synchronized (this) {
            userComfortCache = null;
        }
    }
    
    /**
     * 获取指定用户的舒适度函数
     * 
     * @param userId 用户ID
     * @return 用户舒适度函数，如果不存在返回null
     * @throws IOException 数据加载异常
     */
    public UserComfortFunc getUserComfortFunc(String userId) throws IOException {
        UserComfortData data = load();
        return data.getUserComfortFunc(userId);
    }
    
    /**
     * 获取指定用户的期望温度
     * 
     * @param userId 用户ID
     * @return 用户期望温度，如果不存在返回null
     * @throws IOException 数据加载异常
     */
    public Float getUserWantTemp(String userId) throws IOException {
        UserComfortData data = load();
        return data.getUserWantTemp(userId);
    }
}