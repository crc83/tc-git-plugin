package org.sbelei;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import plugins.wdx.FieldValue;
import plugins.wdx.WDXPluginAdapter;

public class GitDetails extends WDXPluginAdapter {
	
	/**
	 * the logging support.
	 */
	private Log log = LogFactory.getLog(GitDetails.class);
	
	static final int FI_BRANCH = 0;
	static final int FI_BRANCH_TYPE = FieldValue.FT_STRING;
	static final int FI_ORIGIN_URL = 1;
	static final int FI_ORIGIN_URL_TYPE = FieldValue.FT_STRING;
	private Repository gitRepo;
	private String gitRepoFileName;

	@Override
	public int contentGetSupportedField(int fieldIndex, StringBuffer fieldName,
			StringBuffer units, int maxlen) {
		switch (fieldIndex) {
		case FI_BRANCH: 
			fieldName.append("branch name");
			return FI_BRANCH_TYPE;
		case FI_ORIGIN_URL:
			fieldName.append("origin url");
			return FI_ORIGIN_URL_TYPE;
		default:
			return FieldValue.FT_NOMOREFIELDS;		
		}
	}

	@Override
	public int contentGetValue(String fileName, int fieldIndex, int unitIndex,
			FieldValue fieldValue, int maxlen, int flags) {
		switch (fieldIndex) {
		case FI_BRANCH:
			return getCurrentBranchName(fileName, fieldValue);
		case FI_ORIGIN_URL:
			return getOriginUrl(fileName, fieldValue);
		default:
			return FieldValue.FT_NOMOREFIELDS;
		}
	}
	
	private boolean readGitRepo(String fileName) {
		//caching
		try {
			if ((gitRepoFileName == null) || (!gitRepoFileName.equalsIgnoreCase(fileName))) {
				gitRepo = new FileRepositoryBuilder()
					.readEnvironment() // scan environment GIT_* variables
					.findGitDir(new File(fileName)) // scan up the file system tree
					.addCeilingDirectory(new File(fileName))
					.build(); 
				gitRepoFileName = fileName;
				log.debug("[G]"+fileName+" || "+gitRepo.getWorkTree());
			}
		} catch (Exception e){
			//nothing special, we will check later if object is created
		}		
		if (gitRepo == null) {
			return false;
		} else {
			return true;
		}
	}

	private int getOriginUrl(String fileName, FieldValue fieldValue) {
		if (!hasDotGitSubfolder(fileName)) {
			return FieldValue.FT_FIELDEMPTY;
		}
		if (!readGitRepo(fileName)) {
			return FieldValue.FT_FIELDEMPTY;
		}
		Config config = gitRepo.getConfig();
		String url = config.getString("remote", "origin", "url");
		if (url == null) {
			return FieldValue.FT_FIELDEMPTY;
		}
		fieldValue.setValue(FI_ORIGIN_URL_TYPE, url);
		return FI_ORIGIN_URL_TYPE;
	}

	private int getCurrentBranchName(String fileName, FieldValue fieldValue) {
		if (!hasDotGitSubfolder(fileName)) {
			return FieldValue.FT_FIELDEMPTY;
		}
		if (!readGitRepo(fileName)) {
			return FieldValue.FT_FIELDEMPTY;
		}
		try {
			fieldValue.setValue(FI_BRANCH_TYPE, gitRepo.getBranch());
		} catch (IOException e) {
			return FieldValue.FT_FIELDEMPTY;
		}
		return FI_BRANCH_TYPE;
	}

	private boolean hasDotGitSubfolder(String fileName) {
        File [] fileNames;
        File file=new File(fileName);
        if((file!=null) && file.isDirectory()){
            fileNames= file.listFiles();
            if (fileNames == null) {
            	return false;
            }
            for(File temp:fileNames){
                if ((temp!=null) &&".git".equals(temp.getName())) {
                	return true;
                }
            }
        }
		return false;
	}

}
