package org.sbelei;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import plugins.wdx.FieldValue;
import plugins.wdx.WDXPluginAdapter;

public class GitDetails extends WDXPluginAdapter {
	
	static final int FI_BRANCH = 0;
	static final int FI_BRANCH_TYPE = FieldValue.FT_STRING;
	static final int FI_ORIGIN_URL = 1;
	static final int FI_ORIGIN_URL_TYPE = FieldValue.FT_STRING;
	private Repository gitRepo;
	private FileRepositoryBuilder builder = new FileRepositoryBuilder();

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
			gitRepo = builder
				.readEnvironment() // scan environment GIT_* variables
				.findGitDir(new File(fileName)) // scan up the file system tree
				.build(); 
		} catch (Exception e){
			//nothing special, we will create new object
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
		fieldValue.setValue(FI_ORIGIN_URL_TYPE, "");
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
        if(file.isDirectory()){
            fileNames= file.listFiles();
            for(File temp:fileNames){
                if (".git".equals(temp.getName())) {
                	return true;
                }
            }
        }
		return false;
	}

}
