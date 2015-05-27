package com.leavesfly.iac.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerializeUtil {
	/**
	 * 将对象序列化成byte数组
	 * 
	 * @param object
	 * @return
	 */
	public static byte[] object2ByteArray(Serializable serialObject) {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = null;
		try {
			objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(serialObject);
		} catch (IOException e) {
			e.printStackTrace();
			return new byte[0];
		}
		return byteArrayOutputStream.toByteArray();
	}

	/**
	 * 将字节数组转化成对象
	 * 
	 * @param byteArray
	 * @return
	 */
	public static Object byteArray2Object(byte[] byteArray) {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
		ObjectInputStream objectInputStream = null;

		try {
			objectInputStream = new ObjectInputStream(byteArrayInputStream);
			Object object = objectInputStream.readObject();
			return object;
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param serialObject
	 * @return
	 */
	public static Object deepCopyBySerializable(Serializable serialObject) {
		return byteArray2Object(object2ByteArray(serialObject));
	}
}
