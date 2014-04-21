package jp.ac.osaka_u.ist.sdl.instantcdt;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.ast.ASTCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.hash.IHashCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.normalizer.NormalizerCreator;
import jp.ac.osaka_u.ist.sdl.ectec.settings.AnalyzeGranularity;

import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * A thread class that detects fragments
 * 
 * @author k-hotta
 * 
 */
public class InstantCodeFragmentDetectingThread implements Runnable {

	private final String[] filePaths;

	private final ConcurrentMap<String, List<InstantCodeFragmentInfo>> fragments;

	private final ConcurrentMap<Long, InstantFileInfo> files;

	private final AtomicInteger index;

	private final IHashCalculator hashCalculator;

	private final NormalizerCreator normalizerCreator;

	private final int tokenThreshold;

	private final AnalyzeGranularity granularity;

	private final int lineThreshold;

	public InstantCodeFragmentDetectingThread(
			final String[] filePaths,
			final ConcurrentMap<String, List<InstantCodeFragmentInfo>> fragments,
			final ConcurrentMap<Long, InstantFileInfo> files,
			final AtomicInteger index, final IHashCalculator hashCalculator,
			final NormalizerCreator normalizerCreator,
			final int tokenThreshold, final AnalyzeGranularity granularity,
			final int lineThreshold) {
		this.filePaths = filePaths;
		this.fragments = fragments;
		this.files = files;
		this.index = index;
		this.hashCalculator = hashCalculator;
		this.normalizerCreator = normalizerCreator;
		this.tokenThreshold = tokenThreshold;
		this.granularity = granularity;
		this.lineThreshold = lineThreshold;
	}

	@Override
	public void run() {
		while (true) {
			final int currentIndex = index.getAndIncrement();

			if (currentIndex >= filePaths.length) {
				break;
			}

			final String filePath = filePaths[currentIndex];
			System.out.println("\t[" + (currentIndex + 1) + "/"
					+ filePaths.length + "] analyzing " + filePath + "...");

			final File file = new File(filePath);
			FileReader reader = null;

			try {
				final CompilationUnit root = ASTCreator.createAST(file);

				reader = new FileReader(file);

				final Lexer lexer = new JavaLexer(reader);
				final List<Token> tokens = lexer.runLexicalAnalysis();

				final InstantCodeFragmentDetectingVisitor visitor = new InstantCodeFragmentDetectingVisitor(
						tokens, filePath, currentIndex, hashCalculator,
						normalizerCreator, tokenThreshold, granularity,
						lineThreshold);

				root.accept(visitor);

				this.fragments.put(filePath, visitor.getDetectedFragments());

				final int lineCount = root.getLineNumber(root.getLength() - 1);
				final int tokenCount = tokens.size();

				final InstantFileInfo fileInfo = new InstantFileInfo(
						currentIndex, filePath, tokenCount, lineCount);
				files.put((long) currentIndex, fileInfo);

			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("something is wrong in processing "
						+ filePath);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
