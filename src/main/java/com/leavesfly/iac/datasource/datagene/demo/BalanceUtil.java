package com.leavesfly.iac.datasource.datagene.demo;

public class BalanceUtil {

	private static final double[] purchaseInWeekRatio = new double[] { 0.16000748520147823, 0.1827690592055954,
			0.15974337674288627, 0.15783282186161998, 0.14684342180106363, 0.0980377491025481, 0.09476608608480837 };

	private static final double[] redeemInWeekRatio = new double[] { 0.1760963727168705, 0.16909604109222576,
			0.15895258383860042, 0.1725442264621373, 0.13185188367400372, 0.09218481338791876, 0.09927407882824354 };

	public static void purch(double[] data) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < purchaseInWeekRatio.length; j++) {
				System.out.println((long) (data[i] * purchaseInWeekRatio[j]));
			}
		}

		double d1 = purchaseInWeekRatio[0] / (purchaseInWeekRatio[0] + purchaseInWeekRatio[1]);
		double d2 = purchaseInWeekRatio[1] / (purchaseInWeekRatio[0] + purchaseInWeekRatio[1]);
		System.out.println((long) (data[4] * d1));
		System.out.println((long) (data[4] * d2));
	}
	
	public static void redeem (double[] data) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < redeemInWeekRatio.length; j++) {
				System.out.println((long) (data[i] * redeemInWeekRatio[j]));
			}
		}

		double d1 = redeemInWeekRatio[0] / (redeemInWeekRatio[0] + redeemInWeekRatio[1]);
		double d2 = redeemInWeekRatio[1] / (redeemInWeekRatio[0] + redeemInWeekRatio[1]);
		System.out.println((long) (data[4] * d1));
		System.out.println((long) (data[4] * d2));
	}

	public static void main(String[] args) {
		
		
		
		
		
		BalanceUtil.purch(new double[] { 1.6410118798178082E9d, 1.60956436847058E9d, 1.5855202562592392E9d,
				1.5611988855187368E9d, 4.89291253933636E8d });
		System.out.println("++++++++++++++++++");
		BalanceUtil.redeem(new double[] { 1.864080280823102E9, 2.1436923229465673E9, 1.5844682386996367E9,
				1.8174782738025246E9, 6.752630817281686E8 });
	}
}
