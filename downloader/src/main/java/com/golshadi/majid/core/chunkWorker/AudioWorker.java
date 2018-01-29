package com.golshadi.majid.core.chunkWorker;


import android.util.Log;

import com.golshadi.majid.Utils.helper.FileUtils;
import com.golshadi.majid.database.elements.Task;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by Majid Golshadi on 4/14/2014.
 */
public class AudioWorker extends Thread{

    private final int BUFFER_SIZE = 1024;
    private static final String TAG = "Audio Thread";
    private final Task task;
    private byte[] buffer;
    Moderator observer;
    double downloaded;


    public AudioWorker(Task task,Moderator moderator,double downloaded){
        buffer = new byte[BUFFER_SIZE];
        this.task = task;
        this.observer = moderator;
        this.downloaded = downloaded;
    }

    @Override
    public void run() {
        URL url=null;
        HttpURLConnection connection=null;
        InputStream remoteFileIn =null;
        FileOutputStream audioFile;
        File cf;
        try {

            url = new URL(task.aUrl);
            connection = (HttpURLConnection) url.openConnection();
            // Avoid timeout exception which usually occurs in low network
            connection.setConnectTimeout(0);
            connection.setReadTimeout(0);
            if (downloaded != 0.0) // support unresumable links
            {
                connection.setRequestProperty("Range", "bytes=" + (int)downloaded + "-" + (int)task.aSize);
                Log.d(TAG,String.valueOf((int)downloaded));
                Log.d(TAG,String.valueOf((int)task.aSize));
            }
            //check for internet before connect
            connection.connect();
            String name = task.name.concat(".m4a");
            cf = new File(FileUtils.address(task.save_address, name));
            // Check response code first to avoid error stream
            int status = connection.getResponseCode();
            Log.d(TAG, "Status code "+String.valueOf(status));
            switch (status){
                case 302:
                    break;
                case 416:
                    remoteFileIn = connection.getErrorStream();
                    break;
                default:
                    remoteFileIn = connection.getInputStream();
            }
            //outputstream
            audioFile = new FileOutputStream(cf, true);
            int len;
            //read file to buffer until end of file
            while (!isInterrupted() && (len = remoteFileIn.read(buffer)) != -1) {
                audioFile.write(buffer, 0, len);
                progress(task,len);
            }
            audioFile.flush();
            audioFile.close();
            connection.disconnect();
            if (!isInterrupted()) {
                Log.d(TAG,"After Download "+cf.length()+" = "+task.aSize);
                if (cf.length()!=0){
                    observer.audioCompleted(task);
                }else {
                    observer.audioPause(task.id);
                }
            }

        }catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (connection!=null){
                connection.disconnect();
            }
            if (remoteFileIn!=null){
                try {
                    remoteFileIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return;
    }

    public void progress(Task task,int progress){
        observer.audioProgress(task,progress);
    }

}
