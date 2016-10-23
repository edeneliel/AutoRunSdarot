package eden.eliel;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Created by Eden on 10/4/2016.
 */
public class AutoAnimeTake {
    private final String DEFAULT_WATCH_URL="https://animetake.tv";

    private WebDriver _webDriver;
    private JavascriptExecutor _js;
    private JsonManager _jm;
    private String _seriesId;
    private String _malSeriesId;
    private int _currentEpisode;

    public AutoAnimeTake(JsonManager jsonManager){
        _jm = jsonManager;

        System.setProperty("webdriver.chrome.driver", "C://chromedriver.exe");

        _seriesId = "one-piece";
        _currentEpisode = 752;
    }

    public void execute(String seriesName) throws InterruptedException {
        _webDriver = new ChromeDriver();
        _js = (JavascriptExecutor) _webDriver;
        _webDriver.manage().window().maximize();

        setSeries(seriesName);
        int maxEpisode;

        if (_seriesId == null) {
            _webDriver.close();
            return;
        }
        maxEpisode = getEpisodesLength();
        for (; _currentEpisode <= maxEpisode; _currentEpisode++) {
            _webDriver.get(DEFAULT_WATCH_URL + "/watch/" + _seriesId + "-episode-" + _currentEpisode);
            playVideo();

            while (!_js.executeScript("return player.ended()").toString().equals("true"))
                Thread.sleep(2000);
            updateJson(seriesName,_currentEpisode+1);
            MyAnimeListApi.updateMalSeries(_malSeriesId,"episode="+_currentEpisode);
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
        _malSeriesId = _jm.getKeyBySeries(seriesName,"MAL");
        _currentEpisode = Integer.parseInt(_jm.getKeyBySeries(seriesName,"Episode"));
    }
    private int getEpisodesLength(){
        _webDriver.get(DEFAULT_WATCH_URL + "/anime/" + _seriesId);
        return _webDriver.findElements(By.xpath("(//body//*[@class='no-bullet'])[1]/*")).size();
    }
    private void updateJson(String seriesName, int episode){
        _jm.setKeyBySeries(seriesName,"Episode", episode+"");
    }
}
