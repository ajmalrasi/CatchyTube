package com.golshadi.majid.core.chunkWorker;


import android.util.Log;

import com.golshadi.majid.Utils.helper.FileUtils;
import com.golshadi.majid.database.elements.Chunk;
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
public class AsyncWorker extends Thread {

    private static final String TAG = "Video Thread";
    private static double LIMIT = 1024;
    private final int BUFFER_SIZE = 1024;
    private final Task task;
    private final Chunk chunk;
    private final Moderator observer;
    public boolean stop = false;
    private byte[] buffer;
    private ConnectionWatchDog watchDog;
    private double chunkSize;
    private double startTime;
    private double endTime;
    private double time;
    private double speed = 0;
    private double previousTime = 0;
    private boolean flag = true;


    public AsyncWorker(Task task, Chunk chunk, Moderator moderator) {
        buffer = new byte[BUFFER_SIZE];
        this.task = task;
        this.chunk = chunk;
        this.observer = moderator;
    }

    @Override
    public void run() {
        URL url = null;
        HttpURLConnection connection = null;
        InputStream remoteFileIn = null;
        FileOutputStream chunkFile;
        File cf;
        boolean retry = false;
        try {

            url = new URL(task.url);
            connection = (HttpURLConnection) url.openConnection();
            // Avoid timeout exception which usually occurs in low network
            connection.setConnectTimeout(0);
            connection.setReadTimeout(0);
            chunkSize = chunk.end - chunk.begin;
//            Log.w("Async Worker Threads",Thread.currentThread().getName());
            if (chunk.end != 0) // support unresumable links
            {
                connection.setRequestProperty("Range", "bytes=" + chunk.begin + "-" + chunk.end);
            }
            //check for internet before connect
            connection.connect();

            cf = new File(FileUtils.address(task.save_address, String.valueOf(chunk.id)));
            // Check response code first to avoid error stream
            int status = connection.getResponseCode();

            switch (status) {
                case 302:
                    retry = true;
                    break;
                case 416:
                    remoteFileIn = connection.getErrorStream();
                    break;
                default:
                    remoteFileIn = connection.getInputStream();
                    break;
            }

            Log.d(TAG, "Status code " + String.valueOf(status));
            int i=0;
            while (retry) {
                i++;
                String newUrl = connection.getHeaderField("Location");
                connection.disconnect();
                connection = (HttpURLConnection) new URL(newUrl).openConnection();
                if (chunk.end != 0) // support unresumable links
                {
                    connection.setRequestProperty("Range", "bytes=" + chunk.begin + "-" + chunk.end);
                }
                connection.connect();
                System.out.println("Status after "+i+" tries " + connection.getResponseCode());
                if (connection.getResponseCode() == 200 || connection.getResponseCode() == 206) {
                    remoteFileIn = connection.getInputStream();
                    retry = false;
                }else if(i==5){
                    pauseRelatedTask();
                    retry = false;
                    return;
                }
            }
            //outputstream
            chunkFile = new FileOutputStream(cf, true);
            // set watchDoger to stop thread after 60 sec if no connection lost
            watchDog = new ConnectionWatchDog(60000, this);
            watchDog.start();

            int len;
            startTime = System.nanoTime();

            //read file to buffer until end of file
            while (!isInterrupted() && (len = remoteFileIn.read(buffer)) != -1) {
                time();
                watchDog.reset();
                chunkFile.write(buffer, 0, len);
                process(len, speed / 1000000);
            }

            chunkFile.flush();
            chunkFile.close();
            watchDog.interrupt();
            connection.disconnect();

            if (!isInterrupted()) {
                if (cf.length() == 0) {
                    Log.d(TAG, "File length is 0");
                    pauseRelatedTask();
                } else {
                    observer.rebuild(chunk);
                }
            }

        } catch (SocketTimeoutException e) {
            e.printStackTrace();

            observer.connectionLost(task.id);
            //pausing
            pauseRelatedTask();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            pauseRelatedTask();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (remoteFileIn != null) {
                try {
                    remoteFileIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (remoteFileIn != null) {
                try {
                    remoteFileIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return;
    }

    public synchronized void time() {
        endTime = System.nanoTime();
        time = endTime - startTime;
        speed = time - previousTime;
        previousTime = time;
    }

    private void process(int read, double speed) {
        observer.process(chunk.task_id, read, chunk.id, chunkSize, speed);
    }

    private void pauseRelatedTask() {
        observer.pause(task.id);
    }

    public void connectionTimeOut() {
        if (flag) {
            watchDog.interrupt();
            flag = false;
            observer.connectionLost(task.id);
            pauseRelatedTask();
        }

    }

}
