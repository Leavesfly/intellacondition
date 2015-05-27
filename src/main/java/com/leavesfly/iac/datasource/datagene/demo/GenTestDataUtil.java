package com.leavesfly.iac.datasource.datagene.demo;

import java.text.DecimalFormat;

import com.leavesfly.iac.util.MathUtil;

public class GenTestDataUtil {
	private static float[] c = new float[5];
	private static int a = 0;
	public static DecimalFormat decimalFormat = new DecimalFormat(".00");// 构造方法的字符格式这里如果小数不足2位,会以0补足.

	public static void main(String[] args) {
		
		String a="aa,ss,";
		System.out.println(a.split(",").length);
		// genTrainData();

		// System.out.println(decimalFormat.format(2.0f));

		// 2500048699
		// 2855688583
		// 2495922117
		// 2466070512
		// 2294365824
		// 1531798008
		// 1480679669
		// long[] a = new long[7];
		// a[0] = 2500048699l;
		// a[1] = 2855688583l;
		// a[2] = 2495922117l;
		// a[3] = 2466070512l;
		// a[4] = 2294365824l;
		// a[5] = 1531798008l;
		// a[6] = 1480679669l;
		//
		// long sum = 0l;
		// for (int i = 0; i < a.length; i++) {
		// sum += a[i];
		// }
		//
		// for(int i=0;i<a.length;i++){
		// System.out.println((double) a[i]/sum);
		// }

		// System.out.println(0.317575d*1620000000L);

		// 6046275766
		// 5805919109
		// 5457643112
		// 5924312687
		// 4527139524
		// 3165169132
		// 3408579335

//		long[] a = new long[7];
//		a[0] = 6046275766l;
//		a[1] = 5805919109l;
//		a[2] = 5457643112l;
//		a[3] = 5924312687l;
//		a[4] = 4527139524l;
//		a[5] = 3165169132l;
//		a[6] = 3408579335l;
//
//		long sum = 0l;
//		for (int i = 0; i < a.length; i++) {
//			sum += a[i];
//		}
//
//		for (int i = 0; i < a.length; i++) {
//			System.out.println((double) a[i] / sum);
//		}

		// System.out.println(0.317575d*1620000000L);

		// 1725462772
		// 1692396887
		// 1667115400
		// 1641542386
		// 514471500
		// 6886586644
		// long[] b = new long[5];
		// b[0] = 1725462772l;
		// b[1] = 1692396887l;
		// b[2] = 1667115400l;
		// b[3] = 1641542386l;
		// b[4] = 514471500l;
		//
		// long sum = 0l;
		// for (int i = 0; i < b.length; i++) {
		// sum += b[i];
		// }
		//
		// for(int i=0;i<b.length;i++){
		// System.out.println(6886586644L*(double) b[i]/sum);
		// }
		//
		double[] q = new double[] { 0.2, 0.23, 0.17, 0.195, 0.21 * 0.345 };
		double o = 0d;
		for (int i = 0; i < q.length; i++) {
			o += q[i];
		}
		for (int i = 0; i < q.length; i++){
			System.out.println(8084982198L*(q[i]/o));
		}

	}

	public static void genTrainData() {
		for (int i = 1; i <= 5; i++) {
			for (int j = 1; j <= 40; j++) {
				makeDataItem(i);
				// makeData(i, 24f);

			}
		}
	}

	private static void makeDataItem(int sensorId) {
		int outsideTemp = 30;
		float[] pcVector = new float[5];
		for (int i = 0; i < pcVector.length; i++) {
			pcVector[i] = MathUtil.nextFloat(0, 100);
		}
		int temp = temp(pcVector, weigts(sensorId));
		System.out.println(sensorId + "\t" + decimalFormat.format(to(temp)) + "\t" + outsideTemp + "\t"
				+ floatArrayToString(pcVector));
	}

	private static void makeData(int sensorId, float tp) {
		int outsideTemp = 40;
		int[] pcVector = new int[5];
		for (int i = 0; i < pcVector.length; i++) {
			pcVector[i] = MathUtil.nextInt(0, 100);
		}
		// int temp = temp(pcVector, weigts(sensorId));
		// if (to(temp) == tp) {
		// a++;
		// c[0] += pcVector[0];
		// c[1] += pcVector[1];
		// c[2] += pcVector[2];
		// c[3] += pcVector[3];
		// c[4] += pcVector[4];
		// }

	}

	private static float[] weigts(int sensorId) {
		float[] result = new float[5];
		float a = 0.4f;
		float b = 0.2f;
		float c = 0.2f;
		float d = 0.1f;
		float e = 0.1f;
		switch (sensorId) {
		case 1:
			result[0] = a;
			result[1] = b;
			result[2] = c;
			result[3] = d;
			result[4] = e;
			break;
		case 2:
			result[0] = b;
			result[1] = a;
			result[2] = c;
			result[3] = d;
			result[4] = e;
			break;
		case 3:
			result[0] = c;
			result[1] = b;
			result[2] = a;
			result[3] = d;
			result[4] = e;
			break;
		case 4:
			result[0] = d;
			result[1] = b;
			result[2] = c;
			result[3] = a;
			result[4] = e;
			break;
		case 5:
			result[0] = e;
			result[1] = b;
			result[2] = c;
			result[3] = d;
			result[4] = a;
			break;

		default:
			break;
		}
		return result;
	}

	private static int temp(float[] pcVector, float[] weights) {
		float result = 0f;
		for (int i = 0; i < 5; i++) {
			result += pcVector[i] * weights[i];
		}
		return (int) result;
	}

	private static String intArrayToString(float[] a) {
		StringBuilder s = new StringBuilder();
		s.append('[');
		for (int i = 0; i < a.length; i++) {
			s.append(a[i]);
			if (i != a.length - 1) {
				s.append(',');
			}
		}
		s.append(']');
		return s.toString();
	}

	private static String floatArrayToString(float[] a) {
		StringBuilder s = new StringBuilder();
		s.append('[');
		for (int i = 0; i < a.length; i++) {
			s.append(decimalFormat.format(a[i]));
			if (i != a.length - 1) {
				s.append(',');
			}
		}
		s.append(']');
		return s.toString();
	}

	private static float to(float a) {
		int fromA = 0;
		int toA = 100;
		int fromB = 15;
		int toB = 35;

		float tmp = (a) / toA;
		tmp = toB - tmp * (toB - fromB);
		return tmp;
	}

}
