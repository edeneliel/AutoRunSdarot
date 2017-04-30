package eden.eliel.Search;

import eden.eliel.Search.SearchSeriesBox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Eden on 11/18/2016.
 */
public class SearchAnimeTake {
    private final String USER_AGENT = "Mozilla/5.0";
    private final String BASE_URL = "https://animetake.tv/search/?key=";
    private final String BASE_ANIME_URL = "https://animetake.tv/anime/";
    private final String HREF_REGEX = "href=\"/anime/(.*)?/\"";
    private final String NAME_REGEX = "<center>(.*)?</center>";
    private final String WATCH_REGEX = "<a href=\"/watch/(.*)-episode.*/\".*>";

    public ArrayList<SearchSeriesBox> SearchSeries(String input){
        Pattern pattern;
        Matcher matcher;
        String line;
        ArrayList<SearchSeriesBox> result = new ArrayList<>();
        try {
            URL url = new URL(BASE_URL+input.replaceAll(" ","+"));
            HttpURLConnection httpConnect = setUrlConnection(url);
            httpConnect.connect();
            InputStream in = httpConnect.getInputStream();
            BufferedReader buff = new BufferedReader(new InputStreamReader(in));

            while ((line = buff.readLine()) != null){
                if (line.contains("/anime/")){
                    pattern = Pattern.compile(HREF_REGEX);
                    matcher = pattern.matcher(line);
                    matcher.find();
                    String id = matcher.group(1);
                    while ((line = buff.readLine())!= null && !line.contains("center"));

                    pattern = Pattern.compile(NAME_REGEX);
                    matcher = pattern.matcher(line);
                    matcher.find();
                    result.add(new SearchSeriesBox("",matcher.group(1),id));
                }
            }

            in.close();
            buff.close();
            httpConnect.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    public String getAnimeWatchId(String animeId){
        Pattern pattern;
        Matcher matcher;
        String animeWatchId = null;
        String line;
        try {
            URL url = new URL(BASE_ANIME_URL+animeId);
            HttpURLConnection httpConnect = setUrlConnection(url);
            httpConnect.connect();
            InputStream in = httpConnect.getInputStream();
            BufferedReader buff = new BufferedReader(new InputStreamReader(in));

            while (!(line = buff.readLine()).contains("/watch/"));
            System.out.println(line);
            pattern = Pattern.compile(WATCH_REGEX);
            matcher = pattern.matcher(line);
            matcher.find();
            animeWatchId = matcher.group(1);

            in.close();
            buff.close();
            httpConnect.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return animeWatchId;
    }

    private HttpURLConnection setUrlConnection(URL url) throws IOException {
        HttpURLConnection httpConnect = (HttpURLConnection) url.openConnection();
        httpConnect.addRequestProperty("User-Agent", USER_AGENT);

        return httpConnect;
    }
}
