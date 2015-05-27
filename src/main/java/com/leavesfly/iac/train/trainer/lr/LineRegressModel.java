package com.leavesfly.iac.train.trainer.lr;

import java.util.Collection;

import com.leavesfly.iac.config.AppContextConstant;
import com.leavesfly.iac.train.domain.TrainDataItem;
import com.leavesfly.iac.train.trainer.TrainModel;

/**
 * 线性回归模型 :y=w0+w1*f1*+w2*f2+...+wi*fi
 * 
 * @see{wiki:http://zh.wikipedia.org/wiki/%E6%9C%80%E5%B0%8F%E4%BA%8C%E4%B9%98%E6%B3%95}
 * 
 * @author yefei.yf
 * 
 */
public class LineRegressModel implements TrainModel {

	private float[] weightArray;

	public static TrainModel getIntance() {
		return new LineRegressModel();
	}

	private LineRegressModel() {
		weightArray = new float[AppContextConstant.SENSOR_NUM + 1];
	}

	@Override
	public <T extends TrainDataItem<Float, Float>> void train(Collection<T> trainDataSet) {
		Matrix trainMatrix = buildTrainMatix(trainDataSet);
		Matrix resultMatrix = buildResultMati(trainDataSet);
		trainMatrix = trainMatrix.insertColumnWithValueOne();
		// 矩阵装置X'
		final Matrix transposeMatrix = Matrix.transpose(trainMatrix);
		// XX'
		final Matrix tmp = Matrix.multiply(transposeMatrix, trainMatrix);
		// (XX')的逆矩阵
		final Matrix inverseOfTmp = Matrix.inverse(tmp);
		// X的转置与Y相乘：X'Y
		final Matrix backPart = Matrix.multiply(transposeMatrix, resultMatrix);
		// (XX')^-1 X'Y
		Matrix weightMatrix = Matrix.multiply(inverseOfTmp, backPart);

		setWeightArray(weightMatrix);
	}

	private static <T extends TrainDataItem<Float, Float>> Matrix buildTrainMatix(
			Collection<T> trainDataSet) {
		Matrix trainMatrix = new Matrix(trainDataSet.size(), trainDataSet.iterator().next()
				.getFeature().length);
		int i = 0;
		for (T type : trainDataSet) {
			int j = 0;
			for (float value : type.getFeature()) {
				trainMatrix.setValueAt(i, j, value);
				j++;
			}
			i++;
		}
		return trainMatrix;
	}

	private static <T extends TrainDataItem<Float, Float>> Matrix buildResultMati(
			Collection<T> trainDataSet) {
		Matrix resultMatrix = new Matrix(trainDataSet.size(), 1);
		int i = 0;
		for (T type : trainDataSet) {
			resultMatrix.setValueAt(i, 0, type.getResult());
			i++;
		}
		return resultMatrix;

	}

	private void setWeightArray(Matrix weightMatrix) {
		if (weightMatrix.getColNum() != weightArray.length) {
			throw new RuntimeException("LineRegressModel.setWeightArray");
		}
		for (int i = 0; i < weightArray.length; i++) {
			weightArray[i] = weightMatrix.getValueAt(i, 0);
		}
	}

	@Override
	public <T extends Number> float useMode(T[] feature) {
		if (feature.length != weightArray.length - 1) {
			throw new IllegalArgumentException();
		}
		Float[] floatFeature = (Float[]) feature;
		float result = 0f;
		for (int i = 0; i < floatFeature.length; i++) {
			result += floatFeature[i] * weightArray[i + 1];
		}
		result += weightArray[0];
		return result;
	}

	/**
	 * 
	 * 矩阵
	 * 
	 * @author yefei.yf
	 *
	 */
	private static class Matrix {
		private int rowNum;
		private int colNum;
		private float[][] data;

		public Matrix(int rowNum, int colNum) {
			this.rowNum = rowNum;
			this.rowNum = colNum;
			data = new float[rowNum][colNum];
		}

		/**
		 * 两矩阵相乘
		 * 
		 * @param matrix1
		 * @param matrix2
		 * @return
		 */
		public static Matrix multiply(Matrix matrix1, Matrix matrix2) {
			Matrix multipliedMatrix = new Matrix(matrix1.getRowNum(), matrix2.getColNum());
			for (int i = 0; i < multipliedMatrix.getRowNum(); i++) {
				for (int j = 0; j < multipliedMatrix.getColNum(); j++) {
					float sum = 0f;
					for (int k = 0; k < matrix1.getColNum(); k++) {
						sum += matrix1.getValueAt(i, k) * matrix2.getValueAt(k, j);
					}
					multipliedMatrix.setValueAt(i, j, sum);
				}
			}
			return multipliedMatrix;
		}

		/**
		 * 求矩阵的装置
		 * 
		 * @param matrix
		 * @return
		 */
		public static Matrix transpose(Matrix matrix) {
			Matrix transposedMatrix = new Matrix(matrix.getColNum(), matrix.getColNum());
			for (int i = 0; i < matrix.getRowNum(); i++) {
				for (int j = 0; j < matrix.getColNum(); j++) {
					transposedMatrix.setValueAt(j, i, matrix.getValueAt(i, j));
				}
			}
			return transposedMatrix;
		}

		/**
		 * 求矩阵的行列式
		 * 
		 * @param matrix
		 * @return
		 */
		public static float determinant(Matrix matrix) {
			if (!matrix.isSquare())
				throw new RuntimeException("matrix need to be square.");
			if (matrix.size() == 1) {
				return matrix.getValueAt(0, 0);
			}
			if (matrix.size() == 2) {
				return (matrix.getValueAt(0, 0) * matrix.getValueAt(1, 1))
						- (matrix.getValueAt(0, 1) * matrix.getValueAt(1, 0));
			}
			float sum = 0f;
			for (int i = 0; i < matrix.getColNum(); i++) {
				sum += changeSign(i) * matrix.getValueAt(0, i)
						* determinant(createSubMatrix(matrix, 0, i));
			}
			return sum;
		}

		/**
		 * 创建一个排除掉指定行和指定列的子矩阵
		 * 
		 * @param matrix
		 * @param excluding_row
		 * @param excluding_col
		 * @return
		 */
		public static Matrix createSubMatrix(Matrix matrix, int excluding_row, int excluding_col) {
			Matrix mat = new Matrix(matrix.getRowNum() - 1, matrix.getColNum() - 1);
			int r = -1;
			for (int i = 0; i < matrix.getRowNum(); i++) {
				if (i == excluding_row)
					continue;
				r++;
				int c = -1;
				for (int j = 0; j < matrix.getColNum(); j++) {
					if (j == excluding_col)
						continue;
					mat.setValueAt(r, ++c, matrix.getValueAt(i, j));
				}
			}
			return mat;
		}

		/**
		 * 求矩阵的伴随矩阵
		 * 
		 * @param matrix
		 * @return
		 * @throws NoSquareException
		 */
		public static Matrix cofactor(Matrix matrix) {
			Matrix mat = new Matrix(matrix.getRowNum(), matrix.getColNum());
			for (int i = 0; i < matrix.getRowNum(); i++) {
				for (int j = 0; j < matrix.getColNum(); j++) {
					mat.setValueAt(i, j, changeSign(i) * changeSign(j)
							* determinant(createSubMatrix(matrix, i, j)));
				}
			}
			return mat;
		}

		/**
		 * 求矩阵的逆
		 * 
		 * @param matrix
		 * @return
		 */
		public static Matrix inverse(Matrix matrix) {
			return (transpose(cofactor(matrix)).multiplyByConstant(1f / determinant(matrix)));
		}

		/**
		 * 改变正负号
		 * 
		 * @param i
		 * @return
		 */
		private static int changeSign(int i) {
			if (i % 2 == 0)
				return 1;
			return -1;
		}

		public int size() {
			if (isSquare()) {
				return rowNum;
			}
			return 0;
		}

		public boolean isSquare() {
			if (rowNum == colNum) {
				return true;
			}
			return false;
		}

		public float getValueAt(int row, int col) {
			if ((row >= 0 && row < rowNum) && (col >= 0 && col < colNum)) {
				return data[row][col];
			}
			return 0f;
		}

		public void setValueAt(int row, int col, float value) {
			if ((row >= 0 && row < rowNum) && (col >= 0 && col < colNum)) {
				data[row][col] = value;
			} else {
				throw new ArrayIndexOutOfBoundsException();
			}
		}

		public int getRowNum() {
			return rowNum;
		}

		public int getColNum() {
			return colNum;
		}

		/**
		 * 矩阵乘以一个常数
		 * 
		 * @param constant
		 * @return
		 */
		public Matrix multiplyByConstant(float constant) {
			Matrix mat = new Matrix(rowNum, colNum);
			for (int i = 0; i < rowNum; i++) {
				for (int j = 0; j < colNum; j++) {
					mat.setValueAt(i, j, data[i][j] * constant);
				}
			}
			return mat;
		}

		/**
		 * 偏差补齐矩阵
		 * 
		 * @return
		 */
		public Matrix insertColumnWithValueOne() {
			Matrix newMatrix = new Matrix(this.rowNum, this.colNum + 1);
			for (int i = 0; i < newMatrix.rowNum; i++) {
				for (int j = 0; j < newMatrix.colNum; j++) {
					if (j == 0)
						newMatrix.setValueAt(i, j, 1.0f);
					else
						newMatrix.setValueAt(i, j, this.getValueAt(i, j - 1));
				}
			}
			return newMatrix;
		}
	}

}
