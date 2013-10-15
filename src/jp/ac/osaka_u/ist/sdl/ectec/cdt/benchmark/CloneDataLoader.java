package jp.ac.osaka_u.ist.sdl.ectec.cdt.benchmark;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class CloneDataLoader<T extends BenchmarkClonePair> {

	public List<T> load(final String file) throws Exception {
		final List<T> result = new ArrayList<T>();

		final BufferedReader br = new BufferedReader(new FileReader(new File(
				file)));

		String line;
		while ((line = br.readLine()) != null) {
			final String[] split = line.split("\t");

			final int id = Integer.parseInt(split[0]);

			final String path1 = split[2];
			final int start1 = Integer.parseInt(split[3]);
			final int end1 = Integer.parseInt(split[4]);
			final BenchmarkCloneFragment fragment1 = new BenchmarkCloneFragment(path1, start1,
					end1);

			final String path2 = split[5];
			final int start2 = Integer.parseInt(split[6]);
			final int end2 = Integer.parseInt(split[7]);
			final BenchmarkCloneFragment fragment2 = new BenchmarkCloneFragment(path2, start2,
					end2);

			final T pair = createInstance(id, fragment1, fragment2);
			result.add(pair);
		}

		br.close();

		return Collections.unmodifiableList(result);
	}

	protected abstract T createInstance(final int id,
			final BenchmarkCloneFragment fragment1, final BenchmarkCloneFragment fragment2);
}
