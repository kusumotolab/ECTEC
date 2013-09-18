package jp.ac.osaka_u.ist.sdl.ectec.db;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class DBMakerTest {

	private static DBMaker maker;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DBConnectionManager.createInstance("test-resources" + File.separator
				+ "db" + File.separator + "test.db");
		maker = new DBMaker();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DBConnectionManager.getInstance().close();
	}

	@Test
	public void test() {
		try {
			maker.makeDb(true);
			assertTrue(true);
		} catch (Exception e) {
			fail();
		}
	}

}
