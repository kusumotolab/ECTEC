package jp.ac.osaka_u.ist.sdl.ectec.db;

import java.sql.Connection;

public interface IDBConfig {

	/**
	 * initialize db connection and return it
	 * 
	 * @return
	 */
	public Connection init() throws Exception;

}
