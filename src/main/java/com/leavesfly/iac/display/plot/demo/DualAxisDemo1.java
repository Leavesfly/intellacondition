package com.leavesfly.iac.display.plot.demo;

/* ------------------
 * DualAxisDemo1.java
 * ------------------
 * (C) Copyright 2002-2005, by Object Refinery Limited.
 *
 */

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
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.BorderArrangement;
import org.jfree.chart.block.EmptyBlock;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.CompositeTitle;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

/**
 * A simple demonstration application showing how to create a dual axis chart
 * based on data from two {@link CategoryDataset} instances.
 */
public class DualAxisDemo1 extends ApplicationFrame {

	/**
	 * Creates a new demo instance.
	 *
	 * @param title
	 *            the frame title.
	 */
	public DualAxisDemo1(String title) {
		super(title);
		JFreeChart chart = createChart();
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);
	}

	/**
	 * Creates a sample dataset.
	 *
	 * @return The dataset.
	 */
	private static CategoryDataset createDataset1() {

		// row keys...
		String series1 = "S1";
		// String series2 = "S2";
		// String series3 = "S3";

		// column keys...
		String category1 = "Point_1";
		String category2 = "Point_2";
		String category3 = "Point_3";
		String category4 = "Point_4";
		String category5 = "Point_5";

		// create the dataset...
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		dataset.addValue(0.101, series1, category1);
		dataset.addValue(0.103, series1, category2);
		dataset.addValue(0.109, series1, category3);
		dataset.addValue(0.098, series1, category4);
		dataset.addValue(0.104, series1, category5);

		// dataset.addValue(5.0, series2, category1);
		// dataset.addValue(7.0, series2, category2);
		// dataset.addValue(6.0, series2, category3);
		// dataset.addValue(8.0, series2, category4);
		// dataset.addValue(4.0, series2, category5);
		// dataset.addValue(4.0, series2, category6);
		// dataset.addValue(2.0, series2, category7);
		// dataset.addValue(1.0, series2, category8);
		//
		// dataset.addValue(4.0, series3, category1);
		// dataset.addValue(3.0, series3, category2);
		// dataset.addValue(2.0, series3, category3);
		// dataset.addValue(3.0, series3, category4);
		// dataset.addValue(6.0, series3, category5);
		// dataset.addValue(3.0, series3, category6);
		// dataset.addValue(4.0, series3, category7);
		// dataset.addValue(3.0, series3, category8);

		return dataset;

	}

	/**
	 * Creates a sample dataset.
	 *
	 * @return The dataset.
	 */
	private static CategoryDataset createDataset2() {

		// row keys...
		String series1 = "S4";

		// column keys...
		String category1 = "Point_1";
		String category2 = "Point_2";
		String category3 = "Point_3";
		String category4 = "Point_4";
		String category5 = "Point_5";

		// create the dataset...
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		dataset.addValue(0.101, series1, category1);
		dataset.addValue(0.103, series1, category2);
		dataset.addValue(0.109, series1, category3);
		dataset.addValue(0.098, series1, category4);
		dataset.addValue(0.104, series1, category5);

		return dataset;

	}

	/**
	 * Creates the demo chart.
	 * 
	 * @return The chart.
	 */
	private static JFreeChart createChart() {
		// create the chart...
		JFreeChart chart = ChartFactory.createBarChart("", // chart
															// title
				"Point", // domain axis label
				"MAE", // range axis label
				createDataset1(), // data
				PlotOrientation.VERTICAL, false, // include legend
				true, // tooltips?
				false // URL generator? Not required...
				);

		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
		chart.setBackgroundPaint(Color.white);

		// get a reference to the plot for further customisation...
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setBackgroundPaint(new Color(0xEE, 0xEE, 0xFF));

		CategoryDataset dataset2 = createDataset2();
		plot.setDataset(1, dataset2);
		plot.mapDatasetToRangeAxis(1, 1);

		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
		ValueAxis axis2 = new NumberAxis("MAE");
		axis2.setLabelFont(new Font("微软雅黑", Font.PLAIN, 16));
		// 设置Y轴坐标上的文字
		axis2.setTickLabelFont(new Font("微软雅黑", Font.PLAIN, 12));

		plot.setRangeAxis(1, axis2);

		LineAndShapeRenderer renderer2 = new LineAndShapeRenderer();
		renderer2.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
		plot.setRenderer(1, renderer2);
		plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

		LegendTitle legend1 = new LegendTitle(plot.getRenderer(0));
		legend1.setMargin(new RectangleInsets(2, 2, 2, 2));
		legend1.setFrame(new BlockBorder());

		LegendTitle legend2 = new LegendTitle(plot.getRenderer(1));
		legend2.setMargin(new RectangleInsets(2, 2, 2, 2));
		legend2.setFrame(new BlockBorder());

		BarRenderer renderer = new BarRenderer();// 属性修改
		renderer.setBaseOutlinePaint(Color.BLACK);// 设置边框颜色为black
		renderer.setSeriesPaint(0, Color.black);// 设置柱子背景色
		renderer.setMaximumBarWidth(0.05);// 设置柱子宽度
		renderer.setItemMargin(0.0);
		plot.setRenderer(renderer);// 将修改后的属性值保存到图中

		plot.setForegroundAlpha(0.7f);// 柱的透明度
		plot.setBackgroundPaint(Color.WHITE);
		plot.setOutlinePaint(Color.black);
		plot.setRangeCrosshairPaint(Color.black);

		BlockContainer container = new BlockContainer(new BorderArrangement());
		container.add(legend1, RectangleEdge.LEFT);
		container.add(legend2, RectangleEdge.RIGHT);
		container.add(new EmptyBlock(2000, 0));
		CompositeTitle legends = new CompositeTitle(container);
		legends.setPosition(RectangleEdge.BOTTOM);
		chart.addSubtitle(legends);

		chart.setBorderVisible(false);
		chart.setBackgroundPaint(null);
		chart.setBackgroundImageAlpha(0.0f);

		// X轴设置
		CategoryAxis domainAxis1 = plot.getDomainAxis();
		// 设置X轴坐标上标题的文字
		domainAxis1.setLabelFont(new Font("微软雅黑", Font.PLAIN, 16));
		// 设置X轴坐标上的文字，
		domainAxis1.setTickLabelFont(new Font("微软雅黑", Font.PLAIN, 12));

		// y轴设置
		ValueAxis rangeAxis = plot.getRangeAxis();
		// 设置Y轴坐标上标题的文字
		rangeAxis.setLabelFont(new Font("微软雅黑", Font.PLAIN, 16));
		// 设置Y轴坐标上的文字
		rangeAxis.setTickLabelFont(new Font("微软雅黑", Font.PLAIN, 12));

		// OPTIONAL CUSTOMISATION COMPLETED.
		chart.setBackgroundPaint(Color.white);
		return chart;
	}

	/**
	 * Creates a panel for the demo (used by SuperDemo.java).
	 * 
	 * @return A panel.
	 */
	public static JPanel createDemoPanel() {
		JFreeChart chart = createChart();
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setBackground(Color.WHITE);
		return chartPanel;
	}

	/**
	 * Starting point for the demonstration application.
	 *
	 * @param args
	 *            ignored.
	 */
	public static void main(String[] args) {
		DualAxisDemo1 demo = new DualAxisDemo1("Dual Axis Demo 1");
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
	}

}
