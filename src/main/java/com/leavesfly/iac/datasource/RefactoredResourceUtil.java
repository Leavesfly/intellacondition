package com.leavesfly.iac.datasource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * 重构后的资源工具类
 * 
 * 该类提供了一系列静态方法用于加载和处理类路径下的资源文件，
 * 相比原版本增强了以下功能：
 * 1. 支持字符编码指定
 * 2. 增强的异常处理和错误信息
 * 3. 资源存在性检查
 * 4. 支持多种读取方式（流、Reader、字符串、行列表）
 * 5. 更好的资源管理和自动关闭
 */
public class RefactoredResourceUtil {
    
    /**
     * 默认字符编码
     */
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    
    /**
     * 获取资源文件的URL
     * 
     * @param resourceName 资源文件名
     * @return 资源文件的URL，如果资源不存在返回null
     */
    public static URL getResourceUrl(String resourceName) {
        if (resourceName == null || resourceName.trim().isEmpty()) {
            throw new IllegalArgumentException("资源文件名不能为空");
        }
        return ClassLoader.getSystemResource(resourceName.trim());
    }
    
    /**
     * 检查资源文件是否存在
     * 
     * @param resourceName 资源文件名
     * @return 如果资源存在返回true，否则返回false
     */
    public static boolean resourceExists(String resourceName) {
        return getResourceUrl(resourceName) != null;
    }
    
    /**
     * 加载类路径下的资源文件输入流
     * 
     * @param resourceName 资源文件名
     * @return 资源文件输入流
     * @throws IOException 如果资源不存在
     */
    public static InputStream loadClassPathResource(String resourceName) throws IOException {
        if (resourceName == null || resourceName.trim().isEmpty()) {
            throw new IllegalArgumentException("资源文件名不能为空");
        }
        
        String trimmedName = resourceName.trim();
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(trimmedName);
        
        if (inputStream == null) {
            throw new IOException("资源文件不存在: " + trimmedName);
        }
        
        return inputStream;
    }
    
    /**
     * 获取类路径下资源文件的输出流
     * 
     * @param resourceName 资源文件名
     * @return 资源文件输出流
     * @throws IOException 文件操作异常
     */
    public static OutputStream getClassPathResourceOutputStream(String resourceName) throws IOException {
        URL resourceUrl = getResourceUrl(resourceName);
        if (resourceUrl == null) {
            throw new FileNotFoundException("资源文件不存在: " + resourceName);
        }
        
        try {
            return new FileOutputStream(resourceUrl.getFile());
        } catch (FileNotFoundException e) {
            throw new IOException("无法创建资源文件输出流: " + resourceName, e);
        }
    }
    
    /**
     * 加载文本资源文件的BufferedReader（使用默认字符编码）
     * 
     * @param resourceName 文本资源文件名
     * @return 文本资源文件的BufferedReader
     * @throws IOException 文件读取异常
     */
    public static BufferedReader loadTxtResource(String resourceName) throws IOException {
        return loadTxtResource(resourceName, DEFAULT_CHARSET);
    }
    
    /**
     * 加载文本资源文件的BufferedReader（指定字符编码）
     * 
     * @param resourceName 文本资源文件名
     * @param charset 字符编码
     * @return 文本资源文件的BufferedReader
     * @throws IOException 文件读取异常
     */
    public static BufferedReader loadTxtResource(String resourceName, Charset charset) throws IOException {
        InputStream inputStream = loadClassPathResource(resourceName);
        Charset actualCharset = charset != null ? charset : DEFAULT_CHARSET;
        return new BufferedReader(new InputStreamReader(inputStream, actualCharset));
    }
    
    /**
     * 读取文本资源文件的全部内容为字符串
     * 
     * @param resourceName 文本资源文件名
     * @return 文件内容字符串
     * @throws IOException 文件读取异常
     */
    public static String readResourceAsString(String resourceName) throws IOException {
        return readResourceAsString(resourceName, DEFAULT_CHARSET);
    }
    
    /**
     * 读取文本资源文件的全部内容为字符串（指定字符编码）
     * 
     * @param resourceName 文本资源文件名
     * @param charset 字符编码
     * @return 文件内容字符串
     * @throws IOException 文件读取异常
     */
    public static String readResourceAsString(String resourceName, Charset charset) throws IOException {
        StringBuilder content = new StringBuilder();
        
        try (BufferedReader reader = loadTxtResource(resourceName, charset)) {
            String line;
            boolean firstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (!firstLine) {
                    content.append(System.lineSeparator());
                }
                content.append(line);
                firstLine = false;
            }
        }
        
        return content.toString();
    }
    
    /**
     * 读取文本资源文件的所有行为列表
     * 
     * @param resourceName 文本资源文件名
     * @return 文件行列表
     * @throws IOException 文件读取异常
     */
    public static List<String> readResourceAsLines(String resourceName) throws IOException {
        return readResourceAsLines(resourceName, DEFAULT_CHARSET);
    }
    
    /**
     * 读取文本资源文件的所有行为列表（指定字符编码）
     * 
     * @param resourceName 文本资源文件名
     * @param charset 字符编码
     * @return 文件行列表
     * @throws IOException 文件读取异常
     */
    public static List<String> readResourceAsLines(String resourceName, Charset charset) throws IOException {
        java.util.List<String> lines = new java.util.ArrayList<>();
        
        try (BufferedReader reader = loadTxtResource(resourceName, charset)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        
        return lines;
    }
    
    /**
     * 获取类路径下资源文件的BufferedWriter（使用默认字符编码）
     * 
     * @param resourceName 资源文件名
     * @return 资源文件的BufferedWriter
     * @throws IOException 文件操作异常
     */
    public static BufferedWriter getClassPathResourceBufferedWriter(String resourceName) throws IOException {
        return getClassPathResourceBufferedWriter(resourceName, DEFAULT_CHARSET);
    }
    
    /**
     * 获取类路径下资源文件的BufferedWriter（指定字符编码）
     * 
     * @param resourceName 资源文件名
     * @param charset 字符编码
     * @return 资源文件的BufferedWriter
     * @throws IOException 文件操作异常
     */
    public static BufferedWriter getClassPathResourceBufferedWriter(String resourceName, Charset charset) throws IOException {
        OutputStream outputStream = getClassPathResourceOutputStream(resourceName);
        Charset actualCharset = charset != null ? charset : DEFAULT_CHARSET;
        return new BufferedWriter(new OutputStreamWriter(outputStream, actualCharset));
    }
    
    /**
     * 将字符串内容写入资源文件
     * 
     * @param resourceName 资源文件名
     * @param content 要写入的内容
     * @throws IOException 文件写入异常
     */
    public static void writeStringToResource(String resourceName, String content) throws IOException {
        writeStringToResource(resourceName, content, DEFAULT_CHARSET);
    }
    
    /**
     * 将字符串内容写入资源文件（指定字符编码）
     * 
     * @param resourceName 资源文件名
     * @param content 要写入的内容
     * @param charset 字符编码
     * @throws IOException 文件写入异常
     */
    public static void writeStringToResource(String resourceName, String content, Charset charset) throws IOException {
        if (content == null) {
            content = "";
        }
        
        try (BufferedWriter writer = getClassPathResourceBufferedWriter(resourceName, charset)) {
            writer.write(content);
            writer.flush();
        }
    }
    
    /**
     * 将行列表内容写入资源文件
     * 
     * @param resourceName 资源文件名
     * @param lines 要写入的行列表
     * @throws IOException 文件写入异常
     */
    public static void writeLinesToResource(String resourceName, List<String> lines) throws IOException {
        writeLinesToResource(resourceName, lines, DEFAULT_CHARSET);
    }
    
    /**
     * 将行列表内容写入资源文件（指定字符编码）
     * 
     * @param resourceName 资源文件名
     * @param lines 要写入的行列表
     * @param charset 字符编码
     * @throws IOException 文件写入异常
     */
    public static void writeLinesToResource(String resourceName, List<String> lines, Charset charset) throws IOException {
        if (lines == null) {
            lines = java.util.Collections.emptyList();
        }
        
        try (BufferedWriter writer = getClassPathResourceBufferedWriter(resourceName, charset)) {
            for (int i = 0; i < lines.size(); i++) {
                writer.write(lines.get(i));
                if (i < lines.size() - 1) {
                    writer.newLine();
                }
            }
            writer.flush();
        }
    }
    
    /**
     * 获取资源文件的绝对路径
     * 
     * @param resourceName 资源文件名
     * @return 资源文件的绝对路径
     * @throws IOException 如果资源不存在
     */
    public static String getResourcePath(String resourceName) throws IOException {
        URL resourceUrl = getResourceUrl(resourceName);
        if (resourceUrl == null) {
            throw new IOException("资源文件不存在: " + resourceName);
        }
        
        try {
            return Paths.get(resourceUrl.toURI()).toString();
        } catch (Exception e) {
            throw new IOException("无法获取资源文件路径: " + resourceName, e);
        }
    }
    
    /**
     * 获取资源文件的大小（字节数）
     * 
     * @param resourceName 资源文件名
     * @return 文件大小
     * @throws IOException 如果资源不存在或无法读取
     */
    public static long getResourceSize(String resourceName) throws IOException {
        String resourcePath = getResourcePath(resourceName);
        Path path = Paths.get(resourcePath);
        return Files.size(path);
    }
    
    /**
     * 复制资源文件到指定路径
     * 
     * @param resourceName 源资源文件名
     * @param targetPath 目标文件路径
     * @throws IOException 文件复制异常
     */
    public static void copyResourceToFile(String resourceName, String targetPath) throws IOException {
        try (InputStream inputStream = loadClassPathResource(resourceName)) {
            Path target = Paths.get(targetPath);
            Files.copy(inputStream, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
    }
    
    /**
     * 获取默认字符编码
     * 
     * @return 默认字符编码
     */
    public static Charset getDefaultCharset() {
        return DEFAULT_CHARSET;
    }
}