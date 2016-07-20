package eden.eliel;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;

public class Main {

    public static void removeAds(WebDriver webDriver,JavascriptExecutor js){
        String firstDivId = webDriver.findElement(By.xpath("(//body/div)[1]")).getAttribute("id");
        if (!firstDivId.equals("fb-root")){
            js.executeScript("document.getElementById('" + firstDivId + "').remove()");
        }

        ArrayList <WebElement> app_ads = new ArrayList<WebElement>(webDriver.findElements(By.xpath("//body/*[@type='application/x-shockwave-flash']")));
        for (WebElement app_ad:app_ads){
            js.executeScript("document.getElementById('" + app_ad.getAttribute("id") + "').remove()");
        }
    }

    public static void playVideo(JavascriptExecutor js){
        js.executeScript("jwplayer().play()");
        js.executeScript("jwplayer().setFullscreen()");
        js.executeScript("document.getElementById('details').setAttribute('style', 'display: none')");
    }

    public static void main(String[] args) throws InterruptedException {
        Boolean flag,needRefresh;
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        WebDriver webDriver = new ChromeDriver();
        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        webDriver.manage().window().maximize();

        for (int i = 2; i<= 10; i++) {
            webDriver.get("http://www.sdarot.pm/watch/19-%D7%94%D7%9E%D7%A4%D7%A5-%D7%94%D7%92%D7%93%D7%95%D7%9C-the-big-bang-theory/season/3/episode/" + i);
            needRefresh = true;

            while (needRefresh){
                flag = false;
                while (!flag) {
                    flag = (webDriver.findElement(By.id("proceed")).getAttribute("style").equals("display: inline-block;"));
                    if (webDriver.findElement(By.xpath("//body/div[@class='container']/*[@id='loading']//h1")).getText().equals("שגיאה!"))
                        webDriver.navigate().refresh();
                    if (!flag)
                        Thread.sleep(2000);
                }
                removeAds(webDriver, js);

                js.executeScript("scroll(0,250)");
                webDriver.findElement(By.id("proceed")).click();

                if (js.executeScript("return jwplayer().getState()").equals("error"))
                    webDriver.navigate().refresh();
                else
                    needRefresh = false;
            }
            playVideo(js);

            flag = false;
            while (!flag) {
                flag = (js.executeScript("return jwplayer().getState()").equals("complete"));
                if (!flag)
                    Thread.sleep(2000);
            }
        }
        webDriver.close();
    }
}