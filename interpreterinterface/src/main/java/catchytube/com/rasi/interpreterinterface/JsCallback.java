package catchytube.com.rasi.interpreterinterface;

/**
 * Created by kpajm on 21-03-2017.
 */
public interface JsCallback {
     void onResult(String value);
     void onError(String errorMessage);
}
