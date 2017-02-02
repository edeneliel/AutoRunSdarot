package eden.eliel.Platforms;

import eden.eliel.Api.JsonManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;

/**
 * Created by Eden on 7/22/2016.
 */
public class AutoSdarot {
    private final String DEFAULT_WATCH_URL="http://www.sdarot.pm/watch/";
    private final String CHROME_DRIVER = "webdriver.chrome.driver";
    private final String CHROME_DRIVER_PATH = "C://chromedriver.exe";

    private WebDriver webDriver;
    private JavascriptExecutor javascriptExecutor;
    private JsonManager jsonManager;
    private String seriesId;
    private int currentSeason;
    private int currentEpisode;

    public AutoSdarot(JsonManager jsonManager){
        this.jsonManager = jsonManager;

        System.setProperty(CHROME_DRIVER, CHROME_DRIVER_PATH);
    }

    public void execute(String seriesName) throws InterruptedException {
        webDriver = new ChromeDriver();
        javascriptExecutor = (JavascriptExecutor) webDriver;
        webDriver.manage().window().maximize();

        setSeries(seriesName);
        int maxSeason, maxEpisode;

        if (seriesId == null) {
            webDriver.close();
            return;
        }
        maxSeason = getSeasonsLength();

        for (; currentSeason <= maxSeason; currentSeason++) {
            updateJson(seriesName, currentSeason, currentEpisode);
            maxEpisode = getEpisodesLength();
            for (; currentEpisode <= maxEpisode; currentEpisode++) {
                webDriver.get(DEFAULT_WATCH_URL + seriesId + "/season/" + currentSeason + "/episode/" + currentEpisode);

                try {
                    clickAfterWait();

                    while (!javascriptExecutor.executeScript("return jwplayer().getState()").equals("complete"))
                        Thread.sleep(2000);
                }
                catch (Exception e){
                    webDriver.quit();
                }

                updateJson(seriesName, currentSeason, currentEpisode + 1);
            }
            currentEpisode = 1;
        }
        webDriver.quit();
    }
    public String getKeyBySeries(String series,String key){
        return jsonManager.getKeyBySeries(series,key);
    }

    private void clickAfterWait() throws InterruptedException {
        boolean needRefresh = true;
        boolean videoError;

        while (needRefresh) {
            while (!webDriver.findElement(By.id("proceed")).getAttribute("style").equals("display: inline-block;")) {
                if (webDriver.findElement(By.xpath("//body/div[@class='container']/*[@id='loading']//h1")).getText().equals("שגיאה!"))
                    webDriver.navigate().refresh();
                Thread.sleep(2000);
            }
            removeAds();

            javascriptExecutor.executeScript("scroll(0,250)");
            webDriver.findElement(By.id("proceed")).click();

            videoError = false;
            while (!videoError && !javascriptExecutor.executeScript("return jwplayer().getState()").equals("playing")) {
                if (javascriptExecutor.executeScript("return jwplayer().getState()").equals("error")) {
                    videoError = true;
                    webDriver.navigate().refresh();
                } else {
                    if (!javascriptExecutor.executeScript("return jwplayer().getState()").equals("buffering"))
                        playVideo();
                }
                needRefresh = videoError;
                Thread.sleep(1000);
            }
        }
    }
    private void setSeries(String seriesName){
        seriesId = jsonManager.getKeyBySeries(seriesName,"Id");
        currentSeason = Integer.parseInt(jsonManager.getKeyBySeries(seriesName,"Season"));
        currentEpisode = Integer.parseInt(jsonManager.getKeyBySeries(seriesName,"Episode"));
    }
    private void removeAds(){
        String firstDivId = webDriver.findElement(By.xpath("(//body/div)[1]")).getAttribute("id");
        if (!firstDivId.equals("fb-root") && !firstDivId.equals("")){
            javascriptExecutor.executeScript("document.getElementById('" + firstDivId + "').remove()");
        }

        ArrayList<WebElement> app_ads = new ArrayList<>(webDriver.findElements(By.xpath("//body/*[@type='application/x-shockwave-flash']")));
        for (WebElement app_ad:app_ads){
            javascriptExecutor.executeScript("document.getElementById('" + app_ad.getAttribute("id") + "').remove()");
        }
    }
    private void playVideo(){
        javascriptExecutor.executeScript("jwplayer().play()");
        javascriptExecutor.executeScript("jwplayer().setFullscreen()");
        javascriptExecutor.executeScript("document.getElementById('details').setAttribute('style', 'display: none')");
    }
    private int getSeasonsLength(){
        webDriver.get(DEFAULT_WATCH_URL+ seriesId);
        return Integer.parseInt(webDriver.findElement(By.xpath("(//body//*[@id='season'])/*[last()]")).getAttribute("data-season"));
    }
    private int getEpisodesLength(){
        webDriver.get(DEFAULT_WATCH_URL+ seriesId + "/season/" + currentSeason);
        return Integer.parseInt(webDriver.findElement(By.xpath("(//body//*[@id='episode'])/*[last()]")).getAttribute("data-episode"));
    }
    private void updateJson(String seriesName,int season, int episode){
        jsonManager.setKeyBySeries(seriesName,"Episode", episode+"");
        jsonManager.setKeyBySeries(seriesName,"Season", season+"");
    }
}
