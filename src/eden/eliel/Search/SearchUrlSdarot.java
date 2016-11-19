package eden.eliel.Search;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Eden on 7/30/2016.
 */
public class SearchUrlSdarot {
    private final String DEFAULT_WATCH_URL = "https://www.sdarot.pm/search";
    private final String USER_AGENT = "Mozilla/5.0";
    private final String ID_FROM_URL = ".*\\/watch\\/(.*?)-.*";
    private final String SERIES_INFO_PATTERN = "<div class=\"title\"><h1>(.*) \\/ (.*) .*? .*?<\\/h1><\\/div>";
    private final String FIRST_INFO_PATTERN = "<a href=\"\\/watch\\/(.*?)-.*\">(.*)<\\/a>";
    private final String SECOND_INFO_PATTERN = "<h4>(.*)<\\/h4>";

    private URL _url;

    public SearchUrlSdarot() {
        try {
            _url = new URL(DEFAULT_WATCH_URL);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<SearchSeriesBox> SearchSeries(String input){
        ArrayList<SearchSeriesBox> result = new ArrayList<>();
        Pattern pattern;
        Matcher matcher;
        String firstInfo,secondInfo;

        try {
            HttpURLConnection hConnection = setUrlConnection(_url);
            PrintStream ps = new PrintStream( hConnection.getOutputStream() );
            ps.print("search=" + input + "&x=0&y=0");
            ps.close();
            hConnection.connect();

            String newLocation = hConnection.getHeaderField("Location");

            if (newLocation == null) {
                InputStream in = hConnection.getInputStream();
                BufferedReader buff = new BufferedReader(new InputStreamReader(in));
                String data;
                while ((data = buff.readLine()) != null) {
                    if (data.contains("class=\"info\"")) {
                        firstInfo = buff.readLine();
                        secondInfo = buff.readLine();

                        pattern = Pattern.compile(SECOND_INFO_PATTERN);
                        matcher = pattern.matcher(secondInfo);
                        matcher.find();
                        secondInfo = matcher.group(1);

                        pattern = Pattern.compile(FIRST_INFO_PATTERN);
                        matcher = pattern.matcher(firstInfo);
                        matcher.find();
                        result.add(new SearchSeriesBox(matcher.group(2), secondInfo, matcher.group(1)));
                    }
                }
                in.close();
                buff.close();
            }
            else {
                URL redirectUrl = new URL(newLocation);
                HttpURLConnection httpCon = setUrlConnection(redirectUrl);
                httpCon.connect();

                InputStream in = httpCon.getInputStream();
                BufferedReader buff = new BufferedReader(new InputStreamReader(in));
                String data;
                while ((data = buff.readLine()) != null) {
                    if (data.matches(SERIES_INFO_PATTERN)) {
                        String idFromRegex;
                        pattern = Pattern.compile(ID_FROM_URL);
                        matcher = pattern.matcher(newLocation);
                        matcher.find();
                        idFromRegex = matcher.group(1);

                        pattern = Pattern.compile(SERIES_INFO_PATTERN);
                        matcher = pattern.matcher(data);
                        matcher.find();
                        result.add(new SearchSeriesBox(matcher.group(1), matcher.group(2), idFromRegex));
                    }
                }
                in.close();
                buff.close();
                httpCon.disconnect();
            }
            hConnection.disconnect();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private HttpURLConnection setUrlConnection(URL url) throws IOException {
        HttpURLConnection httpConnect = (HttpURLConnection) url.openConnection();
        HttpURLConnection.setFollowRedirects( true );

        httpConnect.setDoOutput( true );
        httpConnect.setRequestMethod("POST");
        httpConnect.addRequestProperty("User-Agent", USER_AGENT);

        return httpConnect;
    }
}
