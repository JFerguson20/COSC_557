//COSC 557 Final Project


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class Matrix2DReader {	
	public static void loadTSV(File f, 
							   List<Matrix2DEntry[]> matrix, 
							   ArrayList<String> pFamNames,
							   ArrayList<String> genomeNames,
							   Matrix2DInfo matrixInfo,
							   ArrayList<Short> rowOffsets) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(f));
		
		// matrix related info to be set
		int minVal = Integer.MAX_VALUE, maxVal = Integer.MIN_VALUE;
		int numRows = 0, numCols = 0;
		
		int line_counter = 0;
		boolean skip_line = false;
		
		String line = reader.readLine();			
		while (line != null) {
			if (line_counter == 0) {
				
				// split string on tab character for .tsv
				String[] parts = line.split("\\t");
				for(int i=1; i<parts.length; i++){
					pFamNames.add(parts[i]);
				}
				//numCols = parts.length - 1;
				line_counter++;
				line = reader.readLine();
				continue;
			}
				
			// split string on tab character for .tsv
			String[] parts = line.split("\\t");
			/*int numNonZero = 0;
			for(int i = 1; i < parts.length; i++ ) { 
				if(Integer.parseInt(parts[i]) != 0) { numNonZero++; }
			}
			rowOffsets.add((short)(parts.length - 1 - numNonZero));
			System.out.println("offset: " + (parts.length - 1 - numNonZero));
			*/
			// genome name is first entry on each row
			genomeNames.add(parts[0]);
			
			// number of vals in a row is parts.length - 1 since we ignore genome name
			//Matrix2DEntry[] rowVals = new Matrix2DEntry[numNonZero];
			Matrix2DEntry[] rowVals = new Matrix2DEntry[parts.length - 1];
			int genomeID = line_counter - 1;
			int currEntry = 0;
			try {
				for(int pFamID=1; pFamID <parts.length; pFamID++){
					short count = (short)Integer.parseInt(parts[pFamID]);
					//if(count != 0) {
						//System.out.println("curr: " + currEntry + "\tactual: " + numNonZero);
						rowVals[currEntry++] = new Matrix2DEntry(genomeID, pFamID - 1, count);
					
						// set min/max Vals
						if(count < minVal) { minVal = count; }
						if(count > maxVal) { maxVal = count; }
					//}
				}
			} catch (NumberFormatException ex) {
				System.out.println("DataSet.readCSV(): NumberFormatException caught so skipping record. " + ex.fillInStackTrace());
				skip_line = true;
				break;
			}
			
			if (!skip_line) {
				matrix.add(rowVals);
			}
			
			line_counter++;
			line = reader.readLine();
		}
		reader.close();
		
		numRows = matrix.size();
		numCols = matrix.get(0).length;
		
		// set the matrix info
		matrixInfo.setInfo(minVal, maxVal, numRows, numCols);
	}
	public static void main(String args[]) throws Exception {
		
		File f = new File("./data/result/translated_Metabolism_PfamA.matrix.tsv");
		ArrayList<String> pFamNames = new ArrayList<String>();
		ArrayList<String> genomeNames = new ArrayList<String>();
		Matrix2DInfo matrixInfo = new Matrix2DInfo();
		List<Matrix2DEntry[]> matrix = new ArrayList<Matrix2DEntry[]>();
		ArrayList<Short> rowOffsets = new ArrayList<Short>();
		
		Matrix2DReader.loadTSV(f, matrix, pFamNames, genomeNames, matrixInfo, rowOffsets);
		
		for(int i = 0; i < matrix.size(); i++) {
			for(int j = 0; j < matrix.get(i).length; j++) {
				System.out.print(pFamNames.get(matrix.get(i)[j].getpFamID()));
				System.out.print("\t");
				System.out.print(genomeNames.get(matrix.get(i)[j].getGenomeID()));
				System.out.print("\t");
				System.out.print(matrix.get(i)[j].getPFamCount());
				System.out.print("\n");
			}
			//System.out.println("");
		}
		
		System.out.println("min: " + matrixInfo.getMinVal().toString());
		System.out.println("max: " + matrixInfo.getMaxVal().toString());
		System.out.println("num rows: " + matrixInfo.getNumRows().toString());
		System.out.println("num cols: " + matrixInfo.getNumCols().toString());
	}
}
