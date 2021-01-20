package dolphinmovie.server.navermovie;

import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import dolphinmovie.server.log.LogManager;
import dolphinmovie.server.movieInfo.MovieDAO;
import dolphinmovie.server.util.DateManager;

public class CurrentScreeningMovie {
	private static volatile CurrentScreeningMovie instance;
	
	private LinkedList<MovieDAO> movies;
	
	private WebDriver driver;
	
	private static final String WEB_DRIVER_ID = "webdriver.chrome.driver";
	private static final String WEB_DRIVER_PATH = "src/main/resources/chromedriver";
	
	private String lastUpdatedDay;
	
	private CurrentScreeningMovie() {
		
	}
	
	public static CurrentScreeningMovie getInstance() {
		if(instance == null) {
			synchronized(CurrentScreeningMovie.class) {
				if(instance == null) {
					instance = new CurrentScreeningMovie();
				}
			}
		}
		return instance;
	}
	
	private boolean updateList(String targetDt) {
		if(movies == null || DateManager.isDayChanged(targetDt, this.lastUpdatedDay)) {
			movies = new LinkedList<>();
			System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
			
			ChromeOptions option = new ChromeOptions();
			option.addArguments("headless");
			driver = new ChromeDriver(option);
			
			String base_url = "https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=0&ie=utf8&query=%EC%98%81%ED%99%94";
			
			try {
				driver.get(base_url);
				
				Document doc = Jsoup.parse(driver.getPageSource());
				
				Elements titles = doc.getElementsByClass("title _ellipsis");
				Elements imageBoxes = doc.getElementsByClass("img_box");
				Elements link = doc.getElementsByClass("btn_reserve");
				
				for(int i=0;i<titles.size();i++) {
					String title = titles.get(i).text();
					String thumbnailURL = imageBoxes.get(i).child(0).attr("src");
					String infoLink = link.get(i).attr("href");
					
					movies.add(new MovieDAO(title,thumbnailURL,infoLink));
				}
				
				driver.findElement(By.xpath("/html/body/div[3]/div[2]/div/div[1]/div[2]/div[2]/div/div/div/div/div[4]/div/a[2]")).click();
				Thread.sleep(100);
				
				doc = Jsoup.parse(driver.getPageSource());
				
				titles = doc.getElementsByClass("title _ellipsis");
				imageBoxes = doc.getElementsByClass("img_box");
				link = doc.getElementsByClass("btn_reserve");
				
				for(int i=0;i<titles.size();i++) {
					String title = titles.get(i).text();
					String thumbnailURL = imageBoxes.get(i).child(0).attr("src");
					String infoLink = link.get(i).attr("href");
					
					movies.add(new MovieDAO(title,thumbnailURL,infoLink));
				}
				
				this.lastUpdatedDay = targetDt;
				return true;
			} catch(Exception e) {
				LogManager.errorLog(e.getLocalizedMessage());
				e.printStackTrace();
				return false;
			} finally {
				if(driver != null)
					driver.close();
			}
		} else {
			return false;
		}
	}
	
	public LinkedList<MovieDAO> getCurrentScreeningMovies() {
		updateList(DateManager.getNowDateToString());
		return this.movies;
	}
}
