package eden.eliel;

/**
 * Created by Eden on 7/30/2016.
 */
public class SearchSeriesBox {
    private String _hebName,_engName,_id;

    public SearchSeriesBox(String hebName,String engName,String id){
        _hebName = hebName;
        _engName = engName;
        _id = id;
    }

    public String getHebName(){
        return _hebName;
    }
    public String getEngName(){
        return _engName;
    }
    public String getId(){
        return _id;
    }
}
