package catchytube.com.rasi.ajmal.interfaces;

/**
 * Created by kpajm on 08-04-2017.
 */

public interface Communicate {
    void sendData(String videoId,String aUrl, String aFl);
    void sendData(String videoId,String vUrl, String vFl,String aUrl,String aFl);
    void transcodeFiles(String vLoc,String aLoc,int type);
}
