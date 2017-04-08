package eden.eliel.Platforms;

import eden.eliel.Api.JsonManager;
import eden.eliel.Api.MyAnimeListApi;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.*;

/**
 * Created by Eden on 10/4/2016.
 */
public class AutoAnimeTake implements Platform{
    private final String DEFAULT_WATCH_URL = "https://animetake.tv";
    private final String CHROME_DRIVER = "webdriver.chrome.driver";
    private final String CHROME_DRIVER_PATH = "C://chromedriver.exe";

    private WebDriver webDriver;
    private JavascriptExecutor javascriptExecutor;
    private JsonManager jsonManager;
    private String seriesId;
    private String seriesWatchId;
    private String malSeriesId;
    private String currentEpisode;
    private int episodesController;
    private boolean playing;

    public AutoAnimeTake(JsonManager jsonManager){
        this.jsonManager = jsonManager;
        episodesController = 0;
        playing = false;

        System.setProperty(CHROME_DRIVER, CHROME_DRIVER_PATH);
    }

    @Override
    public void execute(String seriesName) throws InterruptedException {
        boolean alreadyFinished = false;
        webDriver = new ChromeDriver();
        javascriptExecutor = (JavascriptExecutor) webDriver;
        webDriver.manage().window().maximize();

        setSeries(seriesName);

        if (seriesId == null) {
            webDriver.close();
            return;
        }
        ArrayList<String> allEpisodes = getEpisodes();

        alreadyFinished = isAlreadyFinished(seriesName,allEpisodes);
        if (alreadyFinished){
            currentEpisode = allEpisodes.get(allEpisodes.indexOf(currentEpisode) + 1);
        }
        try {
            startWatchingSeries(seriesName, allEpisodes);
        }
        catch (Exception e) {
            webDriver.quit();
        }

        webDriver.quit();
    }
    @Override
    public void pauseVideoRequest() {
        javascriptExecutor.executeScript("$('#olvideo_html5_api')[0].pause()");
    }
    @Override
    public void playVideoRequest() {
        javascriptExecutor.executeScript("$('#olvideo_html5_api')[0].play()");
    }
    @Override
    public void nextVideoRequest() {
        episodesController = -1;
    }
    @Override
    public void prevVideoRequest() {
        episodesController = 1;
    }
    @Override
    public void setCurrentTime(int timePercent) {
        javascriptExecutor.executeScript("$('#olvideo_html5_api')[0].currentTime = " + (timePercent*getDuration()/100));
    }
    @Override
    public double getTime() {
        return Double.parseDouble(javascriptExecutor.executeScript("return $('#olvideo_html5_api')[0].currentTime").toString());
    }
    @Override
    public double getDuration() {
        return Double.parseDouble(javascriptExecutor.executeScript("return $('#olvideo_html5_api')[0].duration").toString());
    }
    @Override
    public boolean isPlaying() {
        return playing;
    }

    private void startWatchingSeries(String seriesName,ArrayList<String> allEpisodes) throws InterruptedException {
        for (int i = allEpisodes.indexOf(currentEpisode); i>0; i--){
            ArrayList <String> myAnimeListApiParams = new ArrayList<>();
            jsonManager.setKeyBySeries(seriesName,"FinishedEpisode","false");
            updateJson(seriesName,allEpisodes.get(i));
            webDriver.get(DEFAULT_WATCH_URL + "/watch/" + seriesWatchId + "-episode-" + allEpisodes.get(i));
            System.out.println(i + " - " + allEpisodes.get(i));
            playVideo();
            if (episodesController != 0){
                i += episodesController+1;
                episodesController = 0;
            }
            System.out.println(i + " - " + allEpisodes.get(i));

            if (allEpisodes.get(i).contains("final"))
                myAnimeListApiParams.add("status=2");
            myAnimeListApiParams.add("episode="+allEpisodes.get(i));
            if (malSeriesId != null)
                MyAnimeListApi.updateMalSeries(malSeriesId, myAnimeListApiParams.toArray(new String[myAnimeListApiParams.size()]));
            jsonManager.setKeyBySeries(seriesName,"FinishedEpisode","true");
            if (!(i+1 >= allEpisodes.size()))
                updateJson(seriesName,allEpisodes.get(i-1));
        }

    }
    private boolean isAlreadyFinished(String seriesName, ArrayList<String> allEpisodes){
        if (jsonManager.getKeyBySeries(seriesName,"FinishedEpisode") != null &&
                jsonManager.getKeyBySeries(seriesName,"FinishedEpisode").equals("true") &&
                allEpisodes.indexOf(currentEpisode) != allEpisodes.size()-1) {
            return true;
        }
        return false;
    }
    private void playVideo() throws InterruptedException {
        List<WebElement> openloadButton = webDriver.findElements(By.id("openload_button"));

        removeAds();
        if (openloadButton.size() > 0)
            openloadButton.get(0).click();

        int iframe_count = webDriver.findElements(By.tagName("iframe")).size();

        if (iframe_count > 4){
            webDriver.get(webDriver.findElement(By.tagName("iframe")).getAttribute("src"));
            String streamurl = javascriptExecutor.executeScript("return $('#streamurl')[0].innerHTML").toString();
            javascriptExecutor.executeScript("document.getElementById('olvideo_html5_api').setAttribute('src','/stream/" + streamurl + "?mime=true')");
            javascriptExecutor.executeScript("$('#olvideo_html5_api')[0].play()");
            while (!javascriptExecutor.executeScript("return $('#olvideo_html5_api')[0].ended").toString().equals("true") && episodesController == 0){
                Thread.sleep(2000);
                playing = true;
            }
            playing = false;
        }
        else {
            javascriptExecutor.executeScript("document.getElementsByClassName('embed-responsive embed-responsive-16by9')[0].setAttribute('style', 'height: 1020px!important; width: 1910px!important')");
            javascriptExecutor.executeScript("scroll(1000,430)");
            while (!javascriptExecutor.executeScript("return document.getElementsByTagName('video')[0].ended").toString().equals("true") && episodesController == 0){
                Thread.sleep(2000);
                playing = true;
            }
            playing = false;
        }
    }
    private void removeAds() {
        webDriver.findElement(By.className("page_title")).click();
        Set<String> windows = webDriver.getWindowHandles();
        if (windows.size() > 1) {
            Iterator<String> it = windows.iterator();
            String parent = it.next();
            String newwin = it.next();
            webDriver.switchTo().window(newwin);
            webDriver.close();
            webDriver.switchTo().window(parent);
        }
    }
    private void setSeries(String seriesName){
        seriesId = jsonManager.getKeyBySeries(seriesName,"Id");
        seriesWatchId = jsonManager.getKeyBySeries(seriesName,"WatchId");
        malSeriesId = jsonManager.getKeyBySeries(seriesName,"MAL");
        currentEpisode = jsonManager.getKeyBySeries(seriesName,"Episode");
        if (seriesWatchId == null){
            seriesWatchId = seriesId;
        }
    }
    private ArrayList<String> getEpisodes(){
        ArrayList episodes = new ArrayList<String>();
        webDriver.get(DEFAULT_WATCH_URL + "/anime/" + seriesId);
        List<WebElement> episodesElements = webDriver.findElements(By.xpath("(//body//*[@class='list-group-item animeinfo-content'])"));
        for (WebElement episodeElement : episodesElements){
            String href = episodeElement.getAttribute("href");
            href = href.substring(href.lastIndexOf("episode-")+"episode-".length(),href.length()-1);
            episodes.add(href);
        }
        Collections.reverse(episodes);
        return episodes;
    }
    private void updateJson(String seriesName, String episode){
        jsonManager.setKeyBySeries(seriesName,"Episode", episode);
    }
}
