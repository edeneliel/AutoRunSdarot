package eden.eliel;

/**
 * Created by Eden on 7/30/2016.
 */
public class SearchSeriesBox {
    private String _hebName,_engName,_url;

    public SearchSeriesBox(String hebName,String engName,String url){
        _hebName = hebName;
        _engName = engName;
        _url = url;
    }

    public String getHebName(){
        return _hebName;
    }
    public String getEngName(){
        return _engName;
    }
    public String getUrl(){
        return _url;
    }
}
