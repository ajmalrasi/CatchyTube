package catchytube.com.rasi.ajmal.network;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import catchytube.com.rasi.ajmal.AppContext;

/**
 * Created by kpajm on 05-04-2017.
 */

public class VolleySingleton {
    private static  VolleySingleton sInstance=null;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private VolleySingleton() {
        requestQueue = Volley.newRequestQueue(AppContext.getAppContext());
        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private LruCache<String,Bitmap> cache = new LruCache<>((int)Runtime.getRuntime().maxMemory()/1024/8);
            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url,bitmap);
            }
        });
    }

    public static VolleySingleton getInstance(){
        if (sInstance==null){
            sInstance = new VolleySingleton();
        }
        return  sInstance;
    }

    public RequestQueue getRequestQueue(){
        return requestQueue;
    }
    public ImageLoader getImageLoader(){
        return imageLoader;
    }
}
