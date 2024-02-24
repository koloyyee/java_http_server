package co.loyyee;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class Log {
	final private static String LogFilePath = "src/main/resources/log/log.txt";
	/*
	* log uses LinkedList because there is no resizing,
	* unlike Array/ArrayList.
	* */
	final private static List<String> logs = new LinkedList<String>();
	final private static SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	final private static Logger logger = Logger.getLogger("co.loyyee.http_server");

	public static void info(String msg) {
		logger.info(msg);
	}

	/**
	 * @param log the message coming from system;
	 * @param print to log out the message
	 *
	 *                Note: for date we need to use new Date() for SimpleDateFormat
	 * */
	public static void write(String log, boolean print) {
		String msg = fmt.format(new Date()) + " " + log;
		logs.add(msg);
		if(print) {
			logger.info(msg);
		}
	}
	/**
	 * @param appendFileWriter wil
	 * */
	public static void save(boolean append){
		File logFile = new File(LogFilePath);
		try {
			logFile.createNewFile();
			try (BufferedWriter bufW = new BufferedWriter(new FileWriter(logFile, append));) {
				for(String line : logs) {
					bufW.write(line + "\n");
				}
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		} catch (IOException e) {
			logger.warning(e.getMessage());
		}

	}

}
