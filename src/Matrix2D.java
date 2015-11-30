
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
	
	private ArrayList<Short>  rowOffsets = new ArrayList<Short>();
	
	public Matrix2D(File matrixTsv) throws Exception {
		Matrix2DReader.loadTSV(matrixTsv, matrix, pFamNames, genomeNames, matrixInfo, rowOffsets);
	}
	
	//create smaller matrix from bigger matrix
	public Matrix2D(Matrix2D bigMatrix, ArrayList<Integer> selectedRows){
		ArrayList<String> allPFamNames = bigMatrix.pFamNames;
		ArrayList<String> allGenomeNames = bigMatrix.genomeNames;
		
		//add all the pFamNames for now.
		ArrayList<Integer> cols = new ArrayList<Integer>();
		for(int pf = 0; pf < allPFamNames.size(); pf++)
		{
			boolean showThisPF = false;
			for(int sr = 0; sr < selectedRows.size(); sr++)
			{
				short count = bigMatrix.getPFamCount(selectedRows.get(sr), pf);
				if(count > 0)
					showThisPF = true;
			}
			if(showThisPF){
				pFamNames.add(allPFamNames.get(pf));
				cols.add(pf);
			}
		}
			
		//add only the selectRow Names.
		for(int i : selectedRows){
			genomeNames.add(allGenomeNames.get(i));
		}
		int maxVal = -10000;
		int minVal = 10000;
		//add the matrix entries
		
		for(int row = 0; row < selectedRows.size(); row++){
			Matrix2DEntry[] rowEntries= new Matrix2DEntry[pFamNames.size()];
			for(int col = 0; col < pFamNames.size(); col++){
				short count = bigMatrix.getPFamCount(selectedRows.get(row), cols.get(col));
				if(count > maxVal)
					maxVal = count;
				if(count < minVal)
					minVal = count;

				rowEntries[col] = new Matrix2DEntry(row, col, count);
			}
			matrix.add(rowEntries);
		}
		
		//add the matrixInfo
		matrixInfo.setInfo(minVal, maxVal, selectedRows.size(), cols.size());
		
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
	
	public Short getPFamCount(int genomeRowID, int pFamColID) {
		return matrix.get(genomeRowID)[pFamColID].getPFamCount();
	}
	
	public Matrix2DEntry[] getRow(int genomeRowID) {
		return matrix.get(genomeRowID);
	}
	
	public String getPFamName(int pFamColID) {
		return pFamNames.get(pFamColID);
	}
	
	public String getGenomeName(int genomeRowID) {
		return genomeNames.get(genomeRowID);
	}
	
	public ArrayList<String> getAllGenomeNames(){
		return genomeNames;
	}
	
	public ArrayList<Short>  getRowOffsets() {
		return rowOffsets;
	}
}
