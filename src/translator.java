/*  COSC 557 Final Project
 *  Genome MD5-name translator
 * 
 *  This class reads in the Genome(MD5)-Pfam tsv file and MD5-Name table,
 *  translates it into Genome(Name)-Pfam tsv file and write to disk.
 * 
 *  By Shiqi Zhong
 *  Nov. 13, 2015
 * */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class translator {

	// Read in the MD5 table and save the relation into a HashTable.
	public static void readMD5(File f, Hashtable genome) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(f));

		String line = reader.readLine();
		while (line != null) {
			String[] parts = line.split("\\t");
			String md5 = parts[0];
			String genomeName = parts[1];
			genome.put(md5, genomeName);
			line = reader.readLine();
		}
	}
	
	// Read in the tsv file, translate the genome names and write to the buffer.
	public static void convertTSV(File f, Hashtable genome, BufferedWriter outputMatrix) throws IOException {
		
		BufferedReader reader = new BufferedReader(new FileReader(f));

		String line = reader.readLine();
		int line_counter = 0;

		while (line != null) {
			// Directly write the attributes into buffer
			if (line_counter == 0){
				outputMatrix.write(line + "\n");
			}
			if (line_counter > 0) {
			// Translate the genome's MD5 to name
				String[] parts = line.split("\\t");
				
				String genomeName = (String) genome.get(parts[0]);
				String row = genomeName;
				for(int i=1; i<parts.length; i++){
					row = row + "\t" + parts[i];
				}
			outputMatrix.write(row + "\n");
			}
			line = reader.readLine();
			line_counter++;
			outputMatrix.flush();
		}
	}

	public static void main(String args[]) throws Exception {
		// Initialize Genome(MD5)-Pfam file
		File inputMatrix = new File(
				"./data/minimatrix/Metabolism_PfamA.matrix.tsv");

		// Initialize the MD5 table file
		File md5 = new File("./data/MD5_Name/MD5_Name_63kGenomes.txt");

		// Initialize the output file
		BufferedWriter outputMatrix = new BufferedWriter(new FileWriter(new File(
				"./data/result/translated_Metabolism_PfamA.matrix.tsv")));

		// Convert the MD5 file into hashtable
		Hashtable genome = new Hashtable();
		translator.readMD5(md5, genome);
		
		// Translate and write to disk
		translator.convertTSV(inputMatrix, genome, outputMatrix);
		outputMatrix.close();
	}
}
