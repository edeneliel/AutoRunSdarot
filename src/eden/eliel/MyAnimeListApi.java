package eden.eliel;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.io.*;
import java.net.*;

/**
 * Created by Eden on 10/23/2016.
 */
public class MyAnimeListApi {
    private static final String UPDATE_URL = "https://myanimelist.net/api/animelist/update/{id}.xml";
    private static final String ANIME_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><entry></entry>";
    private static final String USERNAME = "Thiefxsin";
    private static final String PASSWORD = "adtqmso6st";

    public static boolean updateMalSeries(String seriesId,String...params){
        HttpURLConnection httpConnect = null;
        try {
            httpConnect = setUrlConnection(new URL(UPDATE_URL.replaceFirst("\\{id\\}",seriesId)));

            String animeXml = ANIME_XML;
            for (String param : params){
                String xmlParam = param.substring(0,param.indexOf("="));
                String xmlValue = param.substring(param.indexOf("=")+1);
                animeXml = animeXml.replace("<entry>","<entry><"+xmlParam+">"+xmlValue+"</"+xmlParam+">");
            }

            PrintStream ps = new PrintStream( httpConnect.getOutputStream());
            ps.print("data="+animeXml);
            ps.close();

            httpConnect.connect();
            httpConnect.getResponseCode();
            httpConnect.disconnect();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static HttpURLConnection setUrlConnection(URL url) throws IOException {
        HttpURLConnection httpConnect = (HttpURLConnection) url.openConnection();
        HttpURLConnection.setFollowRedirects( true );

        httpConnect.setDoOutput( true );
        httpConnect.setRequestMethod("GET");

        String basicAuth = "Basic " + new String(new Base64().encode((USERNAME+":"+PASSWORD).getBytes()));
        httpConnect.setRequestProperty("Authorization", basicAuth);

        return httpConnect;
    }
}
