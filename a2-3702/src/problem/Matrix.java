package problem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Matrix {
	
	private int numRows;
	private int numCols;
	private ArrayList<ArrayList<Double>> data;
	
	public Matrix(double[][] input) {
		numRows = input.length;
		numCols = input[0].length;
		data = new ArrayList<ArrayList<Double>>(numRows);
		for (int i = 0; i < numRows; i++) {
			data.add(new ArrayList<Double>(numCols));
			for (int j = 0; j < numCols; j++) {
				data.get(i).add(input[i][j]);
			}
		}
	}
	
	public double get(int row, int col) {
		return data.get(row).get(col);
	} 
	
	public List<Double> getRow(int row) {
		return Collections.unmodifiableList(data.get(row));
	}
	
	public int getNumRows(){
		return this.numRows;
	}
	
	public int getNumCols() {
		return this.numCols;
	}
	
	/*
	 * Multiplies a given matrix with this matrix
	 * 
	 * foreach row in a (this):
	 * 	foreach col in b:
	 * 		get the dot product of a.row and b.col and set it as result[a.row][b.col] 
	 * 
	 * @param Matrix b - an n * m matrix
	 * @require (this.numRows == x) && (b.numCols == x) 
	 * @return Matrix C - resulting Matrix
	 * @return null - unable to perform multiplication
	 * */
	public Matrix multiply(Matrix b) {
		if (this.getNumCols() != b.getNumRows()) {
			return null;
		}
		double[][] result = new double[this.getNumCols()][b.getNumRows()];
		
		for(int i = 0; i < numRows; i++) {
			for(int j = 0; j < b.getNumCols(); j++) {
				double	dotProduct = 0.0;
				for(int x = 0; x < numCols; x++) {
					//argh! so confusing! data structure, why u do dis?
					dotProduct += (this.get(i, x) * b.getRow(x).get(j)); //eg: this [row 0, col 1] * b [row 1, col 0]
				}
				result[i][j] = dotProduct;
			}
		}
		return new Matrix(result);
	}
	/*
	 * Scalar multiplication of this matrix with the given int
	 * @param double b - scalar quantity
	 * @return Matrix C - resulting Matrix
	 */
	public Matrix multiply(double b) {
		double[][] result = new double[numRows][numCols];
		
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numCols; j++) {
				result[i][j] = b * data.get(i).get(j);
			}
		}

		return new Matrix(result);
	}
	
}
