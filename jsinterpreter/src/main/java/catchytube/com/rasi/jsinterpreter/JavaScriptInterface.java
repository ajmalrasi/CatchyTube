package catchytube.com.rasi.jsinterpreter;

/**
 * Created by kpajm on 21-03-2017.
 */

import android.webkit.JavascriptInterface;

import catchytube.com.rasi.interpreterinterface.CallJavaResultInterface;

/**
 * Passed in addJavascriptInterface of WebView to allow web views's JS execute
 * Java code
 */
public class JavaScriptInterface {
    private final CallJavaResultInterface mCallJavaResultInterface;

    public JavaScriptInterface(CallJavaResultInterface callJavaResult) {
        mCallJavaResultInterface = callJavaResult;
    }

    @JavascriptInterface
    public void returnResultToJava(String value, int callIndex) {
        mCallJavaResultInterface.jsCallFinished(value, callIndex);
    }
}