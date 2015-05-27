package com.leavesfly.iac.display.plot.demo;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class BarGui extends ApplicationFrame {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		BarGui barGui = new BarGui("MAE");

		barGui.pack();
		RefineryUtilities.centerFrameOnScreen(barGui);
		barGui.setVisible(true);
	}

	public BarGui(String s) {
		super(s);
		setContentPane(createDemoBar());
	}

	// 生成显示图表的面板
	private JPanel createDemoBar() {
		// JFreeChart jfreechart = createChart2(createDataset3());
		 JFreeChart jfreechart = createChart2(createDataset4());
		//JFreeChart jfreechart = createChart2(createDataset5());
		// JFreeChart jfreechart = createChart1(createDataset1());
		ChartPanel chartPanel = new ChartPanel(jfreechart);
		chartPanel.setBackground(Color.WHITE);
		return chartPanel;
	}

	// 生成图表主对象JFreeChart
	private JFreeChart createChart1(CategoryDataset dataset) {

		JFreeChart chart = ChartFactory.createBarChart("", "",// 横轴名称
				"MAE",// 纵轴名称
				dataset,// 数据集
				PlotOrientation.VERTICAL,// 纵向显示
				false,// 显示每个颜色柱子的柱名
				false, false);

		chart.setBorderVisible(false);
		chart.setBackgroundPaint(null);
		chart.setBackgroundImageAlpha(0.0f);

		BarRenderer renderer = new BarRenderer();// 属性修改
		renderer.setBaseOutlinePaint(Color.BLACK);// 设置边框颜色为black
		renderer.setSeriesPaint(0, Color.black);// 设置柱子背景色
		renderer.setMaximumBarWidth(0.08);
		renderer.setItemMargin(0.0);

		CategoryPlot plot = chart.getCategoryPlot();// 设置图的高级属性
		plot.setRenderer(renderer);// 将修改后的属性值保存到图中
		plot.setForegroundAlpha(0.7f);// 柱的透明度
		plot.setBackgroundPaint(Color.WHITE);
		plot.setOutlinePaint(Color.black);
		plot.setRangeCrosshairPaint(Color.black);

		// X轴设置
		CategoryAxis domainAxis = plot.getDomainAxis();
		// 设置X轴坐标上标题的文字
		// domainAxis.setLabelFont(new Font("微软雅黑",Font.BOLD,18));
		// 设置X轴坐标上的文字，
		domainAxis.setTickLabelFont(new Font("微软雅黑", Font.PLAIN, 12));
		// 倾斜横轴上的 Lable 45度倾斜
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		// 设置距离图片左端距离
		domainAxis.setLowerMargin(0.19);
		// 设置距离图片右端距离
		domainAxis.setUpperMargin(0.19);
		plot.setDomainAxis(domainAxis);

		// y轴设置
		ValueAxis rangeAxis = plot.getRangeAxis();
		// 设置Y轴坐标上标题的文字
		rangeAxis.setLabelFont(new Font("微软雅黑", Font.PLAIN, 18));
		// 设置Y轴坐标上的文字
		rangeAxis.setTickLabelFont(new Font("微软雅黑", Font.PLAIN, 12));

		plot.setRangeAxis(rangeAxis);
		chart.setBackgroundPaint(Color.white);
		return chart;
	}

	// 生成图表主对象JFreeChart
	private JFreeChart createChart2(CategoryDataset dataset) {

		JFreeChart chart = ChartFactory.createBarChart("", "",// 横轴名称
				// "MAE",// 纵轴名称
				// "Satisfaction",
				 "Power",
				//"SP",// 纵轴名称
				dataset,// 数据集
				PlotOrientation.VERTICAL,// 纵向显示
				false,// 显示每个颜色柱子的柱名
				false, false);

		chart.setBorderVisible(false);
		chart.setBackgroundPaint(null);
		chart.setBackgroundImageAlpha(0.0f);

		BarRenderer renderer = new BarRenderer();// 属性修改
		renderer.setBaseOutlinePaint(Color.BLACK);// 设置边框颜色为black
		renderer.setSeriesPaint(0, Color.black);// 设置柱子背景色
		renderer.setMaximumBarWidth(0.05);// 设置柱子宽度
		renderer.setItemMargin(0.0);

		CategoryPlot plot = chart.getCategoryPlot();// 设置图的高级属性
		plot.setRenderer(renderer);// 将修改后的属性值保存到图中
		plot.setForegroundAlpha(0.7f);// 柱的透明度
		plot.setBackgroundPaint(Color.WHITE);
		plot.setOutlinePaint(Color.black);
		plot.setRangeCrosshairPaint(Color.black);

		// X轴设置
		CategoryAxis domainAxis = plot.getDomainAxis();
		// 设置X轴坐标上标题的文字
		// domainAxis.setLabelFont(new Font("微软雅黑",Font.BOLD,18));
		// 设置X轴坐标上的文字，
		domainAxis.setTickLabelFont(new Font("微软雅黑", Font.PLAIN, 12));
		// 倾斜横轴上的 Lable 45度倾斜
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		// 设置距离图片左端距离
		domainAxis.setLowerMargin(0.08);
		// 设置距离图片右端距离
		domainAxis.setUpperMargin(0.08);
		plot.setDomainAxis(domainAxis);

		// y轴设置
		ValueAxis rangeAxis = plot.getRangeAxis();
		// 设置Y轴坐标上标题的文字
		rangeAxis.setLabelFont(new Font("微软雅黑", Font.PLAIN, 18));
		// 设置Y轴坐标上的文字
		rangeAxis.setTickLabelFont(new Font("微软雅黑", Font.PLAIN, 12));

		plot.setRangeAxis(rangeAxis);

		return chart;
	}

	// 生成数据
	private CategoryDataset createDataset() {
		double[][] data = new double[][] { { 0.2 }, { 0.16 }, { 0.1 }, { 0.2 }, { 0.13 }, { 0.12 },
				{ 0.14 } };// 设置数据
		String[] rowKeys = { "dsd", "dsds", "2", "30", "40", "50", "60" };// 行标志
		String[] columnKeys = { "" };// 列标志
		CategoryDataset linedataset = DatasetUtilities.createCategoryDataset(rowKeys, columnKeys,
				data); // 建立数据集
		return linedataset;
	}

	// 生成数据
	public static CategoryDataset createDataset0() {
		double[][] data = new double[][] { { 0.101, 0.103, 0.109, 0.098, 0.104, 0.103 } };// 设置数据
		String[] rowKeys = { "" };// 行标志
		String[] columnKeys = { "Point_1", "Point_2", "Point_3", "Point_4", "Point_5", "Point_AVG" };// 列标志
		CategoryDataset linedataset = DatasetUtilities.createCategoryDataset(rowKeys, columnKeys,
				data); // 建立数据集
		return linedataset;
	}

	// 生成数据
	public static CategoryDataset createDataset1() {
		double[][] data = new double[][] { { 0.0984, 0.131 } };// 设置数据
		String[] rowKeys = { "" };// 行标志
		String[] columnKeys = { "BP神经模型", "线性回归模型" };// 列标志
		CategoryDataset linedataset = DatasetUtilities.createCategoryDataset(rowKeys, columnKeys,
				data); // 建立数据集
		return linedataset;
	}

	private CategoryDataset createDataset2() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		dataset.addValue(0.16, "", "5");
		dataset.addValue(0.13, "", "10");
		dataset.addValue(0.125, "", "20");
		dataset.addValue(0.12, "", "30");
		dataset.addValue(0.126, "", "40");
		dataset.addValue(0.135, "", "50");
		dataset.addValue(0.144, "", "60");
		dataset.addValue(0.145, "", "70");
		dataset.addValue(0.16, "", "80");
		dataset.addValue(0.17, "", "90");
		return dataset;
	}

	private CategoryDataset createDataset3() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		dataset.addValue(2.7801654f, "", "User_A");
		dataset.addValue(2.0400827f, "", "User_B");
		dataset.addValue(2.7801654f, "", "User_C");
		dataset.addValue(1.3f, "", "User_D");
		dataset.addValue(2.3400826f, "", "User_E");
		dataset.addValue(2.461096f, "", "AVG");
		dataset.addValue(3.5974036f, "", "PCPSO");
		return dataset;
	}

	private CategoryDataset createDataset4() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		dataset.addValue(246.7931f, "", "User_A");
		dataset.addValue(208.0f, "", "User_B");
		dataset.addValue(219.18182f, "", "User_C");
		dataset.addValue(305.4375f, "", "User_D");
		dataset.addValue(255.61539f, "", "User_E");
		dataset.addValue(243.6f, "", "AVG");
		dataset.addValue(250.18f, "", "PCPSO");
		return dataset;
	}

	private CategoryDataset createDataset5() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		dataset.addValue(1.29f, "", "User_A");
		dataset.addValue(0.98f, "", "User_B");
		dataset.addValue(1.26f, "", "User_C");
		dataset.addValue(0.42f, "", "User_D");
		dataset.addValue(0.91f, "", "User_E");
		dataset.addValue(1.00f, "", "AVG");
		dataset.addValue(1.43f, "", "PCPSO");
		return dataset;
	}

}