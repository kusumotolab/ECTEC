package jp.ac.osaka_u.ist.sdl.ectec.cdt;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.ast.ASTCreator;
import jp.ac.osaka_u.ist.sdl.ectec.detector.sourceanalyzer.hash.IHashCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.detector.sourceanalyzer.normalizer.NormalizerCreator;
import jp.ac.osaka_u.ist.sdl.ectec.settings.AnalyzeGranularity;
import jp.ac.osaka_u.ist.sdl.ectec.settings.MessagePrinter;

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

	private final AtomicInteger index;

	private final IHashCalculator hashCalculator;

	private final NormalizerCreator normalizerCreator;

	private final int threshold;

	private final AnalyzeGranularity granularity;

	public InstantCodeFragmentDetectingThread(
			final String[] filePaths,
			final ConcurrentMap<String, List<InstantCodeFragmentInfo>> fragments,
			final AtomicInteger index, final IHashCalculator hashCalculator,
			final NormalizerCreator normalizerCreator, final int threshold,
			final AnalyzeGranularity granularity) {
		this.filePaths = filePaths;
		this.fragments = fragments;
		this.index = index;
		this.hashCalculator = hashCalculator;
		this.normalizerCreator = normalizerCreator;
		this.threshold = threshold;
		this.granularity = granularity;
	}

	@Override
	public void run() {
		while (true) {
			final int currentIndex = index.getAndIncrement();

			if (currentIndex >= filePaths.length) {
				break;
			}

			final String filePath = filePaths[currentIndex];
			MessagePrinter.println("\t[" + currentIndex + "/"
					+ filePaths.length + "] analyzing " + filePath + "...");

			final File file = new File(filePath);
			FileReader reader = null;

			try {
				final CompilationUnit root = ASTCreator.createAST(file);

				reader = new FileReader(file);

				final Lexer lexer = new JavaLexer(reader);
				final List<Token> tokens = lexer.runLexicalAnalysis();

				final InstantCodeFragmentDetectingVisitor visitor = new InstantCodeFragmentDetectingVisitor(
						tokens, filePath, hashCalculator, normalizerCreator,
						threshold, granularity);

				root.accept(visitor);

				this.fragments.put(filePath, visitor.getDetectedFragments());

			} catch (Exception e) {
				e.printStackTrace();
				MessagePrinter.ePrintln("something is wrong in processing "
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
