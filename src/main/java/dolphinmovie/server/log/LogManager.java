package dolphinmovie.server.log;

import java.io.IOException;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import dolphinmovie.server.util.CommandLineExecutor;

public class LogManager {
	private static final Logger errorLogger = Logger.getLogger("error");
	private static final Logger connectionLogger = Logger.getLogger("connection");
	private static final Logger httpLogger = Logger.getLogger("http");
	private static final Logger runningLogger = Logger.getLogger("running");
	
	private static final int ERROR_LOG_FILE_LIMIT = 10000000;
	private static final int CONNECTION_LOG_FILE_LIMIT = 10000000;
	private static final int HTTP_LOG_FILE_LIMIT = 10000000;
	private static final int RUNNING_LOG_FILE_LIMIT = 10000000;
	
	private static final int ERROR_LOG_FILE_COUNT = 5;
	private static final int CONNECTION_LOG_FILE_COUNT = 5;
	private static final int HTTP_LOG_FILE_COUNT = 5;
	private static final int RUNNING_LOG_FILE_COUNT = 5;
	
	private static final String COMMON_FILE_PATH = "%h/eclipse-workspace/dolphinmovie.server/src/main/resources/log/";
	
	static {
		try {
			FileHandler errorHandler = new FileHandler(COMMON_FILE_PATH + "error/dolphin_movie_error%g.log",ERROR_LOG_FILE_LIMIT,ERROR_LOG_FILE_COUNT,true);
			FileHandler connectionHandler = new FileHandler(COMMON_FILE_PATH+ "connection/dolphin_movie_connection%g.log",CONNECTION_LOG_FILE_LIMIT,CONNECTION_LOG_FILE_COUNT,true);
			FileHandler httpHandler = new FileHandler(COMMON_FILE_PATH + "http/dolphin_movie_http%g.log",HTTP_LOG_FILE_LIMIT, HTTP_LOG_FILE_COUNT,true);
			FileHandler runningHandler = new FileHandler(COMMON_FILE_PATH + "running/dolphin_movie_runnning%g.log",RUNNING_LOG_FILE_LIMIT, RUNNING_LOG_FILE_COUNT,true);
			
			errorLogger.addHandler(errorHandler);
			connectionLogger.addHandler(connectionHandler);
			httpLogger.addHandler(httpHandler);
			runningLogger.addHandler(runningHandler);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void errorLog(String msg) {
		errorLogger.log(Level.SEVERE, msg);
	}
	
	public static void connectionLog(String msg) {
		connectionLogger.log(Level.INFO, msg);
	}
	
	public static void httpLog(String msg) {
		httpLogger.log(Level.INFO, msg);
	}
	
	public static void runningLog(String msg) {
		runningLogger.log(Level.INFO, msg);
	}
	
	public static void httpLog(Map<String, String> keyAndValues) {
		StringBuilder sb = new StringBuilder();
		
		keyAndValues.forEach((a,b) -> {
			sb.append(a+":"+b+"\n");
		});
		
		httpLogger.log(Level.INFO, sb.toString());
	}
	
	//나중에 리팩토링 필수!
	public static void deleteLogFiles() {
		String[] commands = {"cd src/main/resources/log/http\nrm *\ncd ../error\nrm *\ncd ../connection\nrm *\ncd ../running\nrm *"};
		CommandLineExecutor executor = new CommandLineExecutor(commands);
		Thread t = new Thread(executor);
		t.start();
	}
}
