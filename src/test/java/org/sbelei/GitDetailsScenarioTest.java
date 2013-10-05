package org.sbelei;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.lib.Repository;
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
	private static File repo;
	
	@BeforeClass
	public static void globalSetUp() throws IOException{
		temp = createTempFolder();
		System.out.println("[TEST] "+temp.getCanonicalPath());
		notRepo =createSubFolder(temp,"not_repo");	
		Repository gitRepo = createGitRepo(temp,"test_repo");
		repo = gitRepo.getWorkTree();
		//2. add origin 'foo_bar'
		//3. switch to branch 'branch_1'
		//4. plugin should say that it can work with 'test_repo'
		//5. plugin should get origin url and branch name
	}
	
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

		
//		assertEquals(FieldValue.FT_STRING, retrievedBranchName.getFieldType());
//		assertEquals("", retrievedBranchName.getStr());
	}

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
	
	@AfterClass
	public static void globalTearDown(){
		//deleteFolder(temp);		
	}


}
