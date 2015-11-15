
public class Matrix2DEntry {
	
	private int genomeID;
	private int pFamID;
	private int count;
	
	public Matrix2DEntry(int genomeID_in, int pFamID_in, int count_in) {
		genomeID  = genomeID_in;
		pFamID    = pFamID_in;
		count     = count_in;
	}
	
	public int getGenomeID() {
		return genomeID;
	}
	
	public int getpFamID() {
		return pFamID;
	}
	
	public int getPFamCount() {
		return count;
	}
}
