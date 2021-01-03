package dolphinmovie.server.theater;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;

import dolphinmovie.server.database.WithDataBase;
import dolphinmovie.server.log.LogManager;

public class TheaterManager extends WithDataBase {
	
	private static volatile TheaterManager instance;
	private static final String TABLE_NAME = "theater";
	
	private TheaterManager() {
		loadAllTheaters();
	}
	
	public static TheaterManager getInstance() {
		if(instance == null) {
			synchronized(TheaterManager.class) {
				if(instance == null) {
					instance = new TheaterManager();
				}
			}
		}
		return instance;
	}
	
	private LinkedList<TheaterDAO> theaterList;
	
	//일단은 기존의 테이블 삭제하고 아예 새롭게 만드는 방식 추후에 더 효율적인 방식 모색하자 s
	public void updateTheaterTable() {
		deleteTable();
		createTable();
		TheaterUpdater u = TheaterUpdater.getInstance();
		theaterList = u.updateTheater(); 
		addTheaters(theaterList);
		LogManager.runningLog("Theater Updated");
		System.out.println("Theater Updated");
	}
	
	public LinkedList<TheaterDAO> getTheaterList() {
		return theaterList;
	}
	
	//
	//
	//
	
	private void loadAllTheaters() {
		try {
			Connection connection = getConnection();
			PreparedStatement loadAllTheaters = connection.prepareStatement("SELECT * FROM " +TABLE_NAME);
			ResultSet resultSet = loadAllTheaters.executeQuery();
			LinkedList<TheaterDAO> list = new LinkedList<>();
			
			while(resultSet.next()) {
				String name = resultSet.getString("name");
				String address = resultSet.getString("address");
				String lot_number = resultSet.getNString("lot_number");
				double xpos = resultSet.getDouble("xpos");
				double ypos = resultSet.getDouble("ypos");
				boolean open = resultSet.getInt("open") == 1 ? true : false;
				String link = resultSet.getString("link");
				
				list.add(new TheaterDAO(name,address,lot_number,xpos,ypos,open,link));
			}
			theaterList = list;
			System.out.println("Theater Load Success!");
		} catch(Exception e) {
			LogManager.errorLog(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	private void deleteTable() {
		try {
			Connection connection = getConnection();
			PreparedStatement deleteTable = connection.prepareStatement("DROP TABLE " + TABLE_NAME);
			deleteTable.executeUpdate();
		} catch(Exception e) {
			LogManager.errorLog(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	private static void addTheaters(LinkedList<TheaterDAO> list) {
		StringBuilder sb = new StringBuilder("INSERT INTO "+ TABLE_NAME +
				"(name,address,lot_number,xpos,ypos,open,link)" +
				"VALUES");
		for(TheaterDAO theater: list) {
			sb.append("('"+theater.getName()+"','"+theater.getAddress()+"','"+theater.getLot_number()+"','"+theater.getXpos()+"','"+theater.getYpos()+"','"+theater.getOpenInt()+"','"+"."+"'),");
		}
		
		sb.deleteCharAt(sb.length()-1);
		try {
			Connection connection = getConnection();
			PreparedStatement addTheaters = connection.prepareStatement(sb.toString());
			addTheaters.executeUpdate();
		} catch(Exception e) {
			LogManager.errorLog(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	private static void addTheater(TheaterDAO theater) {
		try {
			Connection connection = getConnection();
			PreparedStatement addTheater = connection.prepareStatement(
					"INSERT INTO " +TABLE_NAME +
					"(name,address,lot_number,xpos,ypos,open,link)" +
					"VALUE" +
					"('"+theater.getName()+"','"+theater.getAddress()+"','"+theater.getLot_number()+"','"+theater.getXpos()+"','"+theater.getYpos()+"','"+theater.getOpenInt()+"','"+"."+"')");
			addTheater.executeUpdate();
			System.out.println(theater.getName() + " Registered");
		} catch(Exception e) {
			LogManager.errorLog(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	@Override 
	public void createTable() {
		try {
			Connection connection = getConnection();
			PreparedStatement createTable = connection.prepareStatement(
					"CREATE TABLE IF NOT EXISTS " +
					TABLE_NAME +"(theater_code INT NOT NULL auto_increment," +
					"name varChar(255) NOT NULL," +
					"address varChar(255)," +
					"lot_number varChar(255)," +
					"xpos DOUBLE," +
					"ypos DOUBLE," +
					"open BOOL," +
					"link varChar(255)," +
					"PRIMARY KEY(theater_code))");
			createTable.execute();
		} catch(Exception ex) {
			LogManager.errorLog(ex.getLocalizedMessage());
			ex.printStackTrace();
		}
	}
}
