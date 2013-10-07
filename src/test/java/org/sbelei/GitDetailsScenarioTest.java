package org.sbelei;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import plugins.wdx.FieldValue;
import static org.sbelei.TestHelper.*;


public class GitDetailsScenarioTest {
	
	private static final int NO_UNIT_INDEX = 0;
	private static final int NO_MAX_LENGTH = 0;
	private static final int NO_FLAGS = 0;

	private static File temp;
	private static List<File> folders;
	
	/*
	 * test folder stricture
	 * [not_reop]
	 * [testrepo1](master/git/origin)
	 * 
	 * ------------
	 * [notrepo1]
	 * [testrepo1](br1/git/origin1)
	 * [notrepo2]
	 * 		[testrepo2](br2/git/origin2)
	 * 		[testrepo3](br3/git/no origin)
	 * some_file
	 */
	@BeforeClass
	public static void globalSetUp() throws Exception{
		temp = createTempFolder();
		System.out.println("[TEST] "+temp.getCanonicalPath());
		folders = new LinkedList<File>();
		folders.add(createSubFolder(temp,"notrepo1"));//0
		folders.add(createGitRepo(temp,"testrepo1", "git@origin1/url","branch1"));//1
		folders.add(createSubFolder(temp,"notrepo2"));//2
		File notrepo2 = folders.get(2);
		folders.add(createGitRepo(notrepo2,"testrepo2", "git@origin3/url","branch3"));//3
		folders.add(createGitRepo(notrepo2,"testrepo3", null,"branch4"));//4
	}
	
	/*
	 * Given : non git folder
	 * When  : we retrieve current branch for it
	 * Then  : we receive "field is empty" response
	 */
	@Test
	public void test00InPlainFolderGetCurrentBranch() throws Exception {
		FieldValue result = new FieldValue();
		int responce =methodUnderTestInvocation(folders.get(0), GitDetails.FI_BRANCH, result);
		
		assertEquals(FieldValue.FT_FIELDEMPTY, responce);
	}
	
	@Test
	public void test02InPlainFolderGetCurrentBranch() throws Exception {
		FieldValue result = new FieldValue();
		int responce =methodUnderTestInvocation(folders.get(2), GitDetails.FI_BRANCH, result);
		
		assertEquals(FieldValue.FT_FIELDEMPTY, responce);
	}

	private int methodUnderTestInvocation(File folderToCheck, int field,
			FieldValue result) throws IOException {
		GitDetails gitDetails = new GitDetails();
		return gitDetails.contentGetValue(folderToCheck.getCanonicalPath(), 
				field, 
				NO_UNIT_INDEX, 
				result, 
				NO_MAX_LENGTH, 
				NO_FLAGS);
	}

	/*
	 * Given : git folder
	 * When  : we retrieve current branch for it
	 * Then  : we receive "master"
	 */
	@Test
	public void test03InGitGetCurrentBranch() throws Exception {

		FieldValue result = new FieldValue();
		int responce =methodUnderTestInvocation(folders.get(3), GitDetails.FI_BRANCH, result);
		
		assertEquals(FieldValue.FT_STRING, responce);
		assertEquals(FieldValue.FT_STRING, result.getFieldType());
		assertEquals("branch3", result.getStr());
	}

	@Test
	public void test04InGitGetCurrentBranch() throws Exception {

		FieldValue result = new FieldValue();
		int responce =methodUnderTestInvocation(folders.get(4), GitDetails.FI_BRANCH, result);
		
		assertEquals(FieldValue.FT_STRING, responce);
		assertEquals(FieldValue.FT_STRING, result.getFieldType());
		assertEquals("branch4", result.getStr());
	}

	@Test
	public void test01InGitGetCurrentBranch() throws Exception {

		FieldValue result = new FieldValue();
		int responce =methodUnderTestInvocation(folders.get(1), GitDetails.FI_BRANCH, result);
		
		assertEquals(FieldValue.FT_STRING, responce);
		assertEquals(FieldValue.FT_STRING, result.getFieldType());
		assertEquals("branch1", result.getStr());
	}

	/*
	 * Given : git folder with remote "origin" set
	 * When  : we retrieve origin url
	 * Then  : we receive "git@foobar/url" response
	 */
	@Test
	public void test01InGitGetOriginIfSet() throws Exception {

		FieldValue result = new FieldValue();
		int responce = methodUnderTestInvocation(folders.get(1), GitDetails.FI_ORIGIN_URL, result);
		
		assertEquals(FieldValue.FT_STRING, responce);
		assertEquals(FieldValue.FT_STRING, result.getFieldType());
		assertEquals("git@origin1/url", result.getStr());
	}
	
	@Test
	public void test03InGitGetOriginIfSet() throws Exception {

		FieldValue result = new FieldValue();
		int responce = methodUnderTestInvocation(folders.get(3), GitDetails.FI_ORIGIN_URL, result);
		
		assertEquals(FieldValue.FT_STRING, responce);
		assertEquals(FieldValue.FT_STRING, result.getFieldType());
		assertEquals("git@origin3/url", result.getStr());
	}

	/*
	 * Given : git folder with no remotes set
	 * When  : we retrieve origin url
	 * Then  : we receive "field is empty" response
	 */
	@Test
	public void test04InGitGetOriginIfSet() throws Exception {

		FieldValue result = new FieldValue();
		int responce = methodUnderTestInvocation(folders.get(4), GitDetails.FI_ORIGIN_URL, result);
		
		assertEquals(FieldValue.FT_FIELDEMPTY, responce);
	}

	
	@AfterClass
	public static void globalTearDown(){
		//deleteFolder(temp);		
	}


}
