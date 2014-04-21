package jp.ac.osaka_u.ist.sdl.instantcdt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;

public class FilePathDetector {

	private final Language language;

	public FilePathDetector(final Language language) {
		this.language = language;
	}

	public List<String> detectFiles(final File target) {
		final List<String> result = new ArrayList<String>();
		
		if (target.isFile()) {
			if (language.isTarget(target.getName())) {
				result.add(target.getAbsolutePath());
			}
		} else if (target.isDirectory()) {
			File[] files = target.listFiles();
			for (final File file : files) {
				result.addAll(detectFiles(file));
			}
		}
		
		return result;
	}

}
