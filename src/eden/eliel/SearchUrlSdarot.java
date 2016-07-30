package eden.eliel;

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
    private final String DEFAULT_URL = "http://www.sdarot.pm/search";
    private final String USER_AGENT = "Mozilla/5.0";
    private final String FIRST_INFO_PATTERN = "<a href=\"(.*)\">(.*)<\\/a>";
    private final String SECOND_INFO_PATTERN = "<h4>(.*)<\\/h4>";

    private HttpURLConnection _hConnection;
    private URL _url;

    public SearchUrlSdarot() {
        try {
            _url = new URL(DEFAULT_URL);
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
            setUrlConnection();
            PrintStream ps = new PrintStream( _hConnection.getOutputStream() );
            ps.print("search=" + input + "&x=0&y=0");
            ps.close();
            _hConnection.connect();

            InputStream in = _hConnection.getInputStream();
            BufferedReader buff = new BufferedReader(new InputStreamReader(in));
            String data;
            while((data=buff.readLine()) != null) {
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
                    result.add(new SearchSeriesBox(matcher.group(2),secondInfo,matcher.group(1)));
                }
            }
            in.close();
            buff.close();
            _hConnection.disconnect();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void setUrlConnection() throws IOException {
        _hConnection = (HttpURLConnection) _url.openConnection();
        HttpURLConnection.setFollowRedirects( true );

        _hConnection.setDoOutput( true );
        _hConnection.setRequestMethod("POST");
        _hConnection.addRequestProperty("User-Agent", USER_AGENT);
    }
}
