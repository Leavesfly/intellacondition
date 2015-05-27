package com.leavesfly.iac.train.collect;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.leavesfly.iac.config.AppContextConstant;
import com.leavesfly.iac.datasource.DomainParser;
import com.leavesfly.iac.datasource.ResourceUtil;
import com.leavesfly.iac.train.domain.IntellacTrainDataItem;
import com.leavesfly.iac.train.store.TrainDataSetManager;
import com.leavesfly.iac.train.store.TrainDataSetManagerInLucene;

/**
 * 训练数据收集器
 * 
 * @author yefei.yf
 *
 */
public class DataCollecter {

	private TrainDataSetManager trainDataSetManager;
	private BufferedReader bufferedReader;
	private Timer timer;

	public static DataCollecter getInstance() {
		DataCollecter dataCollecter = new DataCollecter();
		dataCollecter.setTrainDataSetManager(new TrainDataSetManagerInLucene());
		dataCollecter.setTimer(new Timer(true));
		return dataCollecter;
	}

	public static DataCollecter getInstance(String trainDataFileName) {
		DataCollecter dataCollecter = new DataCollecter();
		dataCollecter.setTrainDataSetManager(new TrainDataSetManagerInLucene());
		dataCollecter.setBufferedReader(ResourceUtil.loadTxtResource(trainDataFileName));
		return dataCollecter;
	}

	private DataCollecter() {

	}

	/**
	 * 开始收集训练数据
	 */
	public void startCollectData() {
		// 延迟1000毫秒，间隔2000毫秒
		if (timer == null) {
			throw new RuntimeException("you use error api,when you instance DataCollecter!");
		}
		timer.schedule(new DataCollectTask(trainDataSetManager), 1000, 2000);
	}

	/**
	 * 关闭数据收集
	 */
	public void stopCollectData() {
		timer.cancel();
	}

	/**
	 * 
	 * @return
	 */
	public Collection<IntellacTrainDataItem> collectTrainDataItemFromTxt() {
		List<IntellacTrainDataItem> trainDatas = new ArrayList<IntellacTrainDataItem>();
		IntellacTrainDataItem trainDataItem = collectOneTrainDataItemFromTxt();
		while (trainDataItem != null) {
			trainDatas.add(trainDataItem);
			trainDataItem = collectOneTrainDataItemFromTxt();
		}
		return trainDatas;
	}

	private IntellacTrainDataItem collectOneTrainDataItemFromTxt() {
		String strLine = null;
		try {
			strLine = bufferedReader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (strLine == null) {
			return null;
		}
		return DomainParser.parseTrainData(strLine);
	}

	public static Collection<IntellacTrainDataItem> collectData() {

		return null;
	}

	// private static IntellacTrainDataItem makeTrainDataItem(String sensorId,
	// float outsideTemp) {
	//
	// return null;
	// }

	public TrainDataSetManager getTrainDataSetManager() {
		return trainDataSetManager;
	}

	public void setTrainDataSetManager(TrainDataSetManager trainDataSetManager) {
		this.trainDataSetManager = trainDataSetManager;
	}

	public BufferedReader getBufferedReader() {
		return bufferedReader;
	}

	public void setBufferedReader(BufferedReader bufferedReader) {
		this.bufferedReader = bufferedReader;
	}

	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}

	/**
	 * 定时任务
	 * 
	 * @author yefei.yf
	 *
	 */
	private static class DataCollectTask extends TimerTask {

		private TrainDataSetManager trainDataSetManager;

		public DataCollectTask(TrainDataSetManager trainDataSetManager) {
			this.trainDataSetManager = trainDataSetManager;
		}

		@Override
		public void run() {
			Collection<IntellacTrainDataItem> trainDataSet = collectData();
			trainDataSetManager.storeTrainDataSet(trainDataSet);
		}
	}

	public static void main(String[] args) {
		DataCollecter dataCollecter = DataCollecter
				.getInstance(AppContextConstant.TRAIN_DATA_FILE_NAME);

		Collection<IntellacTrainDataItem> trainDatas = dataCollecter.collectTrainDataItemFromTxt();

		TrainDataSetManager trainDataSetManager = dataCollecter.getTrainDataSetManager();

		trainDataSetManager.storeTrainDataSet(trainDatas);

		Collection<IntellacTrainDataItem> datas = trainDataSetManager.fetchTrainDataSetBySensorId(
				"1", AppContextConstant.OUTSIDE_TEMP);
		for (IntellacTrainDataItem item : datas) {
			System.out.println(item);
		}
	}

}
