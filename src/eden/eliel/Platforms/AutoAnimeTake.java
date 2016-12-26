package eden.eliel.Platforms;

import eden.eliel.Api.JsonManager;
import eden.eliel.Api.MyAnimeListApi;
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

    public void execute(String seriesName){
        _webDriver = new ChromeDriver();
        _js = (JavascriptExecutor) _webDriver;
        _webDriver.manage().window().maximize();

        setSeries(seriesName);

        if (_seriesId == null) {
            _webDriver.close();
            return;
        }
        ArrayList<String> AllEpisodes = getEpisodes();

        if (_jm.getKeyBySeries(seriesName,"FinishedEpisode") != null &&
                _jm.getKeyBySeries(seriesName,"FinishedEpisode").equals("true") &&
                AllEpisodes.indexOf(_currentEpisode) != AllEpisodes.size()-1)
            _currentEpisode = AllEpisodes.get(AllEpisodes.indexOf(_currentEpisode)+1);

        for (int i = AllEpisodes.indexOf(_currentEpisode); i<AllEpisodes.size(); i++){
            try {
                ArrayList <String> myAnimeListApiParams = new ArrayList<>();
                _jm.setKeyBySeries(seriesName,"FinishedEpisode","false");
                _webDriver.get(DEFAULT_WATCH_URL + "/watch/" + _seriesWatchId + "-episode-" + AllEpisodes.get(i));
                playVideo();

                if (AllEpisodes.get(i).contains("final"))
                    myAnimeListApiParams.add("status=2");
                myAnimeListApiParams.add("episode="+AllEpisodes.get(i));
                MyAnimeListApi.updateMalSeries(_malSeriesId, myAnimeListApiParams.toArray(new String[myAnimeListApiParams.size()]));
                _jm.setKeyBySeries(seriesName,"FinishedEpisode","true");
                if (!(i+1 >= AllEpisodes.size()))
                    updateJson(seriesName,AllEpisodes.get(i+1));
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        _webDriver.quit();
    }

    private void playVideo() throws InterruptedException {
        if (_webDriver.findElements(By.id("video_html5_api")).size() == 0) {
            _webDriver.get(_webDriver.findElement(By.tagName("iframe")).getAttribute("src"));
            String streamurl = _js.executeScript("return $('#streamurl')[0].innerHTML").toString();
            _js.executeScript("document.getElementById('olvideo_html5_api').setAttribute('src','/stream/" + streamurl + "?mime=true')");
            _js.executeScript("$('#olvideo_html5_api')[0].play()");
            while (!_js.executeScript("return $('#olvideo_html5_api')[0].ended").toString().equals("true"))
                Thread.sleep(2000);
        }
        else {
            _js.executeScript("player.play()");
            _js.executeScript("document.getElementById('video').setAttribute('style', 'height: 1080px!important; width: 1920px!important')");
            _js.executeScript("scroll(1000,500)");
            while (!_js.executeScript("return player.ended()").toString().equals("true"))
                Thread.sleep(2000);
        }
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
            href = href.substring(href.lastIndexOf("episode-")+"episode-".length(),href.length()-1);
            episodes.add(href);
        }
        Collections.reverse(episodes);
        return episodes;
    }
    private void updateJson(String seriesName, String episode){
        _jm.setKeyBySeries(seriesName,"Episode", episode);
    }
}
