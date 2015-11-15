
public class Matrix2DInfo {
	
	// min/max values for the matrix
	private Integer minVal;
	private Integer maxVal;
	
	// dimensions of the matrix
	private Integer numRows;
	private Integer numCols;
	
	public Matrix2DInfo() {
	}
	
	public void setInfo(Integer minVal_in, 
			       		Integer maxVal_in,
			       		Integer numRows_in,
			       		Integer numCols_in) {
		minVal  = minVal_in;
		maxVal  = maxVal_in;
		numRows = numRows_in;
		numCols = numCols_in;
	}
	
	public Integer getMinVal() {
		return minVal;
	}
	
	public Integer getMaxVal() {
		return maxVal;
	}
	
	public Integer getNumRows() {
		return numRows;
	}
	
	public Integer getNumCols() {
		return numCols;
	}
			         

}
