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

    private WebDriver _webDriver;
    private JavascriptExecutor _js;
    private JsonManager _jm;
    private String _seriesId;
    private int _currentSeason;
    private int _currentEpisode;

    public AutoSdarot(JsonManager jsonManager){
        _jm = jsonManager;

        System.setProperty("webdriver.chrome.driver", "C://chromedriver.exe");
    }

    public void execute(String seriesName) {
        _webDriver = new ChromeDriver();
        _js = (JavascriptExecutor) _webDriver;
        _webDriver.manage().window().maximize();

        setSeries(seriesName);
        Boolean needRefresh,videoError;
        int maxSeason, maxEpisode;

        if (_seriesId == null) {
            _webDriver.close();
            return;
        }
        maxSeason = getSeasonsLength();

        for (; _currentSeason <= maxSeason; _currentSeason++) {
            updateJson(seriesName,_currentSeason,_currentEpisode);
            maxEpisode = getEpisodesLength();
            for (; _currentEpisode <= maxEpisode; _currentEpisode++) {
                try {
                    _webDriver.get(DEFAULT_WATCH_URL + _seriesId + "/season/" + _currentSeason + "/episode/" + _currentEpisode);
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

                        videoError = false;
                        while (!videoError && !_js.executeScript("return jwplayer().getState()").equals("playing")) {
                            if (_js.executeScript("return jwplayer().getState()").equals("error")) {
                                videoError = true;
                                _webDriver.navigate().refresh();
                            } else {
                                if (!_js.executeScript("return jwplayer().getState()").equals("buffering"))
                                    playVideo();
                            }
                            needRefresh = videoError;
                            Thread.sleep(1000);
                        }
                    }

                    while (!_js.executeScript("return jwplayer().getState()").equals("complete"))
                        Thread.sleep(2000);

                    updateJson(seriesName, _currentSeason, _currentEpisode + 1);
                    _currentEpisode = 1;
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        _webDriver.quit();
    }
    public void setSeries(String seriesName){
        _seriesId = _jm.getKeyBySeries(seriesName,"Id");
        _currentSeason = Integer.parseInt(_jm.getKeyBySeries(seriesName,"Season"));
        _currentEpisode = Integer.parseInt(_jm.getKeyBySeries(seriesName,"Episode"));
    }
    public String getKeyBySeries(String series,String key){
        return _jm.getKeyBySeries(series,key);
    }
    public boolean removeSeries(String series) {
        return _jm.removeSeries(series);
    }

    private <T> T coalesce(T a, T b){
        return a == null? b: a;
    }
    private void removeAds(){
        String firstDivId = _webDriver.findElement(By.xpath("(//body/div)[1]")).getAttribute("id");
        if (!firstDivId.equals("fb-root") && !firstDivId.equals("")){
            _js.executeScript("document.getElementById('" + firstDivId + "').remove()");
        }

        ArrayList<WebElement> app_ads = new ArrayList<>(_webDriver.findElements(By.xpath("//body/*[@type='application/x-shockwave-flash']")));
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
        _webDriver.get(DEFAULT_WATCH_URL+_seriesId);
        return Integer.parseInt(_webDriver.findElement(By.xpath("(//body//*[@id='season'])/*[last()]")).getAttribute("data-season"));
    }
    private int getEpisodesLength(){
        _webDriver.get(DEFAULT_WATCH_URL+_seriesId + "/season/" + _currentSeason);
        return Integer.parseInt(_webDriver.findElement(By.xpath("(//body//*[@id='episode'])/*[last()]")).getAttribute("data-episode"));
    }
    private void updateJson(String seriesName,int season, int episode){
        _jm.setKeyBySeries(seriesName,"Episode", episode+"");
        _jm.setKeyBySeries(seriesName,"Season", season+"");
    }
}
