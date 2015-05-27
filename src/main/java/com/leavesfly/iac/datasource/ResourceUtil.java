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
 * 
 * @author LeavesFly
 *
 */
public class ResourceUtil {

	public static URL getResourceUrl(String resourceName) {
		return ClassLoader.getSystemResource(resourceName);
	}

	public static InputStream loadClassPathResource(String resourceName) {
		InputStream inputStream = ClassLoader.getSystemResourceAsStream(resourceName);
		return inputStream;
	}

	public static OutputStream getClassPathResourceOutputStream(String resourceName) {
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(getResourceUrl(resourceName).getFile());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return outputStream;
	}

	public static BufferedReader loadTxtResource(String resourceName) {
		InputStream inputStream = loadClassPathResource(resourceName);
		return new BufferedReader(new InputStreamReader(inputStream));
	}

	public static BufferedWriter getClassPathResourceBufferedWriter(String resourceName) {
		return new BufferedWriter(new OutputStreamWriter(
				getClassPathResourceOutputStream(resourceName)));
	}

	public static void main(String[] args) throws IOException {
		BufferedReader reader = loadTxtResource(AppContextConstant.USER_COMFORT_TEMP_DATA_FILE_NAME);
		String str = null;
		while ((str = reader.readLine()) != null) {
			System.out.println(str);
		}
		reader.close();
	}

}
