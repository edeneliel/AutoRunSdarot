package eden.eliel;

import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JsonManager {
    Gson _gson;
    ArrayList<Map<String,String>> _series;
    String _file;

    public JsonManager(String file) {
        _gson = new Gson();
        _file = file;
        try {
            _series = (ArrayList<Map<String, String>>) _gson.fromJson(new FileReader(_file),Map.class).get("Series");
        } catch (FileNotFoundException e) {
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
        getSeriesMap(series).put(key,value);
        try (Writer writer = new FileWriter(_file)) {
            HashMap<String,ArrayList> map = new HashMap();
            map.put("Series",_series);
            _gson.toJson(map, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> getSeriesMap(String series){
        series = series.toLowerCase();
        String tempSeriesName;
        for (Map<String, String> ser : _series){
            tempSeriesName = ser.get("Name").toLowerCase();
            if (tempSeriesName.equals(series.toLowerCase()))
                return ser;
            if (firstLetters(tempSeriesName).equals(series))
                return ser;
        }
        return null;
    }

    private String firstLetters(String str){
        String result="";
        for (String t: str.split(" ")){
            result+=t.substring(0,1);
        }
        return result;
    }
}
