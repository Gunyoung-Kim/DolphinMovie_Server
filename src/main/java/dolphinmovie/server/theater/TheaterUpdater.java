package dolphinmovie.server.theater;

import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

import dolphinmovie.server.log.LogManager;

public class TheaterUpdater {
	private static volatile TheaterUpdater instance;
	
	private WebDriver driver;
	
	private static final String WEB_DRIVER_ID = "webdriver.chrome.driver";
	private static final String WEB_DRIVER_PATH = "src/main/resources/chromedriver";
	
	private TheaterUpdater() {}
	
	protected static TheaterUpdater getInstance() {
		if(instance == null) {
			synchronized(TheaterUpdater.class) {
				if(instance == null)
					instance = new TheaterUpdater();
			}
		}
		return instance;
	}
	
	/*
	protected void loadTheaterListFromDB(LinkedList<TheaterDAO> list) {
		this.theaterList = list;
	}
	*/
	
	protected LinkedList<TheaterDAO> updateTheater() {
		System.out.println("---------------------- Updating Theater Info ----------------------------");
		LinkedList<TheaterDAO> theaterList = new LinkedList<>();
		System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
		
		ChromeOptions option = new ChromeOptions();
		option.addArguments("headless");
		driver = new ChromeDriver(option);
		
		String base_url = "https://map.kakao.com/?from=total&nil_suggest=btn&q=%EC%98%81%ED%99%94%EA%B4%80&tab=place";
		
		try {
			driver.get(base_url);
			
			for(int i=0;i<7;i++) {
				WebElement[] page = new WebElement[5];
				page[0] = driver.findElement(By.xpath("/html/body/div[5]/div[2]/div[1]/div[7]/div[6]/div/a[1]"));
				page[1] = driver.findElement(By.xpath("/html/body/div[5]/div[2]/div[1]/div[7]/div[6]/div/a[2]"));
				page[2] = driver.findElement(By.xpath("/html/body/div[5]/div[2]/div[1]/div[7]/div[6]/div/a[3]"));
				page[3] = driver.findElement(By.xpath("/html/body/div[5]/div[2]/div[1]/div[7]/div[6]/div/a[4]"));
				page[4] = driver.findElement(By.xpath("/html/body/div[5]/div[2]/div[1]/div[7]/div[6]/div/a[5]"));
				WebElement next_page = driver.findElement(By.xpath("/html/body/div[5]/div[2]/div[1]/div[7]/div[6]/div/button[2]"));
				
				Actions actions = new Actions(driver);
				
				for(int j=1;j<=5;j++) {
					actions.moveToElement(page[j-1]).click().build().perform();
					Thread.sleep(300);
					Document doc = Jsoup.parse(driver.getPageSource());
					
					Elements eles = doc.getElementsByClass("link_name");
					Elements address = doc.getElementsByClass("addr");
					Elements links = doc.getElementsByClass("homepage");
					
					
					for(int k=0;k<eles.size();k++) {
						String name = eles.get(k).text();
						String addr = address.get(k).text();
						String link = links.get(k).absUrl("href");
						
						if(link.equals(""))
							theaterList.add(new TheaterDAO(name,addr));
						else
							theaterList.add(new TheaterDAO(name,addr,link));
					}
					if(i==6 && j==4)
						break;
					
				}
				Thread.sleep(300);
				if(i <6) 
					actions.moveToElement(next_page).click().build().perform();
			}
			System.out.println("Theater Info Load Success!");
			return theaterList;
		} catch(Exception e) {
			LogManager.errorLog(e.getLocalizedMessage());
			e.printStackTrace();
			return null;
		} finally {
			if(driver != null)
				driver.close();
		}
	}
}
