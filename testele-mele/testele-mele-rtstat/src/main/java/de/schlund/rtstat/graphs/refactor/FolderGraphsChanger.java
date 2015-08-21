package de.schlund.rtstat.graphs.refactor;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

/**
 * Changes any *.xml graph files in a given folder to use the new BAP/BS severs
 * 
 * @author rcirstoiu
 * 
 */
public class FolderGraphsChanger {

	public static void main(String[] args) throws IOException {
		new FolderGraphsChanger("/home/radu/work/rtstat/stats-conf/prod2instances/").changeAllXmlFilesInFolder();
	}

	String folderName;

	public FolderGraphsChanger(String folderName) {
		this.folderName = folderName;
	}

	public void changeAllXmlFilesInFolder() throws IOException {
		File folder = new File(folderName);

		validateFolder(folder);

		File[] filesInsideFolder = getXmlFilesInFolder(folder);

		for (File file : filesInsideFolder) {
			new GraphFileChanger(file).changeFile();
		}

	}

	private File[] getXmlFilesInFolder(File folder) {
		File[] filesInsideFolder = folder.listFiles(new FileFilter() {

			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				if (pathname.getName().endsWith(".xml")) {
					return true;
				} else {
					return false;
				}
			}
		});
		return filesInsideFolder;
	}

	private void validateFolder(File folder) {
		if (!folder.exists()) {
			throw new IllegalArgumentException("Folder: " + folderName + " does not exist");
		}

		if (!folder.isDirectory()) {
			throw new IllegalArgumentException(folderName + " is not a file");
		}
	}

}
