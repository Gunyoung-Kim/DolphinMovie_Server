package dolphinmovie.server.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import dolphinmovie.server.database.WithDataBase;
import dolphinmovie.server.log.LogManager;


public class MemberManager extends WithDataBase {
	
	public interface LoginResult {
		public static final int SUCCESS = 1;
		public static final int NO_ACCOUNT = -1;
		public static final int INCORRECTPW = 0;
	}
	
	private final static String TABLE_NAME = "member";
	private static String[] memberField = {"id","password","name"};
	
	private volatile static MemberManager instance;
	
	private MemberManager() {};

	public static MemberManager getInstance() {
		if(instance == null) {
			synchronized(MemberManager.class) {
				if(instance == null)
					instance = new MemberManager();
			}
		}
		return instance;
	}
	
	public MemberDAO login(String id, String password) {
		try {
			Connection connection = getConnection();
			PreparedStatement login = connection.prepareStatement("SELECT id, password, name FROM " + TABLE_NAME + " WHERE id = " +"\"" +id+"\"");
			ResultSet result = login.executeQuery();
			MemberDAO member;
			if(result.next()) {
				String pw = result.getString("password");
				String name = result.getString("name");
				if(password.equals(pw)) {
					member = new MemberDAO(id,password,name);
					return member;
				} else {
					member = new MemberDAO("INCORRECTPW","","");
					return member;
				}
			} else {
				member = new MemberDAO("NO_ACCOUNT","","");
				return member;
			}
		} catch(Exception ex) {
			LogManager.errorLog(ex.getLocalizedMessage());
			ex.printStackTrace();
			return null;
		}
	}
	
	public boolean isIDExist(String id) {
		try {
			Connection connection = getConnection();
			PreparedStatement isIDExist = connection.prepareStatement("SELECT id FROM " +TABLE_NAME + " WHERE id = " +"\"" +id+"\"");
			ResultSet result = isIDExist.executeQuery();
			return result.next();
		} catch(Exception e) {
			LogManager.errorLog(e.getLocalizedMessage());
			e.printStackTrace();
			return true;
		}
	}
	
	public int addMember(String id,String password,String name) {
		try {
			Connection connection = getConnection();
			PreparedStatement addMember = connection.prepareStatement(
					"INSERT INTO "+TABLE_NAME +
					"(id,password,name)" +
					"VALUE" +
					"('"+id+"','"+password+"','"+name+"')");
			addMember.executeUpdate();
			System.out.println("Welcome !" + id);
			return 0;
		} catch(Exception e) {
			LogManager.errorLog(e.getLocalizedMessage());
			e.printStackTrace();
			return 507;
		}
	}
	
	public void createTable() {
		try {
			Connection connection = getConnection();
			PreparedStatement createTable = connection.prepareStatement(
					"CREATE TABLE IF NOT EXISTS " +
					TABLE_NAME + "(id varChar(255) NOT NULL," +
					"password varChar(255) NOT NULL," +
					"name varChar(255) NOT NULL," +
					"PRIMARY KEY(id))");
			createTable.execute();
			System.out.println("Table Created");
		} catch(Exception ex) {
			LogManager.errorLog(ex.getLocalizedMessage());
			ex.printStackTrace();
		}
	}
}
