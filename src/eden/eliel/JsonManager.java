package eden.eliel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.Map;

public class JsonManager {
    Gson gson;
    Map<String,String> map;
    String file;

    public JsonManager(String file) {
        gson = new Gson();
        this.file = file;
        try {
            map = gson.fromJson(new FileReader(file),Map.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Object getByKey(String key){
        return map.get(key);
    }

    public void setByID(String key,String value){
        map.put(key,value);
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(map, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
