package dolphinmovie.server.user;

public class UserDAO {
	private String id;
	private String name;
	
	public UserDAO(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	
}
