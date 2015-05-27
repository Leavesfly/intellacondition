package com.leavesfly.iac.execute.domain;

public class ContiUserComfortFunc extends UserComfortFunc {

	private UserTempRange userTempRange;
	private float average;
	private float variance;

	/**
	 * 
	 * @param userId
	 * @param userTempRange
	 */
	public ContiUserComfortFunc(String userId, UserTempRange userTempRange) {
		super(userId);
		this.userTempRange = userTempRange;
		buildComfortFunc();
	}

	private void buildComfortFunc() {
		average = (userTempRange.getFrom() + userTempRange.getTo()) / 2;
		variance = -(float) Math.pow((userTempRange.getTo() - average), 2)
				/ (2 * (float) Math.log(COMFORT_MIN_VALUE));
	}

//	@Override
//	public float calUserComfortExt(float temperature) {
//		if (userTempRange.isInRange(temperature)) {
//			float value = -(float) Math.pow((temperature - average), 2) / (2 * variance);
//			return (float) Math.exp(value);
//		}
//		return 0f;
//	}

	@Override
	public float calUserComfort(float temperature) {
		if (userTempRange.isInRange(temperature)) {
			float value = -(float) Math.pow((temperature - average), 2) / (2 * variance);
			return (float) Math.exp(value);
		}
		return 0f;
	}

	@Override
	public String toString() {
		return userId + "\t" + userTempRange.getFrom() + "," + userTempRange.getTo() + "\t"
				+ average + "\t" + variance + "\t" + calUserComfort(average);
	}

	@Override
	public boolean isUpMinSatisfy(float temperature) {

		return userTempRange.isInRange(temperature);
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// ContiUserComfortFunc c = new ContiUserComfortFunc("122", new
		// UserTempRange(24, 28));
		// System.out.println(c.calUserComfort(24));
		// System.out.println(c.calUserComfort(28));
		// System.out.println(c.calUserComfort(26));
		// System.out.println(c.calUserComfort(22));
		// System.out.println(c.calUserComfort(30));
		// System.out.println("-----------------");
		// ContiUserComfortFunc b = new ContiUserComfortFunc("122", new
		// UserTempRange(22, 26));
		// System.out.println(b.calUserComfort(22));
		// System.out.println(b.calUserComfort(26));
		// System.out.println(b.calUserComfort(24));
		// System.out.println(b.calUserComfort(21));
		// System.out.println(b.calUserComfort(27));

		// test(24.8f);
		test1();

	}

	public static void test(float temp) {
		ContiUserComfortFunc userA = new ContiUserComfortFunc("122", new UserTempRange(23, 27));

		ContiUserComfortFunc userB = new ContiUserComfortFunc("122", new UserTempRange(25, 29));

		ContiUserComfortFunc userC = new ContiUserComfortFunc("122", new UserTempRange(24, 28));

		ContiUserComfortFunc userD = new ContiUserComfortFunc("122", new UserTempRange(20, 24));

		ContiUserComfortFunc userE = new ContiUserComfortFunc("122", new UserTempRange(22, 26));

		ContiUserComfortFunc[] userArray = new ContiUserComfortFunc[5];
		userArray[0] = userA;
		userArray[1] = userB;
		userArray[2] = userC;
		userArray[3] = userD;
		userArray[4] = userE;

		float result = 0f;
		for (ContiUserComfortFunc tmp : userArray) {
			float tm = tmp.calUserComfort(temp);
			if (tm > 0) {
				result += tm;
			}
			System.out.println(tm);
		}
		System.out.println(result);
	}

	public static void test1() {
		float a = 0f;
		ContiUserComfortFunc userA = new ContiUserComfortFunc("122", new UserTempRange(23, 27));
		float tmp = userA.calUserComfort(25.2f);
		a += tmp;
		System.out.println(tmp);

		ContiUserComfortFunc userB = new ContiUserComfortFunc("122", new UserTempRange(25, 29));
		tmp = userB.calUserComfort(25.8f);
		a += tmp;
		System.out.println(tmp);

		ContiUserComfortFunc userC = new ContiUserComfortFunc("122", new UserTempRange(24, 28));

		tmp = userC.calUserComfort(25.1f);
		a += tmp;
		System.out.println(tmp);

		ContiUserComfortFunc userD = new ContiUserComfortFunc("122", new UserTempRange(20, 24));
		tmp = userD.calUserComfort(23.9f);
		a += tmp;
		System.out.println(tmp);

		ContiUserComfortFunc userE = new ContiUserComfortFunc("122", new UserTempRange(22, 26));
		tmp = userE.calUserComfort(25.0f);
		a += tmp;
		System.out.println(tmp);
		System.out.println(a);

	}

}
