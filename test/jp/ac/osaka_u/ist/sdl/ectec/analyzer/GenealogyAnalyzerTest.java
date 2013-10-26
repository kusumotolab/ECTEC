package jp.ac.osaka_u.ist.sdl.ectec.analyzer;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.selector.RevisionRangeConstraint;
import jp.ac.osaka_u.ist.sdl.ectec.settings.VersionControlSystem;

import org.junit.BeforeClass;
import org.junit.Ignore;
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

	@Ignore
	public void test() {
		try {
			analyzer.concretizeCloneGenealogy(10);
			assertTrue(analyzer.getDataManagerManager()
					.getCloneGenealogyManager().contains(10));
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test2() {
		try {
			final RevisionRangeConstraint constraint = new RevisionRangeConstraint(
					analyzer.getDBConnectionManager());
			constraint.setStartRevision("10");
			constraint.setEndRevision("20");

			analyzer.selectAndConcretizeCloneGenealogies(constraint);
			assertTrue(analyzer.getDataManagerManager()
					.getCloneGenealogyManager().contains(10));
		} catch (Exception e) {
			fail();
		}
	}

}
