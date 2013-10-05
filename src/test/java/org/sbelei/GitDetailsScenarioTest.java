package org.sbelei;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import plugins.wdx.FieldValue;
import static org.sbelei.TestHelper.*;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GitDetailsScenarioTest {
	
	private static final int NO_UNIT_INDEX = 0;
	private static final int NO_MAX_LENGTH = 0;
	private static final int NO_FLAGS = 0;

	private static File temp;
	private static File notRepo;
	private static Repository gitRepo;
	private static File repo;
	
	@BeforeClass
	public static void globalSetUp() throws IOException{
		temp = createTempFolder();
		System.out.println("[TEST] "+temp.getCanonicalPath());
		notRepo =createSubFolder(temp,"not_repo");	
		gitRepo = createGitRepo(temp,"test_repo");

		repo = gitRepo.getWorkTree();
	}
	
	/*
	 * Given : non git folder
	 * When  : we retrieve current branch for it
	 * Then  : we receive "field is empty" response
	 */
	@Test
	public void test01InPlainFolderGetCurrentBranch() throws Exception {
		GitDetails gitDetails = new GitDetails();
		FieldValue retrievedBranchName = new FieldValue();
		int responce =gitDetails.contentGetValue(notRepo.getCanonicalPath(), 
				GitDetails.FI_BRANCH, 
				NO_UNIT_INDEX, 
				retrievedBranchName, 
				NO_MAX_LENGTH, 
				NO_FLAGS);
		
		assertEquals(FieldValue.FT_FIELDEMPTY, responce);
	}

	/*
	 * Given : git folder
	 * When  : we retrieve current branch for it
	 * Then  : we receive "master"
	 */
	@Test
	public void test02InGitGetCurrentBranch() throws Exception {
		GitDetails gitDetails = new GitDetails();
		FieldValue retrievedBranchName = new FieldValue();
		int responce =gitDetails.contentGetValue(repo.getCanonicalPath(), 
				GitDetails.FI_BRANCH, 
				NO_UNIT_INDEX, 
				retrievedBranchName, 
				NO_MAX_LENGTH, 
				NO_FLAGS);
		
		assertEquals(FieldValue.FT_STRING, responce);
		assertEquals(FieldValue.FT_STRING, retrievedBranchName.getFieldType());
		assertEquals("master", retrievedBranchName.getStr());
	}

	/*
	 * Given : git folder with no remotes set
	 * When  : we retrieve origin url
	 * Then  : we receive "field is empty" response
	 */
	@Test
	public void test03InGitGetOriginIfNotSet() throws Exception {
		GitDetails gitDetails = new GitDetails();
		FieldValue retrievedOriginUrl = new FieldValue();
		int responce =gitDetails.contentGetValue(repo.getCanonicalPath(), 
				GitDetails.FI_ORIGIN_URL, 
				NO_UNIT_INDEX, 
				retrievedOriginUrl, 
				NO_MAX_LENGTH, 
				NO_FLAGS);
		
		assertEquals(FieldValue.FT_FIELDEMPTY, responce);		
	}

	/*
	 * Given : git folder with remote "origin" set
	 * When  : we retrieve origin url
	 * Then  : we receive "git@foobar/url" response
	 */
	@Test
	public void test04InGitGetOriginIfSet() throws Exception {
		//add remoote
		StoredConfig config = gitRepo.getConfig();
		config.setString("remote", "origin", "url", "git@foobar/url");
		config.save();

		GitDetails gitDetails = new GitDetails();
		FieldValue retrievedOriginUrl = new FieldValue();
		int responce =gitDetails.contentGetValue(repo.getCanonicalPath(), 
				GitDetails.FI_ORIGIN_URL, 
				NO_UNIT_INDEX, 
				retrievedOriginUrl, 
				NO_MAX_LENGTH, 
				NO_FLAGS);
		
		assertEquals(FieldValue.FT_STRING, responce);
		assertEquals(FieldValue.FT_STRING, retrievedOriginUrl.getFieldType());
		assertEquals("git@foobar/url", retrievedOriginUrl.getStr());
	}

	
	@AfterClass
	public static void globalTearDown(){
		//deleteFolder(temp);		
	}


}
