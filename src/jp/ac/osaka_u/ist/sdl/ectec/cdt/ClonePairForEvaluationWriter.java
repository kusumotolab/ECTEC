package jp.ac.osaka_u.ist.sdl.ectec.cdt;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;

public class ClonePairForEvaluationWriter implements IClonePairWriter {

	private final PrintWriter pw;

	public ClonePairForEvaluationWriter(final PrintWriter pw) {
		this.pw = pw;
	}

	@Override
	public void write(Collection<ClonePair> clonePairs,
			Map<Long, InstantFileInfo> files) throws Exception {
		for (final ClonePair clonePair : clonePairs) {
			pw.print(clonePair.getId() + "\t");
			pw.print(clonePair.getFragment1().getFilePath() + "\t");
			pw.print(clonePair.getFragment1().getStartLine() + "\t");
			pw.print(clonePair.getFragment1().getEndLine() + "\t");
			pw.print(clonePair.getFragment2().getFilePath() + "\t");
			pw.print(clonePair.getFragment2().getStartLine() + "\t");
			pw.print(clonePair.getFragment2().getEndLine() + "\t");
			pw.println("0");
		}

		pw.close();
	}

}
