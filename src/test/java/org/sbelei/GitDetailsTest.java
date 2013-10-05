package org.sbelei;

import static org.junit.Assert.*;

import java.io.File;

import org.eclipse.jgit.lib.Repository;

import org.junit.Test;

import static org.sbelei.TestHelper.*;

public class GitDetailsTest {
	
	@Test
	public void testGetCurrentBranchString() throws Exception {
		File temp = createTempFolder();
		System.out.println(temp.getCanonicalPath());
		createSubFolder(temp,"not_repo");	
		Repository repo = createGitRepo(temp,"test_repo");
		//2. add origin 'foo_bar'
		//3. switch to branch 'branch_1'
		//4. plugin should say that it can work with 'test_repo'
		//5. plugin should get origin url and branch name
		//deleteFolder(temp);
	}


}
