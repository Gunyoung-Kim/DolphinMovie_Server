package dolphinmovie.server.kobis;

import java.net.*;
import java.io.*;
import java.util.*;

import org.json.simple.*;
import org.json.simple.parser.*;

import dolphinmovie.server.log.LogManager;
import dolphinmovie.server.util.DateManager;

public class Kobis {
	
	private volatile static Kobis instance;
	
	private static final String KOBIS_KEY = "342f60885e5e3583a7c9be41e3ff2cb7";
	
	private JSONArray dailyBoxoffice;
	private JSONArray weeklyBoxoffice;
	private String lastUpdatedDay;
	
	private Kobis() {}
	
	public static Kobis getInstance() {
		if(instance == null) {
			synchronized(Kobis.class) {
				if(instance == null)
					instance = new Kobis();
			}
		}
		return instance;
	}
	
	private void updateBoxOffice(String key, String targetDt,boolean isDaily) {
		StringBuilder urlString = new StringBuilder("http://kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/search");
		if(isDaily) {		
			urlString.append("DailyBoxOfficeList.json?");
		} else {
			urlString.append("WeeklyBoxOfficeList.json?");
		}
		urlString.append("key=" + key);
		
		if(isDaily)
			urlString.append("&targetDt=" +targetDt);
		else 
			urlString.append("&targetDt=" + DateManager.getSevenAgoString());
		
		try {
			URL url = new URL(urlString.toString());
			URLConnection connection = url.openConnection();
			InputStream in = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String resultString = br.readLine();
			
			JSONParser parser = new JSONParser();
			JSONObject parseResult = (JSONObject) parser.parse(resultString); // 이부분 인자로 그냥br 넣어서 한번 해보
			JSONObject jsonResult = (JSONObject) parseResult.get("boxOfficeResult");
			if(jsonResult == null) {
				throw new MalformedURLException();
			}
			JSONArray list;
			
			if(isDaily) {
				list = (JSONArray) jsonResult.get("dailyBoxOfficeList");
				dailyBoxoffice = list;
			} else {
				list = (JSONArray) jsonResult.get("weeklyBoxOfficeList");
				weeklyBoxoffice = list;
			}
			
		} catch(MalformedURLException mex) {
			LogManager.errorLog(mex.getLocalizedMessage());
			mex.printStackTrace();
		} catch(IOException iex) {
			LogManager.errorLog(iex.getLocalizedMessage());
			iex.printStackTrace();
		} catch (ParseException pex) {
			LogManager.errorLog(pex.getLocalizedMessage());
			pex.printStackTrace();
		}
	}
	
	// kobis class 에서 필요한 정보 리턴하는 함수들 외부 클래스에서 사용전에 본 클래스의 getNewBoxoffice 함수 호출 필수 
	
	public String[] getDailyBoxofficeMovieName() {
		String[] arr = new String[dailyBoxoffice.size()];
		int i=0;
		for(Object o: dailyBoxoffice) {
			JSONObject jo = (JSONObject) o;
			String name = (String) jo.get("movieNm");
			arr[i++] = name;
		}
		return arr;
	}
	
	public String[] getWeeklyBoxofficeMovieName() {
		String[] arr = new String[weeklyBoxoffice.size()];
		int i=0;
		for(Object o: weeklyBoxoffice) {
			JSONObject jo = (JSONObject) o;
			String name = (String) jo.get("movieNm");
			arr[i++] = name;
		}
		return arr;
	}
	
	public int[] getDailyBoxofficeMovieRankInten() {
		int[] arr = new int[dailyBoxoffice.size()];
		int i=0;
		for(Object o: dailyBoxoffice) {
			JSONObject jo = (JSONObject) o;
			int rankInten = Integer.parseInt((String) jo.get("rankInten"));
			arr[i++] = rankInten;
		}
		return arr;
	}
	
	public int[] getWeeklyBoxifficeMovieRankInten() {
		int[] arr = new int[weeklyBoxoffice.size()];
		int i=0;
		for(Object o: weeklyBoxoffice) {
			JSONObject jo = (JSONObject) o;
			int rankInten = Integer.parseInt((String) jo.get("rankInten"));
			arr[i++] = rankInten;
		}
		return arr;
	}
	
	public boolean[] getDailyBoxofficeMovieRankOldAndNew() {
		boolean[] arr = new boolean[dailyBoxoffice.size()];
		int i=0;
		for(Object o: dailyBoxoffice) {
			JSONObject jo = (JSONObject) o;
			String str = (String) jo.get("rankOldAndNew");
			if(str.equals("OLD")) {
				arr[i++] = false;
			} else {
				arr[i++] = true;
			}
		}
		
		return arr;
	}
	
	public boolean[] getWeeklyBoxofficeMovieRankOldAndNew() {
		boolean[] arr = new boolean[weeklyBoxoffice.size()];
		int i=0;
		for(Object o: weeklyBoxoffice) {
			JSONObject jo = (JSONObject) o;
			String str = (String) jo.get("rankOldAndNew");
			if(str.equals("OLD")) {
				arr[i++] = false;
			} else {
				arr[i++] = true;
			}
		}
		
		return arr;
	}
	
	public int[] getDailyBoxofficeMovieOpenDateYear() {
		int[] arr = new int[dailyBoxoffice.size()];
		int i = 0;
		for(Object o: dailyBoxoffice) {
			JSONObject jo = (JSONObject) o;
			String str = (String) jo.get("openDt");
			arr[i++] = Integer.parseInt(str.substring(0, 4));
		}
		
		return arr;
	}
	
	public int[] getWeeklyBoxofficeMovieOpenDateYear() {
		int[] arr = new int[weeklyBoxoffice.size()];
		int i = 0;
		for(Object o: weeklyBoxoffice) {
			JSONObject jo = (JSONObject) o;
			String str = (String) jo.get("openDt");
			arr[i++] = Integer.parseInt(str.substring(0, 4));
		}
		
		return arr;
	}
	
	// updated -> return true , nonupdated -> return false
	public boolean getNewBoxOffice(String targetDt) {
		if(dailyBoxoffice == null || weeklyBoxoffice == null || lastUpdatedDay == null || DateManager.isDayChanged(targetDt, this.lastUpdatedDay)) {
			updateBoxOffice(KOBIS_KEY,targetDt,true);
			updateBoxOffice(KOBIS_KEY,targetDt,false);
			this.lastUpdatedDay = targetDt;
			return true;
		} else {
			return false;
		}
	}

}
