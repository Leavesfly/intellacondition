package com.leavesfly.iac.datasource.datagene.demo;

import java.text.DecimalFormat;

import com.leavesfly.iac.util.MathUtil;

public class GenTestDataUtilB {
	public static float a[] = new float[] { 0.6f, 0.5f, 0.45f };
	public static String s[] = new String[] { "A", "B", "C" };
	public static DecimalFormat decimalFormat = new DecimalFormat(".0");// 构造方法的字符格式这里如果小数不足2位,会以0补足.

	public static void main(String[] args) {
		genData();
	}

	public static void genData() {

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 30; j++) {
				int q[] = getPower();
				float d = genTemp(q, a[i]);

				System.out.println(s[i] + "\t" + "<" + q[0] + "," + q[1] + ">" + "\t"
						+ decimalFormat.format(d) + "\t" + 30);

			}
		}
	}

	private static int[] getPower() {
		int a[] = new int[2];
		a[0] = MathUtil.nextInt(1500, 2500);
		a[1] = MathUtil.nextInt(1500, 2500);
		return a;
	}

	private static float genTemp(int[] a, float b) {
		float c = a[0] * b + a[1] * (1f - b);
		c = 30 - ((2500f - c) / 2500f) * (30 - 15);
		return c;
	}
}
