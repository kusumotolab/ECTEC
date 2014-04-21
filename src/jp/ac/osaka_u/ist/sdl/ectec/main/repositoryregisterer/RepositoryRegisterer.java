package jp.ac.osaka_u.ist.sdl.ectec.main.repositoryregisterer;

import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRepositoryInfo;

import org.apache.log4j.Logger;

/**
 * A class that performs the main processing of RepositoryRegisterer
 * 
 * @author k-hotta
 * 
 */
public class RepositoryRegisterer {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(RepositoryRegisterer.class.getName());

	/**
	 * the settings
	 */
	private final RepositoryRegistererMainSettings settings;

	/**
	 * the dbManager
	 */
	private final DBConnectionManager dbManager;

	public RepositoryRegisterer(
			final RepositoryRegistererMainSettings settings,
			final DBConnectionManager dbManager) {
		this.settings = settings;
		this.dbManager = dbManager;
	}

	/**
	 * run the main process of repository registerer
	 * 
	 * @throws Exception
	 */
	public void perform() throws Exception {
		logger.info("loading the given file ... ");
		final CSVReader reader = new CSVReader(settings.getFilePath());		
		final Map<Long, DBRepositoryInfo> repositories = reader.read();
		logger.info("loading " + settings.getFilePath() + " has been completed");
		logger.info(repositories.size() + " repositories have been detected");

		logger.info("registering repositories into db ... ");
		dbManager.getRepositoryRegisterer().register(repositories.values());
		logger.info("registering repositories into db has been completed");
	}

}
