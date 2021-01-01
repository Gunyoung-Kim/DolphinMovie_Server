package dolphinmovie.server.util;

import java.util.Calendar;
import java.util.Date;

public class DateManager {
	
	public static String getNowDateToString() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		String str = String.valueOf(cal.get(Calendar.YEAR));
		if(cal.get(Calendar.MONTH)+1 < 10) {
			str += ("0" + String.valueOf(cal.get(Calendar.MONTH)+1));
		} else {
			str += String.valueOf(cal.get(Calendar.MONTH)+1);
		}
		
		if(cal.get(Calendar.DATE) < 10) {
			str += ("0" + String.valueOf(cal.get(Calendar.DATE)));
		} else {
			str += String.valueOf(cal.get(Calendar.DATE));
		}
		return str;
	}
	
	public static String getSevenAgoString() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -7);
		String str = String.valueOf(cal.get(Calendar.YEAR));
		if(cal.get(Calendar.MONTH)+1 < 10) {
			str += ("0" + String.valueOf(cal.get(Calendar.MONTH)+1));
		} else {
			str += String.valueOf(cal.get(Calendar.MONTH)+1);
		}
		
		if(cal.get(Calendar.DATE) < 10) {
			str += ("0" + String.valueOf(cal.get(Calendar.DATE)));
		} else {
			str += String.valueOf(cal.get(Calendar.DATE));
		}
		return str;
	}
	
	public static boolean isDayChanged(Date now, Date last) {
		if(now.toString().equals(last.toString())) {
			return false;
		} else {
			return true;
		}
	}
	
	public static boolean isDayChanged(String now, String last) {
		if(now.equals(last)) {
			return false;
		} else {
			return true;
		}
	}
}
