package eden.eliel.Platforms;

import eden.eliel.Api.JsonManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Eden on 7/22/2016.
 */
public class AutoSdarot implements Platform{
    private final String DEFAULT_WATCH_URL="http://www.zira.online/watch/";
    private final String CHROME_DRIVER = "webdriver.chrome.driver";
    private final String CHROME_DRIVER_PATH = "C://chromedriver.exe";

    private WebDriver webDriver;
    private JavascriptExecutor javascriptExecutor;
    private Iterator<String> windows;
    private JsonManager jsonManager;
    private String seriesName;
    private String seriesId;
    private int currentSeason;
    private int currentEpisode;
    private int episodesController;
    private boolean playing;

    public AutoSdarot(JsonManager jsonManager){
        this.jsonManager = jsonManager;
        episodesController = 0;
        playing = false;

        System.setProperty(CHROME_DRIVER, CHROME_DRIVER_PATH);
    }

    @Override
    public void execute(String seriesName) throws InterruptedException {
        if (webDriver == null) {
            webDriver = new ChromeDriver();
            javascriptExecutor = (JavascriptExecutor) webDriver;
            webDriver.manage().window().maximize();
        }

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
                if(webDriver.getWindowHandles().size() > 1) {
                    webDriver.close();
                    windows = webDriver.getWindowHandles().iterator();
                    webDriver.switchTo().window(windows.next());
                }
                else
                    webDriver.get(DEFAULT_WATCH_URL + seriesId + "/season/" + currentSeason + "/episode/" + currentEpisode);

                try {
                    clickAfterWait();

                    boolean runNext = false;
                    playing = true;
                    while (!javascriptExecutor.executeScript("return jwplayer().getState()").equals("complete") && episodesController == 0) {
                        if (!runNext && Double.parseDouble(javascriptExecutor.executeScript("return jwplayer().getPosition()").toString())+30 > Double.parseDouble(javascriptExecutor.executeScript("return jwplayer().getDuration()").toString())){
                            runNext = true;
                            if (currentEpisode+1 > maxEpisode)
                                javascriptExecutor.executeScript("window.open(\"" + DEFAULT_WATCH_URL + seriesId + "/season/" + (currentSeason + 1) + "/episode/1\")");
                            else
                                javascriptExecutor.executeScript("window.open(\"" + DEFAULT_WATCH_URL + seriesId + "/season/" + currentSeason + "/episode/" + (currentEpisode + 1) + "\")");
                            windows = webDriver.getWindowHandles().iterator();
                            webDriver.switchTo().window(windows.next());
                        }
                        Thread.sleep(2000);
                    }
                    playing = false;

                    if (episodesController != 0) {
                        currentEpisode += episodesController-1;
                        episodesController = 0;
                    }
                    updateJson(seriesName, currentSeason, currentEpisode + 1);
                }
                catch (Exception e){
                    e.printStackTrace();
                    webDriver.quit();
                }
            }
            currentEpisode = 1;
        }
        webDriver.quit();
    }
    @Override
    public void pauseVideoRequest() {
        javascriptExecutor.executeScript("jwplayer().pause()");
    }
    @Override
    public void playVideoRequest() {
        javascriptExecutor.executeScript("jwplayer().play()");
    }
    @Override
    public void nextVideoRequest() {
        episodesController = 1;
    }
    @Override
    public void prevVideoRequest() {
        episodesController = -1;
    }
    @Override
    public void setCurrentTime(int timePercent) {
        javascriptExecutor.executeScript("jwplayer().seek(" + (timePercent*getDuration()/100) + ")");
    }
    @Override
    public double getTime() {
        return Double.parseDouble(javascriptExecutor.executeScript("return jwplayer().getPosition()").toString());
    }
    @Override
    public double getDuration() {
        return Double.parseDouble(javascriptExecutor.executeScript("return jwplayer().getDuration()").toString());
    }
    @Override
    public boolean isPlaying() {
        return playing;
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
        this.seriesName = seriesName;
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
