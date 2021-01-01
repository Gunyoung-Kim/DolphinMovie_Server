package dolphinmovie.server.user;

import java.util.HashMap;

import dolphinmovie.server.log.LogManager;
import dolphinmovie.server.member.MemberDAO;

public class UserManager {
	
	private static UserManager instance;
	
	private HashMap<String,UserDAO> concurrent_users;
	
	private UserManager() {
		this.concurrent_users = new HashMap<>();
	}
	
	public static UserManager getInstance() {
		if(instance == null) {
			synchronized(UserManager.class) {
				if(instance== null) 
					instance = new UserManager();
			}
		}
		
		return instance;
	}
	
	public void login(MemberDAO member) {
		String id = member.getId();
		UserDAO user = new UserDAO(id,member.getName());
		LogManager.connectionLog(id +" has login");
		this.concurrent_users.put(id, user);
	}
	
	public UserDAO logout(String id) {
		UserDAO result = this.concurrent_users.remove(id);
		if(result != null) {
			LogManager.connectionLog(id +" has logout");
		}
		return result;
	}
	
	public UserDAO findUser(String id) {
		UserDAO result = this.concurrent_users.get("id");
		return result;
	}
	
	public int getConcurrentUsersNum() {
		return this.concurrent_users.size();
	}
	
}
