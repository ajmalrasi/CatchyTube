package com.golshadi.majid.core.chunkWorker;


import android.util.Log;

import com.golshadi.majid.Utils.helper.FileUtils;
import com.golshadi.majid.core.enums.TaskStates;
import com.golshadi.majid.core.mainWorker.QueueModerator;
import com.golshadi.majid.database.ChunksDataSource;
import com.golshadi.majid.database.TasksDataSource;
import com.golshadi.majid.database.elements.Chunk;
import com.golshadi.majid.database.elements.Task;
import com.golshadi.majid.report.ReportStructure;
import com.golshadi.majid.report.listener.DownloadManagerListenerModerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Majid Golshadi on 4/14/2014.
 * <p>
 * start
 * stop
 * downloader thread hear i call them AsyncWorker because i use AsyncTask instead of thread
 * for more information you can see these ref:
 */

public class Moderator {

    private final int LIMIT = 1000 * 4;
    public DownloadManagerListenerModerator downloadManagerListener;
    Map<Integer, Long> map = new HashMap<>();
    private ChunksDataSource chunksDataSource;  // query on chunk table
    private TasksDataSource tasksDataSource;    // query on task table
    private HashMap<Integer, Thread> workerList;          // chunk downloader list
    private HashMap<Integer, ReportStructure> processReports;  // to save download percent
    private QueueModerator finishedDownloadQueueObserver;
    private double timeLimit = 0;
    private double byteLimit = 0;
    private double percent = -1;
    private double chunkPercent = 0;

    /*
    to calculate download percentage
    if download task is un resumable it return -1 as percent
     */
    private double speedPerSecond;
    private int timeLeft;
    private double totalSize;

    public Moderator(TasksDataSource tasksDS, ChunksDataSource chunksDS) {
        tasksDataSource = tasksDS;
        chunksDataSource = chunksDS;
        workerList = new HashMap<Integer, Thread>(); // chunk downloader with they id key
        processReports = new HashMap<Integer, ReportStructure>();
    }

    public void setQueueObserver(QueueModerator queueObserver) {
        finishedDownloadQueueObserver = queueObserver;
    }

    public void start(Task task, DownloadManagerListenerModerator listener) {
        downloadManagerListener = listener;
        // fetch task chunk info
        // set task state to Downloading
        // get any chunk file size calculate where it has to begin
        // start any of them as AsyncTask

        // fetch task chunk info
        List<Chunk> taskChunks = chunksDataSource.chunksRelatedTask(task.id);
        ReportStructure rps = new ReportStructure();
        rps.setObjectValues(task, taskChunks);
        processReports.put(task.id, rps);


        Long downloaded;
        Long totalSize;
        if (taskChunks != null) {
            task.state = TaskStates.DOWNLOADING;
            tasksDataSource.update(task);

            // get any chunk file size calculate
            int i=1;
            for (Chunk chunk : taskChunks) {
//                downloaded = new Long(FileUtils.size(task.save_address, String.valueOf(chunk.id)));
                downloaded = FileUtils.size(task.save_address, String.valueOf(chunk.id));
                totalSize = chunk.end - chunk.begin + 1;
//                totalSize = new Long(chunk.end - chunk.begin + 1);
                if (!task.resumable) {
                    chunk.begin = 0;
                    chunk.end = 0;
                    // start one chunk as AsyncTask (duplicate code!! :( )
                    Thread chunkDownloaderThread = new AsyncWorker(task, chunk, this);
                    chunkDownloaderThread.setName("Main downloader Thread "+i);
                    workerList.put(chunk.id, chunkDownloaderThread);
                    chunkDownloaderThread.start();

                } else if (!downloaded.equals(totalSize)) {
                    // where it has to begin
                    // modify start point but i have not save it in Database
                    chunk.begin = chunk.begin + downloaded;

                    // start any of them as AsyncTask
                    Thread chunkDownloaderThread = new AsyncWorker(task, chunk, this);
                    chunkDownloaderThread.setName("Main downloader Thread "+i);
                    workerList.put(chunk.id, chunkDownloaderThread);
                    chunkDownloaderThread.start();
                    i++;
                }
            }
            // notify to developer------------------------------------------------------------
            downloadManagerListener.OnDownloadStarted(task.id);
        }
    }


    public void audioDownload(Task task){
        if (task.aUrl!=null){
            task.state = TaskStates.AUDIO_DOWNLOADING;
            tasksDataSource.update(task);
            String name = task.name.concat(".m4a");
            double downloadedSize = FileUtils.size(task.save_address,name);
            if (downloadedSize==task.aSize){
                audioCompleted(task);
            }else {
                Log.i("AudioDownload","Download started");
                Thread audioDownload = new AudioWorker(task,this,downloadedSize);
                audioDownload.setPriority(10);
                audioDownload.setName("AudioDownloaderThread");
                workerList.put(1000,audioDownload);
                audioDownload.start();
            }
        }else{
            Log.e("Audio","no audio stream");
            downloadCompleted(task);
        }
    }

    private static final int A_LIMIT = 1024*16;
    private double AUDIO_FLAG = 0;
    private double audioProgress = 0;

    public void audioProgress(Task task,int progress){
        ReportStructure report2 = processReports.get(task.id);
        long downloadLength = report2.setAudioLength(progress);
        AUDIO_FLAG +=progress;
        if (AUDIO_FLAG >A_LIMIT){
            AUDIO_FLAG =0;
            audioProgress = (downloadLength/task.aSize)*100;
            downloadManagerListener.OnAudioDownloadStarted(task.id,(int)audioProgress);
        }
    }

    public void audioCompleted(Task task){
        task.state = TaskStates.AUDIO_FINISHED;
        tasksDataSource.update(task);
        downloadManagerListener.OnAudioDownloadFinished(task.id);
        downloadCompleted(task);
    }

    public void downloadCompleted(Task task){
            task.state = TaskStates.END;
            task.notify = false;
            tasksDataSource.update(task);
            // notify to developer------------------------------------------------------------
            downloadManagerListener.OnDownloadCompleted(task.id);
            wakeUpObserver(task.id);
    }


    public void pauseAll(){
        List<Task> taskList = tasksDataSource.getTasksInState(TaskStates.DOWNLOADING);
        for (int i =0;i<taskList.size();i++){
            taskList.get(i).state = TaskStates.PAUSED;
            tasksDataSource.update(taskList.get(i));
        }
    }

    /*
     * pause all chunk thread related to one Task
     */
    public void audioPause(int taskID){
        Task task = tasksDataSource.getTaskInfo(taskID);
        if (task!=null&&task.state==TaskStates.AUDIO_DOWNLOADING){
            Thread audioWorker = workerList.get(1000);
            if (audioWorker !=null){
                audioWorker.interrupt();
                workerList.remove(1000);
            }else{
                Log.d("Thread","Audio Thread returned null");
            }
        }
    }


    public void pause(int taskID) {

        Task task = tasksDataSource.getTaskInfo(taskID);
        if (task != null && task.state != TaskStates.PAUSED) {
            // pause task asyncWorker
            // change task state
            // save in DB
            // notify developer

            // pause task asyncWorker
            List<Chunk> taskChunks =
                    chunksDataSource.chunksRelatedTask(task.id);
            for (Chunk chunk : taskChunks) {
                Thread worker = workerList.get(chunk.id);
                if (worker != null) {
                    worker.interrupt();
                    workerList.remove(chunk.id);
                }
            }
            Thread audioWorker = workerList.get(1000);
            if (audioWorker !=null){
                audioWorker.interrupt();
                workerList.remove(1000);
            }else{
                Log.d("Thread","Audio Thread returned null");
            }
            // change task state
            // save in DB
            task.state = TaskStates.PAUSED;
            tasksDataSource.update(task);

            // notify to developer------------------------------------------------------------
            try{
                downloadManagerListener.OnDownloadPaused(task.id);
            }catch (NullPointerException e){
                Log.e("Moderator",e.getMessage());
            }

        }
    }

    public void connectionLost(int taskId) {
        downloadManagerListener.ConnectionLost(taskId);
    }

    public void process(int taskId, long byteRead, int chunkID, double chunkSize, double speed) {
        ReportStructure report = processReports.get(taskId);

//        if (!map.containsKey(chunkID)) {
//            map.put(chunkID, (long) 0);
//        }
//
//        if (map.containsKey(chunkID)) {
//            map.put(chunkID, map.get(chunkID) + byteRead);
//        }

        timeLimit += speed;
        byteLimit += byteRead;
        long downloadLength = report.setDownloadLength(byteRead);
        if (timeLimit >= LIMIT) {
            speedPerSecond = (byteLimit / timeLimit) * report.chunks;
            timeLimit = 0;
            byteLimit = 0;
            totalSize = report.getTotalSize();
            timeLeft = (int) ((totalSize - downloadLength) / speedPerSecond) / 1000;
            if (report.isResumable()) {
//                chunkPercent = (map.get(chunkID) / chunkSize) * 100;
                percent = ((float) downloadLength / report.getTotalSize() * 100);
            }

            // notify to developer------------------------------------------------------------
//            downloadManagerListener.onDownloadProcess(taskId, percent, downloadLength,chunkPercent);
            downloadManagerListener.onDownloadProcess(taskId, percent, downloadLength, speedPerSecond, timeLeft);
        }
    }

    public void rebuild(Chunk chunk) {
        workerList.remove(chunk.id);
        List<Chunk> taskChunks =
                chunksDataSource.chunksRelatedTask(chunk.task_id); // delete itself from worker list

        for (Chunk ch : taskChunks) {
            if (workerList.get(ch.id) != null)
                return;
        }
        Task task = tasksDataSource.getTaskInfo(chunk.task_id);

        // set state task state to finished
        task.percent = 100;
        task.state = TaskStates.DOWNLOAD_FINISHED;
        tasksDataSource.update(task);
        //

        // notify to developer------------------------------------------------------------
        downloadManagerListener.OnDownloadFinished(task.id);

        // assign chunk files together
        Thread t = new Rebuilder(task, taskChunks, this);
        t.start();
    }

    public void reBuildIsDone(Task task, List<Chunk> taskChunks) {
        // delete chunk row from chunk table
        for (Chunk chunk : taskChunks) {
            chunksDataSource.delete(chunk.id);
            FileUtils.delete(task.save_address, String.valueOf(chunk.id));
        }
        // notify to developer------------------------------------------------------------
        downloadManagerListener.OnDownloadRebuildFinished(task.id);
        //Dash audio download
        if (task.isDash){
            audioDownload(task);
        }else{
            downloadCompleted(task);
        }

    }

    private void wakeUpObserver(int taskID) {
        if (finishedDownloadQueueObserver != null) {
            finishedDownloadQueueObserver.wakeUp(taskID);

        }
    }
}
