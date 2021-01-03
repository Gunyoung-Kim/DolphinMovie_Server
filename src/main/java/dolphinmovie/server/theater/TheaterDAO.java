package dolphinmovie.server.theater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import dolphinmovie.server.log.LogManager;
import dolphinmovie.server.util.NoHttpResponseException;

public class TheaterDAO {
	
	private static final String HEADER_ONE = "X-NCP-APIGW-API-KEY-ID";
	private static final String HEADER_TWO = "X-NCP-APIGW-API-KEY";
	private static final String ID = "vxy4y1l4oj";
	private static final String SECRET = "dacPpomCXNve0kdFrNJdkjHjTCA00zUzuiQP1K2I";
	
	private String name;
	private String address;
	private String lot_number;
	private double xpos;
	private double ypos;
	private boolean open = true;
	private String link = ".";
	
	public TheaterDAO(String name, String address) {
		int isOpen = isOpened(name);
		if( isOpen == -1) 
			this.name = name;
		else {
			this.name = name.substring(0, isOpen);
			this.open = false;
		}
		int lot_number_index = address.indexOf("(");
		int lot_number_index_2 = address.indexOf(")");
		if(lot_number_index > -1)  {
			this.address = address.substring(0, lot_number_index);
			this.lot_number = address.substring(lot_number_index_2+1);
		} else {
			this.address = address;
			this.lot_number = null;
		}
		
		Pos p = null;
		try {
			p = getPosition(address);
		} catch(NoHttpResponseException e) {
			p = new Pos(-1,-1);
		}
		if(p != null) {
			this.xpos = p.xpos;
			this.ypos = p.ypos;
		}
	}
	
	
	
	public TheaterDAO(String name, String address, String lot_number, double xpos, double ypos, boolean open,
			String link) {
		this.name = name;
		this.address = address;
		this.lot_number = lot_number;
		this.xpos = xpos;
		this.ypos = ypos;
		this.open = open;
		this.link = link;
	}



	private Pos getPosition(String address) throws NoHttpResponseException{
		Pos pos = null;
		try {
			String urlString = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=";
			urlString += URLEncoder.encode(address, "UTF-8");
			URL url = new URL(urlString);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.addRequestProperty(HEADER_ONE, ID);
			con.addRequestProperty(HEADER_TWO, SECRET);
			JSONParser parser = new JSONParser();
			JSONObject result = (JSONObject) parser.parse(new BufferedReader(new InputStreamReader(con.getInputStream())));
			JSONArray addresses = (JSONArray) result.get("addresses");
			if(addresses.size() == 0) {
				LogManager.errorLog(address + "position not found\n");
				throw new NoHttpResponseException();
			}
			double xpos = Double.parseDouble((String)(((JSONObject)addresses.get(0)).get("x")));
			double ypos = Double.parseDouble((String)(((JSONObject)addresses.get(0)).get("y")));
			pos = new Pos(xpos,ypos);
			return pos;
		} catch(MalformedURLException e) {
			LogManager.errorLog(e.getLocalizedMessage());
			e.printStackTrace();
			return null;
		} catch(UnsupportedEncodingException e) {
			LogManager.errorLog(e.getLocalizedMessage());
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			LogManager.errorLog(e.getLocalizedMessage());
			e.printStackTrace();
			return null;
		} catch (ParseException e) {
			LogManager.errorLog(e.getLocalizedMessage());
			e.printStackTrace();
			return null;
		} catch(NoHttpResponseException e) {
			return null;
		}
		
	}
	
	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public String getLot_number() {
		return lot_number;
	}

	public double getXpos() {
		return xpos;
	}

	public double getYpos() {
		return ypos;
	}

	public boolean isOpen() {
		return open;
	}
	
	public int getOpenInt() {
		if(open) {
			return 1;
		} else {
			return 0;
		}
	}
	
	public String getLink() {
		return link;
	}
	
	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();
		
		json.put("name", name);
		json.put("address", address);
		json.put("lot_number", lot_number);
		json.put("xpos", xpos);
		json.put("ypos", ypos);
		json.put("open", open);
		json.put("link", link);
		
		return json;
	}
	
	private static int isOpened(String name) {
		char[] c = name.toCharArray();
		if(c[c.length-1] != ')') {
			return -1;
		} 
		
		for(int i=0;i<c.length;i++) {
			if(c[i] == '(') {
				return i;
			}
		}
		return -1;
	}
	
	private static class Pos {
		double xpos;
		double ypos;
		
		public Pos(double xpos, double ypos) {
			super();
			this.xpos = xpos;
			this.ypos = ypos;
		}
		
		
	}
}
