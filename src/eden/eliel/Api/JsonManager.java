package eden.eliel.Api;

import com.google.gson.Gson;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JsonManager {
    private Gson gson;
    private ArrayList<Map<String,String>> series;
    private String file;
    private String fileDir;

    public JsonManager(String file) {
        gson = new Gson();
        this.file = file;
        fileDir = "";

        try {
            fileDir = getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            fileDir = fileDir.substring(0, fileDir.lastIndexOf('/')+1);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        File f = new File(fileDir + this.file);
        if(!f.exists()) {
            fileDir ="";
        }

        InputStream in = null;
        try {
            in = new FileInputStream(fileDir + this.file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BufferedReader buff = new BufferedReader(new InputStreamReader(in));
        series = (ArrayList<Map<String, String>>) gson.fromJson(buff,Map.class).get("Series");
        try {
            in.close();
            buff.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getKeyBySeries(String series,String key){
        Map<String, String> seriesMap = getSeriesMap(series);
        if (seriesMap == null)
            return null;
        return seriesMap.get(key);
    }
    public void setKeyBySeries(String series,String key,String value){
        Map<String, String> resultSeries = getSeriesMap(series);
        if (resultSeries != null)
            resultSeries.put(key,value);
        else {
            Map<String, String> newSeries = setNewSeries(series,key,value);
            this.series.add(newSeries);
        }
        updateJson();
    }
    public boolean removeSeries(String series){
        Map<String,String> seriesMap = getSeriesMap(series);
        if (seriesMap != null) {
            this.series.remove(seriesMap);
            updateJson();
            return true;
        }
        return false;
    }
    public ArrayList<String> getSeriesNames(){
        ArrayList<String> result = new ArrayList<>();
        for (Map<String,String> series: this.series)
            result.add(series.get("Name"));
        return result;
    }
    public String getPlatformOfSeries(String series){
        for (Map<String,String> seriesDetails: this.series){
            if (seriesDetails.get("Name").equals(series))
                return seriesDetails.get("Platform");
        }
        return "";
    }

    private Map<String, String> getSeriesMap(String series){
        series = series.toLowerCase();
        String tempSeriesName;
        for (Map<String, String> ser : this.series){
            tempSeriesName = ser.get("Name").toLowerCase();
            if (tempSeriesName.equals(series.toLowerCase()))
                return ser;
        }
        return null;
    }
    private Map<String, String> setNewSeries(String series,String key,String value){
        Map<String, String> newSeries = new HashMap <>();
        newSeries.put("Name",series);
        newSeries.put(key,value);
        newSeries.put("Season","1");
        newSeries.put("Episode","1");
        return newSeries;
    }
    private void updateJson(){
        try {
            OutputStream out = new FileOutputStream(fileDir + file);
            Writer writer = new OutputStreamWriter(out , "UTF-8");
            HashMap<String,ArrayList> map = new HashMap();
            map.put("Series", series);
            gson.toJson(map, writer);
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
