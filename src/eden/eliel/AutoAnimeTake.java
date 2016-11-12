package eden.eliel;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Eden on 10/4/2016.
 */
public class AutoAnimeTake {
    private final String DEFAULT_WATCH_URL="https://animetake.tv";

    private WebDriver _webDriver;
    private JavascriptExecutor _js;
    private JsonManager _jm;
    private String _seriesId;
    private String _seriesWatchId;
    private String _malSeriesId;
    private String _currentEpisode;

    public AutoAnimeTake(JsonManager jsonManager){
        _jm = jsonManager;

        System.setProperty("webdriver.chrome.driver", "C://chromedriver.exe");
    }

    public void execute(String seriesName) throws InterruptedException {
        _webDriver = new ChromeDriver();
        _js = (JavascriptExecutor) _webDriver;
        _webDriver.manage().window().maximize();

        setSeries(seriesName);

        if (_seriesId == null) {
            _webDriver.close();
            return;
        }
        ArrayList<String> AllEpisodes = getEpisodes();

        for (int i = AllEpisodes.indexOf(_currentEpisode); i<AllEpisodes.size(); i++){
            _webDriver.get(DEFAULT_WATCH_URL + "/watch/" + _seriesWatchId + "-episode-" + AllEpisodes.get(i));
            playVideo();

            while (!_js.executeScript("return player.ended()").toString().equals("true"))
                Thread.sleep(2000);

            updateJson(seriesName,AllEpisodes.get(i+1));
            MyAnimeListApi.updateMalSeries(_malSeriesId,"episode="+AllEpisodes.get(i));
        }
        _webDriver.close();
    }

    private void playVideo(){
        _js.executeScript("player.play()");
        _js.executeScript("document.getElementById('video').setAttribute('style', 'height: 1080px!important; width: 1920px!important')");
        _js.executeScript("scroll(1000,500)");
    }
    private void setSeries(String seriesName){
        _seriesId = _jm.getKeyBySeries(seriesName,"Id");
        _seriesWatchId = _jm.getKeyBySeries(seriesName,"WatchId");
        _malSeriesId = _jm.getKeyBySeries(seriesName,"MAL");
        _currentEpisode = _jm.getKeyBySeries(seriesName,"Episode");
        if (_seriesWatchId == null){
            _seriesWatchId = _seriesId;
        }
    }
    private ArrayList<String> getEpisodes(){
        ArrayList episodes = new ArrayList<String>();
        _webDriver.get(DEFAULT_WATCH_URL + "/anime/" + _seriesId);
        List<WebElement> episodesElements = _webDriver.findElements(By.xpath("(//body//*[@class='no-bullet'])[1]/*"));
        for (WebElement episodeElement : episodesElements){
            String href = episodeElement.getAttribute("href");
            href = href.substring(href.lastIndexOf("-")+1,href.length()-1);
            episodes.add(href);
        }
        Collections.reverse(episodes);
        return episodes;
    }
    private void updateJson(String seriesName, String episode){
        _jm.setKeyBySeries(seriesName,"Episode", episode);
    }
}
