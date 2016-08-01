package eden.eliel;

import com.google.gson.Gson;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JsonManager {
    private Gson _gson;
    private ArrayList<Map<String,String>> _series;
    private String _file;
    private String _fileDir;

    public JsonManager(String file) {
        _gson = new Gson();
        _file = file;
        _fileDir = "";

        try {
            _fileDir = getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            _fileDir = _fileDir.substring(0, _fileDir.lastIndexOf('/')+1);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        _fileDir = "";

        InputStream in = null;
        try {
            in = new FileInputStream(_fileDir+_file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BufferedReader buff = new BufferedReader(new InputStreamReader(in));
        _series = (ArrayList<Map<String, String>>) _gson.fromJson(buff,Map.class).get("Series");
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
            _series.add(newSeries);
        }
        try {
            OutputStream out = new FileOutputStream(_fileDir+_file);
            Writer writer = new OutputStreamWriter(out , "UTF-8");
            HashMap<String,ArrayList> map = new HashMap();
            map.put("Series",_series);
            _gson.toJson(map, writer);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public ArrayList<String> getSeriesNames(){
        ArrayList<String> result = new ArrayList<>();
        for (Map<String,String> series:_series)
            result.add(series.get("Name"));
        return result;
    }

    private Map<String, String> getSeriesMap(String series){
        series = series.toLowerCase();
        String tempSeriesName;
        for (Map<String, String> ser : _series){
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
}
