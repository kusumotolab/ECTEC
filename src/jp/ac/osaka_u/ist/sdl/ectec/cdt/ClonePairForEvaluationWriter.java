package jp.ac.osaka_u.ist.sdl.ectec.cdt;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.util.StringUtils;

public class ClonePairForEvaluationWriter implements IClonePairWriter {

	private final PrintWriter pw;

	private final String rootPath;

	public ClonePairForEvaluationWriter(final PrintWriter pw,
			final String rootPath) {
		this.pw = pw;
		this.rootPath = rootPath;
	}

	@Override
	public void write(Collection<ClonePair> clonePairs,
			Map<Long, InstantFileInfo> files) throws Exception {
		for (final ClonePair clonePair : clonePairs) {
			// pw.print(clonePair.getId() + "\t");
			pw.print(StringUtils.translateToCpf(clonePair.getFragment1()
					.getFilePath(), rootPath)
					+ "\t");
			pw.print(clonePair.getFragment1().getStartLine() + "\t");
			pw.print(clonePair.getFragment1().getEndLine() + "\t");
			pw.print(StringUtils.translateToCpf(clonePair.getFragment2()
					.getFilePath(), rootPath)
					+ "\t");
			pw.print(clonePair.getFragment2().getStartLine() + "\t");
			pw.print(clonePair.getFragment2().getEndLine() + "\t");
			pw.println("0");
		}

		pw.close();
	}

}
