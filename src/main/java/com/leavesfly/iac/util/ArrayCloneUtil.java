package com.leavesfly.iac.util;

import com.leavesfly.iac.domain.EnableClone;

public class ArrayCloneUtil {

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
