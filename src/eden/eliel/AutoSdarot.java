package eden.eliel;

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
    WebDriver _webDriver;
    JavascriptExecutor _js;
    JsonManager _jm;
    String _seriesUrl;
    int _currentSeason;
    int _currentEpisode;

    public AutoSdarot(){
        _jm = new JsonManager("config.json");
        _seriesUrl = (String) _jm.getByKey("Url");
        _currentSeason = Integer.parseInt(coalesce((String) _jm.getByKey("Season"),"1"));
        _currentEpisode = Integer.parseInt(coalesce((String) _jm.getByKey("Episode"),"1"));

        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        _webDriver = new ChromeDriver();
        _js = (JavascriptExecutor) _webDriver;
        _webDriver.manage().window().maximize();
    }

    public void execute() throws InterruptedException {
        Boolean needRefresh;
        int maxSeason, maxEpisode;

        maxSeason = getSeasonsLength();

        for (; _currentSeason <= maxSeason; _currentSeason++) {
            updateJson(_currentSeason,_currentEpisode);
            maxEpisode = getEpisodesLength();
            for (; _currentEpisode <= maxEpisode; _currentEpisode++) {
                _webDriver.get(_seriesUrl + "/season/" + _currentSeason + "/episode/" + _currentEpisode);
                needRefresh = true;

                while (needRefresh) {
                    while (!_webDriver.findElement(By.id("proceed")).getAttribute("style").equals("display: inline-block;")) {
                        if (_webDriver.findElement(By.xpath("//body/div[@class='container']/*[@id='loading']//h1")).getText().equals("שגיאה!"))
                            _webDriver.navigate().refresh();
                        Thread.sleep(2000);
                    }
                    removeAds();

                    _js.executeScript("scroll(0,250)");
                    _webDriver.findElement(By.id("proceed")).click();

                    if (_js.executeScript("return jwplayer().getState()").equals("error"))
                        _webDriver.navigate().refresh();
                    else
                        needRefresh = false;
                }
                playVideo();

                while (!_js.executeScript("return jwplayer().getState()").equals("complete"))
                    Thread.sleep(2000);
                updateJson(_currentSeason,_currentEpisode+1);
            }
            _currentEpisode = 1;
        }
        _webDriver.close();
    }

    private <T> T coalesce(T a, T b){
        return a == null? b: a;
    }
    private void removeAds(){
        String firstDivId = _webDriver.findElement(By.xpath("(//body/div)[1]")).getAttribute("id");
        if (!firstDivId.equals("fb-root")){
            _js.executeScript("document.getElementById('" + firstDivId + "').remove()");
        }

        ArrayList<WebElement> app_ads = new ArrayList<WebElement>(_webDriver.findElements(By.xpath("//body/*[@type='application/x-shockwave-flash']")));
        for (WebElement app_ad:app_ads){
            _js.executeScript("document.getElementById('" + app_ad.getAttribute("id") + "').remove()");
        }
    }
    private void playVideo(){
        _js.executeScript("jwplayer().play()");
        _js.executeScript("jwplayer().setFullscreen()");
        _js.executeScript("document.getElementById('details').setAttribute('style', 'display: none')");
    }
    private int getSeasonsLength(){
        _webDriver.get(_seriesUrl);
        return Integer.parseInt(_webDriver.findElement(By.xpath("(//body//*[@id='season'])/*[last()]")).getAttribute("data-season"));
    }
    private int getEpisodesLength(){
        _webDriver.get(_seriesUrl + "/season/" + _currentSeason);
        return Integer.parseInt(_webDriver.findElement(By.xpath("(//body//*[@id='episode'])/*[last()]")).getAttribute("data-episode"));
    }
    private void updateJson(int season, int episode){
        _jm.setByID("Episode", episode+"");
        _jm.setByID("Season", season+"");
    }
}