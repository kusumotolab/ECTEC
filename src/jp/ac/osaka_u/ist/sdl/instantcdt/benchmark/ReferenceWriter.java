package jp.ac.osaka_u.ist.sdl.instantcdt.benchmark;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

public class ReferenceWriter {

	public static void main(String[] args) {
		final String allRefFile = args[0];
		final String outputFile = args[1];
		final String project = args[2];

		try {
			final BufferedReader reader = new BufferedReader(new FileReader(
					new File(allRefFile)));
			final PrintWriter pw = new PrintWriter(new BufferedWriter(
					new FileWriter(new File(outputFile))));

			String line;

			while ((line = reader.readLine()) != null) {
				final String[] split = line.split("\t");
				if (split[2].startsWith(project + "/")) {
					for (int i = 2; i < split.length - 1; i++) {
						pw.print(split[i] + "\t");
					}
					pw.println(split[split.length - 1]);
				}
			}

			reader.close();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
