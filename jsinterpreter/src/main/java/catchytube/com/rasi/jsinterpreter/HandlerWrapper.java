package catchytube.com.rasi.jsinterpreter;

/**
 * Created by kpajm on 21-03-2017.
 */

import android.os.Handler;
import catchytube.com.rasi.interpreterinterface.HandlerWrapperInterface;

public class HandlerWrapper implements HandlerWrapperInterface {
    private final Handler mHandler;

    public HandlerWrapper() {
        mHandler = new Handler();
    }

    @Override
    public void post(Runnable r) {
        mHandler.post(r);
    }
}
