package com.golshadi.majid.core.mainWorker;

import android.content.Intent;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.golshadi.majid.Utils.helper.FileUtils;
import com.golshadi.majid.appConstants.DispatchEcode;
import com.golshadi.majid.appConstants.DispatchElevel;
import com.golshadi.majid.core.chunkWorker.Moderator;
import com.golshadi.majid.core.chunkWorker.Rebuilder;
import com.golshadi.majid.core.enums.TaskStates;
import com.golshadi.majid.database.ChunksDataSource;
import com.golshadi.majid.database.TasksDataSource;
import com.golshadi.majid.database.elements.Chunk;
import com.golshadi.majid.database.elements.Task;
import com.golshadi.majid.report.listener.DownloadManagerListenerModerator;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by Majid Golshadi on 4/20/2014.
 */
public class AsyncStartDownload extends Thread {

    private final long MegaByte = 1048576;
    //1 MibiBye not MegaByte
    private final TasksDataSource tasksDataSource;
    private final ChunksDataSource chunksDataSource;
    private final Moderator moderator;
    private final DownloadManagerListenerModerator downloadManagerListener;
    private final Task task;
    private HttpURLConnection urlConnection, audioUrlConnection = null;

    public AsyncStartDownload(TasksDataSource taskDs, ChunksDataSource chunkDs,
                              Moderator moderator, DownloadManagerListenerModerator listener, Task task) {
        this.tasksDataSource = taskDs;
        this.chunksDataSource = chunkDs;
        this.moderator = moderator;
        this.downloadManagerListener = listener;
        this.task = task;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

        // switch on task state
        switch (task.state) {

            case TaskStates.INIT:
                // -->get file info
                // -->save in table
                // -->slice file to some chunks ( in some case maybe user set 16 but we need only 4 chunk)
                //      and make file in directory
                // -->save chunks in tables

                if (!getTaskFileInfo(task))
                    break;

                convertTaskToChunks(task);


            case TaskStates.READY:

            case TaskStates.PAUSED:
                // -->-->if it's not resumable
                //          * fetch chunks
                //          * delete it's chunk
                //          * delete old file
                //          * insert new chunk
                //          * make new file
                // -->start to download any chunk
                if (!task.resumable) {
                    deleteChunk(task);
                    generateNewChunk(task);
                }
                Log.d("--------", "moderator start");
                moderator.start(task, downloadManagerListener);
                break;

            case TaskStates.DOWNLOAD_FINISHED:
                // -->rebuild general file
                // -->save in database
                // -->report to user
                Thread rb = new Rebuilder(task,
                        chunksDataSource.chunksRelatedTask(task.id), moderator);
                rb.run();
            case TaskStates.AUDIO_DOWNLOADING:
                moderator.audioDownload(task);
                break;

            case TaskStates.AUDIO_FINISHED:

            case TaskStates.END:

            case TaskStates.DOWNLOADING:
                // -->do nothing
                break;
        }

        return;
    }

    private boolean getTaskFileInfo(Task task) {

        URL url = null;

        try {
            url = new URL(task.url);
            urlConnection = (HttpURLConnection) url.openConnection();
            if (urlConnection == null) {
                Log.d(DispatchEcode.EXCEPTION, DispatchElevel.OPEN_CONNECTION);
                return false;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d(DispatchEcode.EXCEPTION, DispatchElevel.URL_INVALID);
            return false;

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(DispatchEcode.EXCEPTION, DispatchElevel.OPEN_CONNECTION);
            return false;
        }

        if (urlConnection != null) {
            task.size = urlConnection.getContentLength();
            try {
                task.extension = task.name.substring(task.name.lastIndexOf("."));
                task.name = task.name.replace(task.extension, "");
                task.quality = task.name.substring(task.name.lastIndexOf("-"));
                task.name = task.name.replace(task.quality, "");
                task.quality = task.quality.replace("-", "");
                task.extension = task.extension.replace(".", "");
            } catch (IndexOutOfBoundsException e) {
                Log.e("AsyncStartDownload", e.getMessage());
            }
        } else {
            Log.d(DispatchEcode.EXCEPTION, DispatchElevel.CONNECTION_ERROR);
            return false;
        }

//        Log.d("-------", "anything goes right");
        urlConnection.disconnect();
        if (task.isDash) {

            audioTask(task);
        }
        return true;
    }

    public boolean audioTask(Task task) {
        URL aUrl;
        try {
            aUrl = new URL(task.aUrl);
            audioUrlConnection = (HttpURLConnection) aUrl.openConnection();
            if (audioUrlConnection == null) {
                Log.d(DispatchEcode.EXCEPTION, DispatchElevel.OPEN_CONNECTION);
                return false;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
//        Log.w("AsyncStartDld Threads",Thread.currentThread().getName());
        task.aSize = audioUrlConnection.getContentLength();
        audioUrlConnection.disconnect();
        return true;
    }


    private void convertTaskToChunks(Task task) {
        if (task.size == 0) {
            // it's NOT resumable!!
            // one chunk
            task.resumable = false;
            task.chunks = 1;
        } else {
            // resumable
            // depend on file size assign number of chunks; up to Maximum user
            task.resumable = true;
            int MaximumUserCHUNKS = task.chunks / 2;
            task.chunks = 1;

            for (int f = 1; f <= MaximumUserCHUNKS; f++)
                if (task.size > MegaByte * f)
                    task.chunks = f * 2;
        }


        // Change Task State
        //return first chunk ID
        int firstChunkID =
                chunksDataSource.insertChunks(task);
        makeFileForChunks(firstChunkID, task);
        task.state = TaskStates.READY;
        tasksDataSource.update(task);
    }

    //creating chunk files in directory
    private void makeFileForChunks(int firstId, Task task) {
        for (int endId = firstId + task.chunks; firstId < endId; firstId++)
            FileUtils.create(task.save_address, String.valueOf(firstId));

    }


    private void deleteChunk(Task task) {
        List<Chunk> TaskChunks = chunksDataSource.chunksRelatedTask(task.id);

        for (Chunk chunk : TaskChunks) {
            FileUtils.delete(task.save_address, String.valueOf(chunk.id));
            chunksDataSource.delete(chunk.id);
        }
    }

    private void generateNewChunk(Task task) {
        int firstChunkID =
                chunksDataSource.insertChunks(task);
        makeFileForChunks(firstChunkID, task);
    }

}
