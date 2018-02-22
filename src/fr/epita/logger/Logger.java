package fr.epita.logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.epita.iam.services.configuration.ConfigurationService;

/**
 * <h3>Description</h3>
 * <p>
 * This logger class manages all logging events
 * </p>
 *
 * <h3>Usage</h3>
 * <p>
 * 
 * <pre>
 * <code>Logger logger = new Logger();</code>
 * </pre>
 * </p>
 * 
 * @author Samer Masaad
 *
 */
public class Logger {

	/**
	 * 
	 */
	private static final String OS_NAME = "os.name";
	/**
	 * 
	 */
	private static final String USER_HOME = "user.home";
	private static final String LOGGER_PATH = "\\.iam-core\\application.log";
	private static PrintWriter pw;
	private final Class<?> cls;

	private static final String ERROR = "ERROR";
	private static final String INFO = "INFO";
	private static final String WARNING = "WARNING";

	static {
		try {
			pw = new PrintWriter(new FileOutputStream(getLoggerFile(), true));
		} catch (final Exception e) {
			System.out.println("Failed to initialise the PrintWriter to use the logger.");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param cls
	 *            - the class that is using the logger
	 */
	public Logger(Class<?> cls) {
		this.cls = cls;

	}

	/**
	 * 
	 * @param message
	 *            - the error to log
	 */
	public void error(String message) {
		printMessage(message, ERROR);
	}

	/**
	 * 
	 * @param message
	 *            - the error to log
	 * @param e
	 *            - the exception occured
	 */
	public void error(String message, Exception e) {
		printMessage(message, ERROR);
		e.printStackTrace(pw);
		pw.flush();
	}

	/**
	 * 
	 * @param message
	 *            - the information to log
	 */
	public void info(String message) {
		printMessage(message, INFO);
	}

	/**
	 * 
	 * @param message
	 *            - the warning to log
	 */
	public void warning(String message) {
		printMessage(message, WARNING);
	}

	private void printMessage(String message, String level) {
		final String completeMessage = getTimeStamp() + " - " + level + " - " + cls.getCanonicalName() + ": " + message;
		pw.println(completeMessage);
		pw.flush();
	}

	private static String getTimeStamp() {
		final Date date = new Date();

		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
		return sdf.format(date);
	}

	private static File getLoggerFile() {

		ConfigurationService configService = ConfigurationService.getInstance();
		String path = configService.getConfigurationValue("logger.path");

		// if path is not provided, the logger is created in the home directory
		// (iam-core\application.log)
		if (path.isEmpty()) {
			String os = System.getProperty(OS_NAME).toLowerCase();

			if (os.indexOf("win") >= 0) {
				path = System.getProperty(USER_HOME) + LOGGER_PATH;
			} else if (os.indexOf("mac") >= 0) {
				path = System.getProperty(USER_HOME) + File.separator + "Documents" + LOGGER_PATH;
			} else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0) {
				path = System.getProperty(USER_HOME) + LOGGER_PATH;
			}
		}

		File file = new File(path);

		if (!file.exists()) {
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				System.out.println("Error creating the logger file in " + path);
			}
		}

		return file;
	}

}
