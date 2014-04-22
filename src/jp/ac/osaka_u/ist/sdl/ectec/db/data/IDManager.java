package jp.ac.osaka_u.ist.sdl.ectec.db.data;

public class IDManager {

	public static long issuanceMinimumId(final short header) {
		final short fixedHeader = (short) ((header >= 0) ? header : -header);

		return ((long) fixedHeader) << 48;
	}

}
