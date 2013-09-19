package jp.ac.osaka_u.ist.sdl.ectec.util;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class StringUtilsTest {

	@Test
	public void test() {
		final List<Integer> list = new ArrayList<Integer>();
		list.add(1);
		list.add(2);
		list.add(3);
		final String result = StringUtils.convertListToString(list, ",");

		assertTrue(result.equals("1,2,3"));
	}

}
