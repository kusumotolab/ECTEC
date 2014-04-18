package jp.ac.osaka_u.ist.sdl.ectec.cdt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.hash.DefaultHashCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.normalizer.NormalizerCreator;
import jp.ac.osaka_u.ist.sdl.ectec.settings.AnalyzeGranularity;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;
import jp.ac.osaka_u.ist.sdl.ectec.settings.MessagePrintLevel;
import jp.ac.osaka_u.ist.sdl.ectec.settings.MessagePrinter;
import jp.ac.osaka_u.ist.sdl.ectec.settings.StringNormalizeMode;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

public class InstantCloneDetector {

	private static String inputDir;

	private static String outputFile;

	private static IClonePairWriter pairWriter;

	private static ICloneSetWriter setWriter;

	private static Language language;

	private static int tokenThreshold;

	private static int lineThreshold;

	private static NormalizerCreator normalizerCreator;

	private static AnalyzeGranularity granularity;

	private static int threadsCount;

	private static boolean useFileList;

	public static void main(String[] args) {
		try {
			final long start = System.nanoTime();
			parseArgs(args);
			MessagePrinter.setLevel(MessagePrintLevel.VERBOSE);

			List<String> paths = null;

			if (useFileList) {
				MessagePrinter.println("loading the given file list ...");
				final FileListLoader loader = new FileListLoader();
				paths = loader.loadFileList(inputDir);
			} else {
				MessagePrinter.println("detecting target files ...");
				final FilePathDetector pathDetector = new FilePathDetector(
						language);
				paths = pathDetector.detectFiles(new File(inputDir));
			}

			MessagePrinter.println("\t" + paths.size()
					+ " files have been detected");
			MessagePrinter.println();

			MessagePrinter.println("parsing each file ...");
			final InstantCodeFragmentDetector fragmentDetector = new InstantCodeFragmentDetector(
					new DefaultHashCalculator(), normalizerCreator,
					tokenThreshold, granularity, threadsCount, lineThreshold);
			fragmentDetector.analyzeFiles(paths);
			final Map<String, List<InstantCodeFragmentInfo>> fragments = fragmentDetector
					.getDetectedFragments();
			final Map<Long, InstantFileInfo> files = fragmentDetector
					.getDetectedFiles();
			MessagePrinter.println();

			if (pairWriter != null) {
				MessagePrinter.println("detecting clone pairs ...");
				final ClonePairDetector detector = new ClonePairDetector(
						threadsCount);
				final Collection<ClonePair> clonePairs = detector
						.detectClonePairs(fragments);
				MessagePrinter.println("\t" + clonePairs.size()
						+ " clone pairs have been detected");
				MessagePrinter.println();

				MessagePrinter.println("writing the results ...");
				pairWriter.write(clonePairs, files);
				MessagePrinter.println("\tcomplete");
			} else if (setWriter != null) {
				MessagePrinter.println("detecting clone sets ...");
				final CloneSetDetector detector = new CloneSetDetector(
						threadsCount);
				final Collection<CloneSet> cloneSets = detector
						.detectCloneSets(fragments);
				MessagePrinter.println("\t" + cloneSets.size()
						+ " clone sets have been detected");
				MessagePrinter.println();

				MessagePrinter.println("writing the results ...");
				setWriter.write(cloneSets, files);
				MessagePrinter.println("\tcomplete");
			}

			final long end = System.nanoTime();

			final long nano = end - start;
			final double mili = (double) nano / (double) 1000000;

			System.out.println("\t\ttime elapsed: " + mili + "[ms]");

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private static void parseArgs(final String[] args) throws Exception {
		final Options options = defineOptions();

		final CommandLineParser parser = new PosixParser();
		final CommandLine cmd = parser.parse(options, args);

		inputDir = cmd.getOptionValue("i");
		outputFile = cmd.getOptionValue("o");
		language = Language.getCorrespondingLanguage(cmd.getOptionValue("l"));

		String rootDir = inputDir;
		if (cmd.hasOption("r")) {
			rootDir = cmd.getOptionValue("r");
		}

		final String write = cmd.getOptionValue("w");
		if (write != null) {
			if (write.equals("pair-evaluation")) {
				pairWriter = new ClonePairForEvaluationWriter(
						new PrintWriter(new BufferedWriter(new FileWriter(
								new File(outputFile)))), rootDir);
			} else if (write.equals("pair-ccfinder")) {
				pairWriter = new CCFinderClonePairWriter(
						new PrintWriter(new BufferedWriter(new FileWriter(
								new File(outputFile)))), language);
			} else {
				// default
				setWriter = new CCFinderCloneSetWriter(
						new PrintWriter(new BufferedWriter(new FileWriter(
								new File(outputFile)))), language);
			}
		} else {
			// default
			setWriter = new CCFinderCloneSetWriter(
					new PrintWriter(new BufferedWriter(new FileWriter(
							new File(outputFile)))), language);
		}

		tokenThreshold = 0;
		lineThreshold = 0;
		if (cmd.hasOption("tt")) {
			tokenThreshold = Integer.parseInt(cmd.getOptionValue("tt"));
		} else if (cmd.hasOption("lt")) {
			lineThreshold = Integer.parseInt(cmd.getOptionValue("lt"));
		} else {
			// default
			tokenThreshold = 30;
		}

		final StringNormalizeMode cloneHashMode = (cmd.hasOption("ch")) ? StringNormalizeMode
				.getCorrespondingMode(cmd.getOptionValue("ch"))
				: StringNormalizeMode.IDENTIFIER_NORMALIZED;
		normalizerCreator = new NormalizerCreator(cloneHashMode);

		granularity = (cmd.hasOption("g")) ? AnalyzeGranularity
				.getCorrespondingGranularity(cmd.getOptionValue("g"))
				: AnalyzeGranularity.ALL;

		threadsCount = (cmd.hasOption("th")) ? Integer.parseInt(cmd
				.getOptionValue("th")) : 1;

		useFileList = cmd.hasOption("list");
	}

	private static Options defineOptions() {
		final Options options = new Options();

		{
			final Option i = new Option("i", "input", true, "input directory");
			i.setArgs(1);
			i.setRequired(true);
			options.addOption(i);
		}

		{
			final Option r = new Option("r", "root", true,
					"root of input directory");
			r.setArgs(1);
			r.setRequired(false);
			options.addOption(r);
		}

		{
			final Option o = new Option("o", "output", true, "output file");
			o.setArgs(1);
			o.setRequired(true);
			options.addOption(o);
		}

		{
			final Option w = new Option("w", "write", true,
					"how to write the results");
			w.setArgs(1);
			w.setRequired(false);
			options.addOption(w);
		}

		{
			final Option l = new Option("l", "language", true,
					"language to be analyzed");
			l.setArgs(1);
			l.setRequired(true);
			options.addOption(l);
		}

		{
			final Option tt = new Option("tt", "token-threshold", true,
					"the theshold for tokens");
			tt.setArgs(1);
			tt.setRequired(false);
			options.addOption(tt);
		}

		{
			final Option lt = new Option("lt", "line-threshold", true,
					"the theshold for lines");
			lt.setArgs(1);
			lt.setRequired(false);
			options.addOption(lt);
		}

		{
			final Option th = new Option("th", "threads", true,
					"the number of maximum threads");
			th.setArgs(1);
			th.setRequired(false);
			options.addOption(th);
		}

		{
			final Option ch = new Option("ch", "clone-hash", true,
					"how to normalize source files for clone detection");
			ch.setArgs(1);
			ch.setRequired(false);
			options.addOption(ch);
		}

		{
			final Option g = new Option("g", "granularity", true,
					"granularity of the analysis");
			g.setArgs(1);
			g.setRequired(false);
			options.addOption(g);
		}

		{
			final Option list = new Option("list", "list-files", false,
					"use a file list");
			list.setArgs(0);
			list.setRequired(false);
			options.addOption(list);
		}

		return options;
	}

}
