package jp.ac.osaka_u.ist.sdl.ectec.app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.GenealogyAnalyzer;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CloneGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.selector.IConstraint;

public class BasicGenealogyAnalyzer {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		final String db = args[0];
		final String repository = args[1];
		final String output = args[2];

		final GenealogyAnalyzer analyzer = GenealogyAnalyzer.setup(db, true);

		final IConstraint constraint = null; // change if needed

		final Map<Long, CloneGenealogyInfo> genealogies = analyzer
				.selectAndConcretizeCloneGenealogies(constraint);

		final PrintWriter pw = new PrintWriter(new BufferedWriter(
				new FileWriter(new File(output))));
		pw.println("ID,#_addition,#_change,#_deletion,start_rev,end_rev,#_elements(latest),length(latest)");

		for (final CloneGenealogyInfo genealogy : genealogies.values()) {
			pw.print(genealogy.getId() + ",");
			pw.print(genealogy.getNumberOfAdditions() + ",");
			pw.print(genealogy.getNumberOfChanges() + ",");
			pw.print(genealogy.getNumberOfDeletions() + ",");
//			pw.print(genealogy.getCombinedStartRevision().getIdentifier() + ",");
//			pw.print(genealogy.getCombinedEndRevision().getIdentifier() + ",");

			final CloneSetInfo latest = genealogy.getClones().get(
					genealogy.getClones().size() - 1);

			pw.print(latest.getElements().size() + ",");
			pw.print(latest.getElements().get(0).getSize());

			pw.println();
		}

		pw.close();

		analyzer.close();
	}

}
