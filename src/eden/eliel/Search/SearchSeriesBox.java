package eden.eliel.Search;

/**
 * Created by Eden on 7/30/2016.
 */
public class SearchSeriesBox {
    private String hebName;
    private String engName;
    private String id;

    public SearchSeriesBox(String hebName,String engName,String id){
        this.hebName = hebName;
        this.engName = engName;
        this.id = id;
    }

    public String getHebName(){
        return hebName;
    }
    public String getEngName(){
        return engName;
    }
    public String getId(){
        return id;
    }
}
