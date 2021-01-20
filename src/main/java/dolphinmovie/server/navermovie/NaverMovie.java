package dolphinmovie.server.navermovie;

import dolphinmovie.server.kobis.Kobis;
import dolphinmovie.server.log.LogManager;
import dolphinmovie.server.movieInfo.*;
import dolphinmovie.server.util.DateManager;

import java.net.*;
import java.util.*;
import java.io.*;

import org.json.simple.*;
import org.json.simple.parser.*;



public class NaverMovie {
	
	private volatile static NaverMovie naverMovie;
	
	private Kobis kobis;
	LinkedList<JSONObject> daily;
	LinkedList<JSONObject> weekly;
	LinkedList<MovieDAO> dailyDAO;
	LinkedList<MovieDAO> weeklyDAO;
	String[] dailyMoviesName;
	String[] weeklyMoviesName;
	
	
	private NaverMovie() {
		this.kobis = Kobis.getInstance();
	}
	
	public static NaverMovie getInstance() {
		if(naverMovie == null) {
			synchronized(NaverMovie.class) {
				if(naverMovie == null)
					naverMovie = new NaverMovie();
			}
		}
		return naverMovie;
	}
	
	
	private void getMovieInfo() {
		if(kobis.getNewBoxOffice(DateManager.getNowDateToString())) {
			daily = new LinkedList<>();
			weekly = new LinkedList<>();
			dailyDAO = new LinkedList<>();
			weeklyDAO = new LinkedList<>();
			
			dailyMoviesName = kobis.getDailyBoxofficeMovieName();
			weeklyMoviesName = kobis.getWeeklyBoxofficeMovieName();
			
			int[] dailyRankIntens = kobis.getDailyBoxofficeMovieRankInten();
			int[] weeklyRankIntens = kobis.getWeeklyBoxifficeMovieRankInten();
			
			int[] dailyOpenYear = kobis.getDailyBoxofficeMovieOpenDateYear();
			int[] weeklyOpenYear = kobis.getWeeklyBoxofficeMovieOpenDateYear();
			
			boolean[] dailyOldAndNew = kobis.getDailyBoxofficeMovieRankOldAndNew();
			boolean[] weeklyOldAndNew = kobis.getWeeklyBoxofficeMovieRankOldAndNew();
			
			int i=0;
			for(String name: dailyMoviesName) {
				JSONObject o = getSingleMovieInfo(name,dailyOpenYear[i]);
				daily.add(o);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					LogManager.errorLog(e.getLocalizedMessage());
					e.printStackTrace();
				}
				
				JSONArray arr = (JSONArray) o.get("items");
				if(arr.size() >0) {
					JSONObject current_movie = (JSONObject) arr.get(0);
					String title = dailyMoviesName[i];
					String link = (String)current_movie.get("link");
					String thumbnailLink = (String)current_movie.get("image");
					int rankInten = dailyRankIntens[i];
					boolean rankOldAndNew = dailyOldAndNew[i++];
					MovieDAO c_movie = new MovieDAO(title,rankInten,rankOldAndNew,thumbnailLink,link);
					dailyDAO.add(c_movie);
				}
				
			}
			
			i=0;
			for(String name: weeklyMoviesName) {
				JSONObject o = getSingleMovieInfo(name,weeklyOpenYear[i]);
				weekly.add(o);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				JSONArray arr = (JSONArray) o.get("items");
				if(arr.size() >0) {
					JSONObject current_movie = (JSONObject) arr.get(0);
					String title = weeklyMoviesName[i];
					String link = (String)current_movie.get("link");
					int rankInten = weeklyRankIntens[i];
					String thumbnailLink = (String)current_movie.get("image");
					boolean rankOldAndNew = weeklyOldAndNew[i++];
					MovieDAO c_movie = new MovieDAO(title,rankInten,rankOldAndNew,thumbnailLink,link);
					weeklyDAO.add(c_movie);
				}
				
			}
		}
	}
	
	private JSONObject getSingleMovieInfo(String movieName,int openYear) {
		JSONObject result = null;
		try {
			String urlString = "https://openapi.naver.com/v1/search/movie.json?query=";
			urlString += URLEncoder.encode(movieName, "UTF-8");
			//urlString += "&display=1&yearfrom=" + (openYear-1) + "&yearto=" + openYear;
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.addRequestProperty("X-Naver-Client-Id", "7byI1OLh_QSu1PIBHm9c");
			connection.addRequestProperty("X-Naver-Client-Secret", "oC4QdFZ9Oe");
			JSONParser parser = new JSONParser();
			result = (JSONObject) parser.parse(new InputStreamReader(connection.getInputStream(),"UTF-8"));
			if(connection.getResponseCode() != 200) {
				LogManager.errorLog(movieName+"Naver API Accident");
				System.err.println(movieName+"Naver API Accident");
			}
		} catch(MalformedURLException e) {
			LogManager.errorLog(e.getLocalizedMessage());
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			LogManager.errorLog(e.getLocalizedMessage());
			e.printStackTrace();
		} catch (IOException e) {
			LogManager.errorLog(e.getLocalizedMessage());
			e.printStackTrace();
		} catch (ParseException e) {
			LogManager.errorLog(e.getLocalizedMessage());
			e.printStackTrace();
		} 
		
		return result;
	}
	
	public String[] getDailyMoviesName() {
		getMovieInfo();
		return this.dailyMoviesName;
	}
	
	public String[] getWeeklyMoviesName() {
		getMovieInfo();
		return this.weeklyMoviesName;
	}
	
	public LinkedList<JSONObject> getDaily() {
		getMovieInfo();
		return daily;
	}
	
	public LinkedList<JSONObject> getWeekly() {
		getMovieInfo();
		return weekly;
	}
	
	public LinkedList<MovieDAO> getDailyDAO() {
		getMovieInfo();
		return this.dailyDAO;
	}
	
	public LinkedList<MovieDAO> getWeeklyDAO() {
		getMovieInfo();
		return this.weeklyDAO;
	}
	
}
