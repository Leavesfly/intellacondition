package com.leavesfly.iac.display.plot.demo;

/* --------------------------
 * XYSplineRendererDemo1.java
 * --------------------------
 * (C) Copyright 2007, by Object Refinery Limited.
 *
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

/**
 * A demo showing a line chart drawn using spline curves.
 */
public class XYSplineRendererDemo2 extends ApplicationFrame {

	static class DemoPanel extends JPanel {

		/** Dataset 1. */
		private XYDataset data1;

		/**
		 * Creates a new instance.
		 */
		public DemoPanel() {
			super(new BorderLayout());
			this.data1 = createSampleData();
			add(createContent());
		}

		/**
		 * Creates and returns a sample dataset. The data was randomly
		 * generated.
		 *
		 * @return a sample dataset.
		 */
		private XYDataset createSampleData() {
			XYSeries series = new XYSeries("PCPSOX");
			series.add(1, 0.101);
			series.add(2, 0.103);
			series.add(3, 0.109);
			series.add(4, 0.098);
			series.add(5, 0.104);
			XYSeriesCollection result = new XYSeriesCollection(series);

			return result;
		}

		/**
		 * Creates a tabbed pane for displaying sample charts.
		 *
		 * @return the tabbed pane.
		 */
		private JTabbedPane createContent() {
			JTabbedPane tabs = new JTabbedPane();
			tabs.add("Splines:", createChartPanel1());
			tabs.add("Lines:", createChartPanel2());
			return tabs;
		}

		/**
		 * Creates a chart based on the first dataset, with a fitted linear
		 * regression line.
		 *
		 * @return the chart panel.
		 */
		private ChartPanel createChartPanel1() {

			// create plot...
			NumberAxis xAxis = new NumberAxis("节点位置");
			xAxis.setAutoRangeIncludesZero(false);
			NumberAxis yAxis = new NumberAxis("MAE");
			yAxis.setAutoRangeIncludesZero(false);

			XYSplineRenderer renderer1 = new XYSplineRenderer();
			XYPlot plot = new XYPlot(this.data1, xAxis, yAxis, renderer1);
			plot.setBackgroundPaint(Color.white);
			plot.setDomainGridlinePaint(Color.black);
			plot.setRangeGridlinePaint(Color.black);
			plot.setAxisOffset(new RectangleInsets(4, 4, 4, 4));

			// create and return the chart panel...
			JFreeChart chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
			chart.setBackgroundPaint(Color.white);

			// -------------------------

			chart.setBorderVisible(false);
			chart.setBackgroundPaint(null);
			chart.setBackgroundImageAlpha(0.0f);

			BarRenderer renderer = new BarRenderer();// 属性修改
			renderer.setBaseOutlinePaint(Color.BLACK);// 设置边框颜色为black
			renderer.setSeriesPaint(0, Color.black);// 设置柱子背景色
			renderer.setMaximumBarWidth(0.05);// 设置柱子宽度
			renderer.setItemMargin(0.0);

			// X轴设置
			ValueAxis domainAxis = plot.getDomainAxis();
			// 设置X轴坐标上标题的文字
			domainAxis.setLabelFont(new Font("微软雅黑", Font.BOLD, 16));
			// 设置X轴坐标上的文字，
			domainAxis.setTickLabelFont(new Font("微软雅黑", Font.PLAIN, 12));

			// y轴设置
			ValueAxis rangeAxis = plot.getRangeAxis();
			// 设置Y轴坐标上标题的文字
			rangeAxis.setLabelFont(new Font("微软雅黑", Font.PLAIN, 16));
			// 设置Y轴坐标上的文字
			rangeAxis.setTickLabelFont(new Font("微软雅黑", Font.PLAIN, 12));

			ChartPanel chartPanel = new ChartPanel(chart, false);
			chartPanel.setBackground(Color.WHITE);
			return chartPanel;

		}

		/**
		 * Creates a chart based on the second dataset, with a fitted power
		 * regression line.
		 *
		 * @return the chart panel.
		 */
		private ChartPanel createChartPanel2() {

			// create subplot 1...
			NumberAxis xAxis = new NumberAxis("节点位置");
			xAxis.setAutoRangeIncludesZero(false);
			NumberAxis yAxis = new NumberAxis("MAE");
			yAxis.setAutoRangeIncludesZero(false);

			XYLineAndShapeRenderer renderer1 = new XYLineAndShapeRenderer();
			XYPlot plot = new XYPlot(this.data1, xAxis, yAxis, renderer1);
			plot.setBackgroundPaint(Color.white);
			plot.setDomainGridlinePaint(Color.black);
			plot.setRangeGridlinePaint(Color.black);
			plot.setAxisOffset(new RectangleInsets(4, 4, 4, 4));

			// create and return the chart panel...
			JFreeChart chart = new JFreeChart("XYLineAndShapeRenderer",
					JFreeChart.DEFAULT_TITLE_FONT, plot, true);
			chart.setBackgroundPaint(Color.white);
			ChartPanel chartPanel = new ChartPanel(chart, false);

			chart.setBorderVisible(false);
			chart.setBackgroundPaint(null);
			chart.setBackgroundImageAlpha(0.0f);

			BarRenderer renderer = new BarRenderer();// 属性修改
			renderer.setBaseOutlinePaint(Color.BLACK);// 设置边框颜色为black
			renderer.setSeriesPaint(0, Color.black);// 设置柱子背景色
			renderer.setMaximumBarWidth(0.05);// 设置柱子宽度
			renderer.setItemMargin(0.0);

			// X轴设置
			ValueAxis domainAxis = plot.getDomainAxis();
			// 设置X轴坐标上标题的文字
			domainAxis.setLabelFont(new Font("微软雅黑", Font.BOLD, 18));
			// 设置X轴坐标上的文字，
			domainAxis.setTickLabelFont(new Font("微软雅黑", Font.PLAIN, 12));

			// y轴设置
			ValueAxis rangeAxis = plot.getRangeAxis();
			// 设置Y轴坐标上标题的文字
			rangeAxis.setLabelFont(new Font("微软雅黑", Font.PLAIN, 18));
			// 设置Y轴坐标上的文字
			rangeAxis.setTickLabelFont(new Font("微软雅黑", Font.PLAIN, 12));

			chartPanel.setBackground(Color.WHITE);

			return chartPanel;

		}

	}

	/**
	 * Creates a new instance of the demo application.
	 *
	 * @param title
	 *            the frame title.
	 */
	public XYSplineRendererDemo2(String title) {
		super(title);
		JPanel content = createDemoPanel();
		getContentPane().add(content);
	}

	/**
	 * Creates a panel for the demo (used by SuperDemo.java).
	 * 
	 * @return A panel.
	 */
	public static JPanel createDemoPanel() {
		return new DemoPanel();
	}

	/**
	 * The starting point for the regression demo.
	 *
	 * @param args
	 *            ignored.
	 */
	public static void main(String args[]) {
		XYSplineRendererDemo2 appFrame = new XYSplineRendererDemo2(
				"JFreeChart: XYSplineRendererDemo1.java");
		appFrame.pack();
		RefineryUtilities.centerFrameOnScreen(appFrame);
		appFrame.setVisible(true);
	}

}
