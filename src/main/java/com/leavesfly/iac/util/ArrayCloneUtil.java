package com.leavesfly.iac.util;

import com.leavesfly.iac.domain.EnableClone;

/**
 * 数组克隆工具类
 * 
 * 该类提供数组深拷贝功能，用于克隆实现EnableClone接口的数组元素。
 */
public class ArrayCloneUtil {

	/**
	 * 数组深拷贝方法
	 * 
	 * 对实现EnableClone接口的数组进行深拷贝，确保数组元素也被正确克隆
	 * 
	 * @param array 待拷贝的数组
	 * @param <T> 数组元素类型，必须实现EnableClone接口
	 * @return 拷贝后的数组
	 */
	@SuppressWarnings("unchecked")
	public static <T extends EnableClone> T[] arrayDeepCopy(T[] array) {
		T[] arrayClone = array.clone();
		for (int i = 0; i < array.length; i++) {
			if (array[i] != null) {
				arrayClone[i] = (T) array[i].clone();
			} else {
				arrayClone[i] = null;
			}
		}
		return arrayClone;
	}

}