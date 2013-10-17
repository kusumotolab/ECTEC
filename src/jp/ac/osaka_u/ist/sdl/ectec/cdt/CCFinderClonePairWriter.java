package jp.ac.osaka_u.ist.sdl.ectec.cdt;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;

public class CCFinderClonePairWriter implements IClonePairWriter {

	private final PrintWriter pw;

	private final Language language;

	public CCFinderClonePairWriter(final PrintWriter pw, final Language language) {
		this.pw = pw;
		this.language = language;
	}

	@Override
	public void write(Collection<ClonePair> clonePairs,
			Map<Long, InstantFileInfo> files) throws Exception {
		printOptions();
		printFileDescriptions(files);
		printClones(clonePairs);
		pw.close();
	}

	private void printOptions() {
		pw.println("#version: ccfinder 7.2.1");
		pw.println("#format: classwise");
		pw.println("#langspec: " + language.getStr());
	}

	private void printFileDescriptions(final Map<Long, InstantFileInfo> files) {
		pw.println("#begin{file description}");

		for (final Map.Entry<Long, InstantFileInfo> entry : files.entrySet()) {
			final InstantFileInfo file = entry.getValue();
			pw.print("0." + file.getFileId());
			pw.print("\t" + file.getLines());
			pw.print("\t" + file.getTokens());
			pw.println("\t" + file.getPath());
		}

		pw.println("#end{file description}");
	}

	private void printClones(final Collection<ClonePair> clonePairs) {
		pw.println("#begin{clone}");

		for (final ClonePair clonePair : clonePairs) {
			final InstantCodeFragmentInfo fragment1 = clonePair.getFragment1();
			final InstantCodeFragmentInfo fragment2 = clonePair.getFragment2();

			pw.println("#begin{set}");
			
			pw.println("0." + fragment1.getFileId() + "\t"
					+ fragment1.getStartLine() + ","
					+ fragment1.getStartColumn() + ",1\t"
					+ fragment1.getEndLine() + "," + fragment1.getEndColumn()
					+ ",2\t1");
			pw.println("0." + fragment2.getFileId() + "\t"
					+ fragment2.getStartLine() + ","
					+ fragment2.getStartColumn() + ",1\t"
					+ fragment2.getEndLine() + "," + fragment2.getEndColumn()
					+ ",2\t1");
			
			pw.println("#end{set}");
		}

		pw.println("#end{clone}");
	}
}
