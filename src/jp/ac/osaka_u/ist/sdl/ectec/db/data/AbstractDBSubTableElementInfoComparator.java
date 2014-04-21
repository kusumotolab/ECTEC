package jp.ac.osaka_u.ist.sdl.ectec.db.data;

import java.util.Comparator;

public class AbstractDBSubTableElementInfoComparator<T extends AbstractDBSubTableElementInfo>
		implements Comparator<T> {

	@Override
	public int compare(T o1, T o2) {
		final int mainCompareResult = ((Long) o1.getMainElementId())
				.compareTo((Long) o2.getMainElementId());

		if (mainCompareResult != 0) {
			return mainCompareResult;
		}

		final int subCompareResult = ((Long) o1.getSubElementId())
				.compareTo((Long) o2.getSubElementId());
		return subCompareResult;
	}

}
