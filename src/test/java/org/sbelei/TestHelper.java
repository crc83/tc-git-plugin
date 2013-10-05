package org.sbelei;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class TestHelper {

	/**
	 * Creates temp folder with name "tempXXXXXXX".
	 * 
	 * @return object that corresponds to physically created temp folder
	 * @throws IOException
	 *             in case it can't create it
	 */
	public static File createTempFolder() throws IOException {
		final File temp;

		temp = File.createTempFile("temp", Long.toString(System.nanoTime()));

		if (!(temp.delete())) {
			throw new IOException("Could not delete temp file: "
					+ temp.getAbsolutePath());
		}

		if (!(temp.mkdir())) {
			throw new IOException("Could not create temp directory: "
					+ temp.getAbsolutePath());
		}

		return (temp);
	}

	/**
	 * Removes folder and it's content.
	 * 
	 * @param folder
	 *            to remove (be carefull)
	 */
	public static void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if (files != null) { // some JVMs return null for empty dirs
			for (File f : files) {
				if (f.isDirectory()) {
					deleteFolder(f);
				} else {
					f.delete();
				}
			}
		}
		folder.delete();
	}

	public static Repository createGitRepo(File temp, String repoName) throws IOException {
		// prepare a new folder
		createSubFolder(temp, repoName);
		// create the directory
		Repository repository = FileRepositoryBuilder.create(new File(createSubFolder(temp, repoName), ".git"));
		repository.create();

		return repository;

	}

	public static File createSubFolder(File temp, String folderName) {
		File subfolder = new File(temp, folderName);
		subfolder.mkdir();
		return subfolder;
		
	}

}
