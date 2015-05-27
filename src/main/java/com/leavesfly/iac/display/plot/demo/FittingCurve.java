package com.leavesfly.iac.display.plot.demo;

import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class FittingCurve extends ApplicationFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<Double> equation = null;
	// 多项式的次数
	int times = 2;

	public FittingCurve(String title) {
		super(title);
		// 计算拟合多项式中各项前的系数。多项式的次数从高到低，，需要的参数为x轴数据组成的List，y轴数据List，和需要进行拟合多项式的次数
		this.equation = this.getCurveEquation(this.getData().get(0), this.getData().get(1),
				this.times);
		JFreeChart chart = this.getChart();
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		chartPanel.setMouseZoomable(true, false);
		setContentPane(chartPanel);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FittingCurve demo = new FittingCurve("XYFittingCurve");
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);

	}

	public JFreeChart getChart() {

		XYDataset xydataset = this.getXYDataset();
		// 创建用坐标表示的折线图
		JFreeChart xyChart = ChartFactory.createXYLineChart("二次多项式拟合光滑曲线", "X轴", "Y轴", xydataset,
				PlotOrientation.VERTICAL, true, true, false);
		// 生成坐标点点的形状
		XYPlot plot = (XYPlot) xyChart.getPlot();

		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(false);// 坐标点的形状是否可见
			renderer.setBaseShapesFilled(false);
		}
		ValueAxis yAxis = plot.getRangeAxis();
		yAxis.setLowerMargin(2);
		return xyChart;
	}

	public XYDataset getXYDataset() {
		// 拟合而来的曲线绘制
		XYSeries s1 = new XYSeries("拟合曲线");
		// 获取拟合多项式系数，equation在构造方法中已经实例化
		List<Double> list = this.equation;
		// 获取原始数据
		List<List<Double>> data = this.getData();

		// get Max and Min of x;
		List<Double> xList = data.get(0);
		double max = this.getMax(xList);
		double min = this.getMin(xList);
		double step = max - min;
		double x = min;
		double step2 = step / 800.0;
		// 按照多项式的形式 还原多项式，并计算给定值所对应的多项式的值，给定值作为x，计算值作为y
		for (int i = 0; i < 800; i++) {
			x = x + step2;
			int num = list.size() - 1;
			double temp = 0.0;
			for (int j = 0; j < list.size(); j++) {
				temp = temp + Math.pow(x, (num - j)) * list.get(j);
			}
			s1.add(x, temp);
		}

		// 通过对原始数据进行点点连线
		XYSeries s2 = new XYSeries("点点连线");
		for (int i = 0; i < data.get(0).size(); i++) {
			s2.add(data.get(0).get(i), data.get(1).get(i));
		}
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(s1);
		dataset.addSeries(s2);
		return dataset;

	}

	// 获取原始绘图计算数据
	public List<List<Double>> getData() {
		// x为x轴坐标
		List<Double> x = new ArrayList<Double>();
		List<Double> y = new ArrayList<Double>();
		for (int i = 0; i < 10; i++) {
			x.add(-5.0 + i);
		}
		y.add(26.0);
		y.add(17.1);
		y.add(10.01);
		y.add(5.0);
		y.add(2.01);

		y.add(1.0);

		y.add(2.0);
		y.add(5.01);
		y.add(10.1);
		y.add(17.001);

		List<List<Double>> list = new ArrayList<List<Double>>();
		list.add(x);
		list.add(y);
		return list;

	}

	// 最小二乘法多项式拟合
	public List<Double> getCurveEquation(List<Double> x, List<Double> y, int m) {
		if (x.size() != y.size() || x.size() <= m + 1) {
			return new ArrayList<Double>();
		}
		List<Double> result = new ArrayList<Double>();
		List<Double> S = new ArrayList<Double>();
		List<Double> T = new ArrayList<Double>();
		// 计算S0 S1 …… S2m
		for (int i = 0; i <= 2 * m; i++) {
			double si = 0.0;
			for (double xx : x) {
				si = si + Math.pow(xx, i);
			}
			S.add(si);
		}
		// 计算T0 T1 …… Tm
		for (int j = 0; j <= m; j++) {
			double ti = 0.0;
			for (int k = 0; k < y.size(); k++) {
				ti = ti + y.get(k) * Math.pow(x.get(k), j);
			}
			T.add(ti);
		}

		// 把S和T 放入二维数组，作为矩阵
		double[][] matrix = new double[m + 1][m + 2];
		for (int k = 0; k < m + 1; k++) {
			double[] matrixi = matrix[k];
			for (int q = 0; q < m + 1; q++) {
				matrixi[q] = S.get(k + q);
			}
			matrixi[m + 1] = T.get(k);
		}
		for (int p = 0; p < matrix.length; p++) {
			for (int pp = 0; pp < matrix[p].length; pp++) {
				System.out.print(" matrix[" + p + "][" + pp + "]=" + matrix[p][pp]);
			}
			System.out.println();
		}
		// 把矩阵转化为三角矩阵
		matrix = this.matrixConvert(matrix);
		// 计算多项式系数，多项式从高到低排列
		result = this.MatrixCalcu(matrix);
		return result;
	}

	// 矩阵转换为三角矩阵
	public double[][] matrixConvert(double[][] d) {
		for (int i = 0; i < d.length - 1; i++) {
			double[] dd1 = d[i];
			double num1 = dd1[i];

			for (int j = i; j < d.length - 1; j++) {
				double[] dd2 = d[j + 1];
				double num2 = dd2[i];

				for (int k = 0; k < dd2.length; k++) {
					dd2[k] = (dd2[k] * num1 - dd1[k] * num2);
				}
			}
		}
		for (int ii = 0; ii < d.length; ii++) {
			for (int kk = 0; kk < d[ii].length; kk++)
				System.out.print(d[ii][kk] + " ");
			System.out.println();
		}
		return d;
	}

	// 计算一元多次方程前面的系数， 其排列为 xm xm-1 …… x0（多项式次数从高到低排列）
	public List<Double> MatrixCalcu(double[][] d) {

		int i = d.length - 1;
		int j = d[0].length - 1;
		List<Double> list = new ArrayList<Double>();
		double res = d[i][j] / d[i][j - 1];
		list.add(res);

		for (int k = i - 1; k >= 0; k--) {
			double num = d[k][j];
			for (int q = j - 1; q > k; q--) {
				num = num - d[k][q] * list.get(j - 1 - q);
			}
			res = num / d[k][k];
			list.add(res);
		}
		return list;
	}

	// 获取List中Double数据的最大最小值
	public double getMax(List<Double> data) {
		double res = data.get(0);
		for (int i = 0; i < data.size() - 1; i++) {
			if (res < data.get(i + 1)) {
				res = data.get(i + 1);
			}
		}
		return res;
	}

	public double getMin(List<Double> data) {
		double res = data.get(0);
		for (int i = 0; i < data.size() - 1; i++) {
			if (res > data.get(i + 1)) {
				res = data.get(i + 1);
			}
		}
		return res;
	}

}
