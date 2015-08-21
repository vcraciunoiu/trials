package de.schlund.rtstat.graphs.refactor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Changes a whole graph file to use the new BAP/BS servers
 * 
 * @author rcirstoiu
 * 
 */
public class GraphFileChanger {

	File graphFile;
	StringBuilder newFileContents = new StringBuilder();
	SingleLineChanger basBapRefactor = new SingleLineChanger();

	public GraphFileChanger(File file) {
		graphFile = file;
	}

	public void changeFile() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(graphFile));

		String readLine = null;
		Boolean firstLineRead = true;
		while ((readLine = reader.readLine()) != null) {
			if (firstLineRead) {
				newFileContents.append(basBapRefactor.refactorLine(readLine));
				firstLineRead = false;
			} else {
				newFileContents.append("\r\n" + basBapRefactor.refactorLine(readLine));
			}
		}
		reader.close();

		BufferedWriter writer = new BufferedWriter(new FileWriter(graphFile));
		writer.write(newFileContents.toString());
		writer.flush();
		writer.close();

	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		new GraphFileChanger(new File("F:/workspaces/abuseWorkspace/rtstat/stats-conf/prod2instances_bap_bs/ner_minus_prefix.xml")).changeFile();

	}

}
