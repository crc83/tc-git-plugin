package org.sbelei;

import static org.sbelei.Column.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import plugins.wdx.FieldValue;
import plugins.wdx.WDXPluginAdapter;

public class GitDetails extends WDXPluginAdapter {
	
	/**
	 * the logging support.
	 */
	private Log log = LogFactory.getLog(GitDetails.class);
	
	static final int FI_BRANCH = 0;
	static final int FI_ORIGIN_URL = 1;
	static final int FI_UPSTREAM_URL = 2;
	
	private FileRepository gitRepo;
	private String gitRepoFileName;

	@Override
	public int contentGetSupportedField(int fieldIndex, StringBuffer fieldName,
			StringBuffer units, int maxlen) {
		for (Column column : Column.values()) {
			if (fieldIndex == column.id()) {
				fieldName.append(column.title());
				return column.type();
			}
		}
		return FieldValue.FT_NOMOREFIELDS;
	}

	@Override
	public int contentGetValue(String fileName, int fieldIndex, int unitIndex,
			FieldValue fieldValue, int maxlen, int flags) {
		switch (fieldIndex) {
		case FI_BRANCH:
			return getCurrentBranchName(fileName, fieldValue);
		case FI_ORIGIN_URL:
			return getOriginUrl(fileName, fieldValue);
		case FI_UPSTREAM_URL:
			return getUpstreamUrl(fileName, fieldValue);
		default:
			return FieldValue.FT_NOMOREFIELDS;
		}
	}
	
	private boolean readGitRepo(String fileName) {
		//caching
		try {
			if ((gitRepoFileName == null) || (!gitRepoFileName.equalsIgnoreCase(fileName))) {
				gitRepo = (FileRepository) new FileRepositoryBuilder()
					.readEnvironment() // scan environment GIT_* variables
					.findGitDir(new File(fileName)) // scan up the file system tree
					.addCeilingDirectory(new File(fileName))
					.build(); 
				gitRepoFileName = fileName;
				log.debug("[G]"+fileName+" || "+gitRepo.getWorkTree());
			}
		} catch (Exception e){
			log.error("Error during creation of repo object:"+e.getMessage());
			log.error(e.getStackTrace().toString());
		}		
		if (gitRepo == null) {
			return false;
		} else {
			return true;
		}
	}
	
	private int getLastCommitMessage(String fileName, FieldValue fieldValue) {
		try {
			Iterable<RevCommit> commitList = Git.open(new File(fileName)).log().setMaxCount(1).call();
			RevCommit commit = commitList.iterator().next();
			return 0;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	private int getOriginUrl(String fileName, FieldValue fieldValue) {
		return getRemooteUrl("origin", fileName, fieldValue);
	}
	
	private int getUpstreamUrl(String fileName, FieldValue fieldValue) {
		return getRemooteUrl("upstream", fileName, fieldValue);
	}

	
	private int getRemooteUrl(String remooteName, String fileName, FieldValue fieldValue) {
		if (!hasDotGitSubfolder(fileName)) {
			return FieldValue.FT_FIELDEMPTY;
		}
		if (!readGitRepo(fileName)) {
			return FieldValue.FT_FIELDEMPTY;
		}
		Config config = gitRepo.getConfig();
		String url = config.getString("remote", remooteName, "url");
		if (url == null) {
			return FieldValue.FT_FIELDEMPTY;
		}
		fieldValue.setValue(ORIGIN_URL.type(), url);
		return ORIGIN_URL.type();
		
	}

	private int getCurrentBranchName(String fileName, FieldValue fieldValue) {
		if (!hasDotGitSubfolder(fileName)) {
			return FieldValue.FT_FIELDEMPTY;
		}
		if (!readGitRepo(fileName)) {
			return FieldValue.FT_FIELDEMPTY;
		}
		try {
			fieldValue.setValue(BRANCH_NAME.type(), gitRepo.getBranch());
		} catch (IOException e) {
			return FieldValue.FT_FIELDEMPTY;
		}
		return BRANCH_NAME.type();
	}

	private boolean hasDotGitSubfolder(String fileName) {
		return new File(fileName, ".git").isDirectory();
	}

}
