
public class Matrix2DEntry {
	
	private int genomeID;
	private int pFamID;
	private short count;
	
	public Matrix2DEntry(int genomeID_in, int pFamID_in, short count_in) {
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
	
	public short getPFamCount() {
		return count;
	}
}
