package eden.eliel.Platforms;

/**
 * Created by Eden on 2/25/2017.
 */
public interface Platform {
    void execute(String seriesName) throws InterruptedException;
    void pauseVideoRequest();
    void playVideoRequest();
    void nextVideoRequest();
    void prevVideoRequest();
    void setCurrentTime(int timePercent);
    double getTime();
    double getDuration();
    boolean isPlaying();
}
