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
 * 训练数据收集器类
 * 
 * 该类负责收集和管理训练数据，支持从文本文件中读取训练数据，
 * 以及通过定时任务方式动态收集数据。
 */
public class DataCollecter {

	/**
	 * 训练数据集管理器
	 */
	private TrainDataSetManager trainDataSetManager;
	
	/**
	 * 缓冲读取器，用于从文本文件读取训练数据
	 */
	private BufferedReader bufferedReader;
	
	/**
	 * 定时器，用于定时收集数据
	 */
	private Timer timer;

	/**
	 * 获取数据收集器实例（默认方式）
	 * 
	 * @return 数据收集器实例
	 */
	public static DataCollecter getInstance() {
		DataCollecter dataCollecter = new DataCollecter();
		dataCollecter.setTrainDataSetManager(new TrainDataSetManagerInLucene());
		dataCollecter.setTimer(new Timer(true));
		return dataCollecter;
	}

	/**
	 * 获取数据收集器实例（指定训练数据文件名）
	 * 
	 * @param trainDataFileName 训练数据文件名
	 * @return 数据收集器实例
	 */
	public static DataCollecter getInstance(String trainDataFileName) {
		DataCollecter dataCollecter = new DataCollecter();
		dataCollecter.setTrainDataSetManager(new TrainDataSetManagerInLucene());
		dataCollecter.setBufferedReader(ResourceUtil.loadTxtResource(trainDataFileName));
		return dataCollecter;
	}

	/**
	 * 私有构造函数
	 */
	private DataCollecter() {

	}

	/**
	 * 开始收集训练数据
	 * 
	 * 启动定时任务，定期收集训练数据并存储到训练数据集管理器中
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
	 * 
	 * 取消定时任务
	 */
	public void stopCollectData() {
		timer.cancel();
	}

	/**
	 * 从文本文件中收集训练数据项集合
	 * 
	 * @return 训练数据项集合
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

	/**
	 * 从文本文件中收集单个训练数据项
	 * 
	 * @return 训练数据项
	 */
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

	/**
	 * 收集训练数据（静态方法）
	 * 
	 * @return 训练数据项集合
	 */
	public static Collection<IntellacTrainDataItem> collectData() {

		return null;
	}

	// private static IntellacTrainDataItem makeTrainDataItem(String sensorId,
	// float outsideTemp) {
	//
	// return null;
	// }

	/**
	 * 获取训练数据集管理器
	 * 
	 * @return 训练数据集管理器
	 */
	public TrainDataSetManager getTrainDataSetManager() {
		return trainDataSetManager;
	}

	/**
	 * 设置训练数据集管理器
	 * 
	 * @param trainDataSetManager 训练数据集管理器
	 */
	public void setTrainDataSetManager(TrainDataSetManager trainDataSetManager) {
		this.trainDataSetManager = trainDataSetManager;
	}

	/**
	 * 获取缓冲读取器
	 * 
	 * @return 缓冲读取器
	 */
	public BufferedReader getBufferedReader() {
		return bufferedReader;
	}

	/**
	 * 设置缓冲读取器
	 * 
	 * @param bufferedReader 缓冲读取器
	 */
	public void setBufferedReader(BufferedReader bufferedReader) {
		this.bufferedReader = bufferedReader;
	}

	/**
	 * 获取定时器
	 * 
	 * @return 定时器
	 */
	public Timer getTimer() {
		return timer;
	}

	/**
	 * 设置定时器
	 * 
	 * @param timer 定时器
	 */
	public void setTimer(Timer timer) {
		this.timer = timer;
	}

	/**
	 * 数据收集定时任务类
	 * 
	 * 该类继承自TimerTask，用于执行定时数据收集任务
	 */
	private static class DataCollectTask extends TimerTask {

		/**
		 * 训练数据集管理器
		 */
		private TrainDataSetManager trainDataSetManager;

		/**
		 * 构造函数
		 * 
		 * @param trainDataSetManager 训练数据集管理器
		 */
		public DataCollectTask(TrainDataSetManager trainDataSetManager) {
			this.trainDataSetManager = trainDataSetManager;
		}

		/**
		 * 执行定时任务
		 * 
		 * 收集训练数据并存储到训练数据集管理器中
		 */
		@Override
		public void run() {
			Collection<IntellacTrainDataItem> trainDataSet = collectData();
			trainDataSetManager.storeTrainDataSet(trainDataSet);
		}
	}

	/**
	 * 主函数，用于测试
	 * 
	 * @param args 命令行参数
	 */
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