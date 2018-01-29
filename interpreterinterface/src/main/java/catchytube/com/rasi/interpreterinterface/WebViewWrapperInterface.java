package catchytube.com.rasi.interpreterinterface;

/**
 * Created by kpajm on 21-03-2017.
 */

import android.webkit.WebView;

public interface WebViewWrapperInterface {
    public void loadJavaScript(String javascript);

    // Destroys the web view in order to free the memory.
    // The web view can not be accessed after is has been destroyed.
    public void destroy();

    // Returns the WebView object
    public WebView getWebView();
}
