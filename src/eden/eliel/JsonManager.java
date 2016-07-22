package eden.eliel;

import com.google.gson.Gson;

import java.io.*;
import java.util.Map;

public class JsonManager {
    Gson _gson;
    Map<String,String> _map;
    String _file;

    public JsonManager(String file) {
        _gson = new Gson();
        _file = file;
        try {
            _map = _gson.fromJson(new FileReader(_file),Map.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Object getByKey(String key){
        return _map.get(key);
    }

    public void setByID(String key,String value){
        _map.put(key,value);
        try (Writer writer = new FileWriter(_file)) {
            _gson.toJson(_map, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
