
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Matrix2D {
	
	// actual matrix data
	private List<Matrix2DEntry[]> matrix = new ArrayList<Matrix2DEntry[]>();
	
	// small class holding info about matrix
	private Matrix2DInfo matrixInfo = new Matrix2DInfo();
	
	// column headers of protein family names
	private ArrayList<String> pFamNames = new ArrayList<String>();
	
	// row headers of genome names
	private ArrayList<String> genomeNames = new ArrayList<String>();
	
	public Matrix2D(File matrixTsv) throws Exception {
		Matrix2DReader.loadTSV(matrixTsv, matrix, pFamNames, genomeNames, matrixInfo);
	}
	
	public Integer getMinVal() {
		return matrixInfo.getMinVal();
	}
	
	public Integer getMaxVal() {
		return matrixInfo.getMaxVal();
	}
	
	public Integer getNumRows() {
		return matrixInfo.getNumRows();
	}
	
	public Integer getNumCols() {
		return matrixInfo.getNumCols();
	}
	
	public Integer getPFamCount(int genomeRowID, int pFamColID) {
		return matrix.get(genomeRowID)[pFamColID].getPFamCount();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
