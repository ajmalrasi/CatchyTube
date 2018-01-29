package catchytube.com.rasi.ajmal;

import android.app.Application;
import android.content.Context;
import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import static catchytube.com.rasi.ajmal.network.UrlEndPoints.*;
import static org.acra.ReportField.LOGCAT;
import static org.acra.ReportField.STACK_TRACE;

@ReportsCrashes(
        formUri = "http://www.catchytube.com/crash/",
        applicationLogFile = "error.log",
        customReportContent = {STACK_TRACE, LOGCAT}
)
public class AppContext extends Application {
    private static AppContext sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance=this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
    }

    public String getVideoRequestUrl(int limit, String geo, int cat){
        return YOUTUBE_API_VIDEO_URL+
                URL_CHAR_QUESTION+
                URL_CHAR_KEY+
                URL_CHAR_EQUALS+
                YOUTUBE_API_KEY+
                URL_CHAR_AMEPERSAND+
                URL_CHAR_PART+
                URL_CHAR_EQUALS+
                URL_CHAR_ID+
                URL_CHAR_COMMA+
                URL_CHAR_SNIPPRT+
                URL_CHAR_COMMA+
                URL_CHAR_STATISTICS+
                URL_CHAR_COMMA+
                URL_CHAR_CONTENT_DETAILS+
                URL_CHAR_AMEPERSAND+
                URL_CHAR_CHART+
                URL_CHAR_EQUALS+
                URL_CHAR_POPULAR+
                URL_CHAR_AMEPERSAND+
                URL_CHAR_REGIONCODE+
                URL_CHAR_EQUALS+
                geo+
                URL_CHAR_AMEPERSAND+
                URL_VIDEO_CATEGORY+
                URL_CHAR_EQUALS+
                cat+
                URL_CHAR_AMEPERSAND+
                URL_CHAR_MAXRESULT+
                URL_CHAR_EQUALS+
                limit;
    }


    public String getSearchRequestUrl(String query,int limit){
        return YOUTUBE_API_SEARCH_URL+
                URL_CHAR_QUESTION+
                URL_CHAR_KEY+
                URL_CHAR_EQUALS+
                YOUTUBE_API_KEY+
                URL_CHAR_AMEPERSAND+
                URL_CHAR_QUERY+
                URL_CHAR_EQUALS+
                query+
                URL_CHAR_AMEPERSAND+
                URL_CHAR_PART+
                URL_CHAR_EQUALS+
                URL_CHAR_SNIPPRT+
                URL_CHAR_AMEPERSAND+
                URL_SEARCH_TYPE+
                URL_CHAR_EQUALS+
                URL_TYPE_VIDEO+
                URL_CHAR_AMEPERSAND+
                URL_CHAR_MAXRESULT+
                URL_CHAR_EQUALS+
                limit;
    }

    public String getSearchRequestUrl(String query, int limit,int cat){

        //https://www.googleapis.com/youtube/v3/
        // search?part=snippet&
        // key=AIzaSyCkgXvy_4ZTXYeaIsvKXgwcXGARo8hoWMo&maxResults=20&
        // videoCategoryId=10&location=9.9312328,76.267304&
        // locationRadius=350km&
        // order=rating&type=video
        return YOUTUBE_API_SEARCH_URL+
                URL_CHAR_QUESTION+
                URL_CHAR_KEY+
                URL_CHAR_EQUALS+
                YOUTUBE_API_KEY+
                URL_CHAR_AMEPERSAND+
                URL_CHAR_QUERY+
                URL_CHAR_EQUALS+
                query+
                URL_CHAR_AMEPERSAND+
                URL_CHAR_PART+
                URL_CHAR_EQUALS+
                URL_CHAR_SNIPPRT+
                URL_CHAR_AMEPERSAND+
                URL_SEARCH_TYPE+
                URL_CHAR_EQUALS+
                URL_TYPE_VIDEO+
                URL_CHAR_AMEPERSAND+
                URL_VIDEO_CATEGORY+
                URL_CHAR_EQUALS+
                cat+
                URL_CHAR_AMEPERSAND+
                URL_CHAR_MAXRESULT+
                URL_CHAR_EQUALS+
                limit;
    }


    public static AppContext getsInstance(){
        return  sInstance;
    }
    public static Context getAppContext(){
        return  sInstance.getApplicationContext();
    }
}
