package jp.ac.osaka_u.ist.sdl.ectec.analyzer;

import static org.junit.Assert.*;
import jp.ac.osaka_u.ist.sdl.ectec.settings.VersionControlSystem;

import org.junit.BeforeClass;
import org.junit.Test;

public class GenealogyAnalyzerTest {

	private static GenealogyAnalyzer analyzer;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String dbPath = "G:\\dbfiles\\ectec-clonetracker.db";
		String repositoryPath = "file:///G:/repositories/repository-clonetracker";
		analyzer = GenealogyAnalyzer.setup(dbPath, repositoryPath,
				VersionControlSystem.SVN);
	}

	@Test
	public void test() {
		try {
			analyzer.getController().concretizeCloneGenealogy(10);
			assertTrue(analyzer.getDataManagerManager().getCloneGenealogyManager().contains(10));
		} catch (Exception e) {
			fail();
		}
	}

}
