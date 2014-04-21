package jp.ac.osaka_u.ist.sdl.instantcdt;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;

public class CCFinderCloneSetWriter implements ICloneSetWriter {

	private final PrintWriter pw;

	private final Language language;

	public CCFinderCloneSetWriter(final PrintWriter pw, final Language language) {
		this.pw = pw;
		this.language = language;
	}

	@Override
	public void write(Collection<CloneSet> cloneSets,
			Map<Long, InstantFileInfo> files) throws Exception {
		printOptions();
		printFileDescriptions(files);
		printClones(cloneSets);
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

	private void printClones(final Collection<CloneSet> cloneSets) {
		pw.println("#begin{clone}");

		for (final CloneSet cloneSet : cloneSets) {

			pw.println("#begin{set}");

			for (final InstantCodeFragmentInfo fragment : cloneSet
					.getElements()) {
				pw.println("0." + fragment.getFileId() + "\t"
						+ fragment.getStartLine() + ","
						+ fragment.getStartColumn() + ",1\t"
						+ fragment.getEndLine() + ","
						+ fragment.getEndColumn() + ",2\t1");
			}


			pw.println("#end{set}");
		}

		pw.println("#end{clone}");
	}

}
