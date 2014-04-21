package jp.ac.osaka_u.ist.sdl.instantcdt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class FileListLoader {

	public List<String> loadFileList(final String fileListPath)
			throws Exception {
		final List<String> result = new ArrayList<String>();
		
		final BufferedReader br = new BufferedReader(new FileReader(new File(
				fileListPath)));
		String line;
		while ((line = br.readLine()) != null) {
			result.add(line);
		}

		br.close();
		
		return result;
	}

}
