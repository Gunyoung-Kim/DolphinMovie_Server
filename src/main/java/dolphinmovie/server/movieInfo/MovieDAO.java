package dolphinmovie.server.movieInfo;

import org.json.simple.JSONObject;

public class MovieDAO {
	public String movieName;
	int rankInten;
	boolean rankOldAndNew;
	String thumbnailLink;
	String infoLink;
	
	public MovieDAO(String movieName, int rankInten, boolean rankOldAndNew,String thumbnailLink, String infoLink) {
		this.movieName = movieName;
		this.rankInten = rankInten;
		this.rankOldAndNew = rankOldAndNew;
		this.thumbnailLink = thumbnailLink;
		this.infoLink = infoLink;
	}
	
	public JSONObject toJSONObject() {
		JSONObject object = new JSONObject();
		
		object.put("title", this.movieName);
		object.put("rankInten", this.rankInten);
		object.put("rankOldAndNew", this.rankOldAndNew);
		object.put("thumbnailLink", thumbnailLink);
		object.put("link", this.infoLink);
		
		return object;
	}
}
