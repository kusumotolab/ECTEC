package jp.ac.osaka_u.ist.sdl.ectec.analyzer;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.BlockInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CloneGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.selector.RevisionRangeConstraint;
import jp.ac.osaka_u.ist.sdl.ectec.settings.VersionControlSystem;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class GenealogyAnalyzerTest {

	private static GenealogyAnalyzer analyzer;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String dbPath = "G:\\dbfiles\\carol.db";
		String repositoryPath = "file:///G:/repositories/repository-carol";
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

	@Ignore
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

	@Test
	public void test3() {
		try {
			final Map<Long, CloneGenealogyInfo> genealogies = analyzer.selectAndConcretizeCloneGenealogies(null);
			for (final CloneGenealogyInfo genealogy : genealogies.values()) {
				for (final CloneSetInfo clone : genealogy.getClones()) {
					for (final CodeFragmentInfo fragment : clone.getElements()) {
						final BlockInfo<?> block = (BlockInfo<?>) fragment;
						if (block.getNode() == null) {
							fail();
						}
					}
				}
			}
			assertTrue(true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
