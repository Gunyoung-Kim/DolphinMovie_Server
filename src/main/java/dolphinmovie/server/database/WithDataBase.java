package dolphinmovie.server.database;

import java.sql.Connection;
import java.sql.DriverManager;

import dolphinmovie.server.log.LogManager;

public abstract class WithDataBase {
	
	public static Connection getConnection() {
		try {
			String driver = "com.mysql.cj.jdbc.Driver";
			String url = "jdbc:mysql://localhost:3306/dolphinmovie?characterEncoding=UTF-8&serverTimezone=UTC";
			String user = "root";
			String pass = "tel16027!";
			Class.forName(driver);
			Connection connection = DriverManager.getConnection(url,user,pass);
			return connection;
		} catch(Exception e) {
			LogManager.errorLog(e.getLocalizedMessage());
			e.printStackTrace();
			return null;
		}
		
	}
	
	public void createTable() {};
	
}
