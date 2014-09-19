package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sei;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.GenealogyAnalyzer;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CodeFragmentGenealogyInfo;

public class FragmentGenealogyAnalyzerForSei {

	private static final String header = "ID,START_REPOSITORY,START_REV,START_FILE,START_FROMLINE,START_TOLINE,END_REPOSITORY,END_REV,END_FILE,END_FROMLINE,END_TOLINE,#_MOD_IN_ORIGINAL_REPOSITORY,#_MOD_IN_OTHER_REPOSITORIES";

	public static void main(String[] args) throws Exception {
		final String dbPath = args[0];
		final String outputCsvFilePath = args[1];
		final long genealogyId = Long.parseLong(args[2]);

		final File outputCsvFile = new File(outputCsvFilePath);
		final PrintWriter pw = new PrintWriter(new BufferedWriter(
				new FileWriter(outputCsvFile, outputCsvFile.exists())));

		if (!outputCsvFile.exists()) {
			pw.println(header);
		}

		final GenealogyAnalyzer analyzer = GenealogyAnalyzer
				.setup(dbPath, true);
		final CodeFragmentGenealogyInfo genealogy = analyzer
				.concretizeFragmentGenealogy(genealogyId);

		final SeiCodeFragmentGenealogyVisitor visitor = new SeiCodeFragmentGenealogyVisitor();
		genealogy.accept(visitor);
		
		if (visitor.getOutputLine() != null) {
			pw.println(visitor.getOutputLine());
		}

		pw.close();
	}

}
