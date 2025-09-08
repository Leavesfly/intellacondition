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

import com.leavesfly.iac.config.AppContextConstant;

/**
 * 资源工具类
 * 
 * 该类提供了一系列静态方法用于加载和处理类路径下的资源文件，
 * 包括文本文件的读取和写入操作。
 */
public class ResourceUtil {

	/**
	 * 获取资源文件的URL
	 * 
	 * @param resourceName 资源文件名
	 * @return 资源文件的URL
	 */
	public static URL getResourceUrl(String resourceName) {
		return ClassLoader.getSystemResource(resourceName);
	}

	/**
	 * 加载类路径下的资源文件输入流
	 * 
	 * @param resourceName 资源文件名
	 * @return 资源文件输入流
	 */
	public static InputStream loadClassPathResource(String resourceName) {
		InputStream inputStream = ClassLoader.getSystemResourceAsStream(resourceName);
		return inputStream;
	}

	/**
	 * 获取类路径下资源文件的输出流
	 * 
	 * @param resourceName 资源文件名
	 * @return 资源文件输出流
	 */
	public static OutputStream getClassPathResourceOutputStream(String resourceName) {
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(getResourceUrl(resourceName).getFile());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return outputStream;
	}

	/**
	 * 加载文本资源文件的BufferedReader
	 * 
	 * @param resourceName 文本资源文件名
	 * @return 文本资源文件的BufferedReader
	 */
	public static BufferedReader loadTxtResource(String resourceName) {
		InputStream inputStream = loadClassPathResource(resourceName);
		return new BufferedReader(new InputStreamReader(inputStream));
	}

	/**
	 * 获取类路径下资源文件的BufferedWriter
	 * 
	 * @param resourceName 资源文件名
	 * @return 资源文件的BufferedWriter
	 */
	public static BufferedWriter getClassPathResourceBufferedWriter(String resourceName) {
		return new BufferedWriter(new OutputStreamWriter(
				getClassPathResourceOutputStream(resourceName)));
	}

	/**
	 * 主函数，用于测试资源加载功能
	 * 
	 * @param args 命令行参数
	 * @throws IOException IO异常
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader reader = loadTxtResource(AppContextConstant.USER_COMFORT_TEMP_DATA_FILE_NAME);
		String str = null;
		while ((str = reader.readLine()) != null) {
			System.out.println(str);
		}
		reader.close();
	}

}