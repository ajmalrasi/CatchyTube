package catchytube.com.rasi.ajmal.fragment;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.golshadi.majid.core.DownloadManagerPro;
import com.golshadi.majid.core.enums.TaskStates;
import com.golshadi.majid.report.ReportStructure;
import com.golshadi.majid.report.listener.DownloadManagerListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import catchytube.com.rasi.ajmal.R;
import catchytube.com.rasi.ajmal.interfaces.Communicate;
import catchytube.com.rasi.ajmal.downloadview.DownloadAdapter;
import catchytube.com.rasi.ajmal.downloadview.DownloadListItems;


public class DownloadManager extends Fragment implements DownloadAdapter.DownloadListener, DownloadManagerListener {

    public static final String TAG = "DownloadManager";
    RecyclerView recyclerView;
    DownloadAdapter adapter;
    ArrayList<DownloadListItems> downloadList;
    List<ReportStructure> downloadReport;
    DownloadManagerPro dm;
    Handler handler;
    DownloadListItems dlList;
    Communicate cm;

    public DownloadManager() {
        // Required empty public constructor
    }

    public boolean startDownload(final String videoId, final String fileName, final String url) {

        Runnable r = new Runnable() {
            @Override
            public void run() {
                int token = dm.addTask(videoId,fileName, url, false, true);
                try {
                    dm.startDownload(token);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
        Thread newThread = new Thread(r,"StartThread");
        newThread.start();
        return true;
    }

    public boolean startDashDownload(final String videoId, final String vUrl, final String vFl, final String aUrl, String aFl) {
        Runnable r2 = new Runnable() {
            @Override
            public void run() {
                int token = dm.addTask(videoId,vFl, vUrl, aUrl, false, true);
                try {
                    dm.startDownload(token);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread secondThread = new Thread(r2,"DashStartThread");
        secondThread.start();
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_download_list, container, false);
        dm = new DownloadManagerPro(this.getContext());
        dm.init("Catchytube/", 4, this);
        handler = new Handler();
        downloadReport = dm.downloadTasksInSameState(TaskStates.ALL);
        recyclerView = (RecyclerView) view.findViewById(R.id.download_recycler_view);
        adapter = new DownloadAdapter(this, this);
        downloadList = adapter.getDownloadList();
        for (int i = 0;i<downloadReport.size(); i++) {
            dlList = new DownloadListItems(downloadReport.get(i));
            downloadList.add(dlList);
        }
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        return view;
    }

    @Override
    public void OnDownloadListClicked(DownloadListItems downloadListItems) {
//        Toast.makeText(this.getContext(), String.valueOf(downloadListItems.getState()), Toast.LENGTH_SHORT).show();
        int state = downloadListItems.getState();
        if (state == 2) {
            dm.pauseDownload(downloadListItems.getToken());
        } else if (state == 0 || state == 3||state==5) {
            try {
                dm.startDownload(downloadListItems.getToken());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(state==4){
            dm.pauseAudioDownload(downloadListItems.getToken());
        }
        else if(state==7){
            String save = dm.singleDownloadStatus(downloadListItems.getToken()).saveAddress;
            Intent i = new Intent(Intent.ACTION_VIEW);
            save = save.replace(save.substring(save.lastIndexOf(".")), "");
            if (downloadListItems.getType().equals("mp4")){
                save= save+" - Mux.mp4";
                String mime= "video/mp4";
                i.setDataAndType(Uri.fromFile(new File(save)),mime);
            }else{
                save= save+" - M4.mp3";
                String mime= "audio/mpeg";
                i.setDataAndType(Uri.fromFile(new File(save)),mime);
            }
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                this.getContext().startActivity(i);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this.getContext(), "No handler for this type of file.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "Destroyed");

        dm.pauseAll();
//        dm.dispose();
        super.onDestroy();
    }

    @Override
    public void OnDownloadInit(long taskId) {
        Log.i(TAG, "Initializing...");
        int token = (int) taskId;
        dlList = new DownloadListItems();
        dlList.setToken(token);
        dlList.setState(2);
        downloadList.add(dlList);
        handler.post(new Runnable() {
            @Override
            public void run() {
//                catchytube.com.ajmal.rasi.com.rasi.catchytube.com.ajmal.rasi.adapter.notifyItemInserted(d);
                adapter.notifyDataSetChanged();
            }

        });
    }

    @Override
    public void OnDownloadStarted(long taskId) {
        Log.i(TAG, "Started");
        final int token = (int) taskId;
        final int position;
        if (token==0){
            position = token;
        }else{
            position = token -1;
        }
        ReportStructure dmObject = dm.singleDownloadStatus((int) taskId);
        dlList = downloadList.get(position);
        dlList.setState(2);
        dlList.setFileSize(dmObject.fileSize);
        dlList.setTitle(dmObject.name);
        dlList.setQuailty(dmObject.quality);
        dlList.setType(dmObject.type);
        dlList.setVideoThumbnail(dmObject.videoId);
        downloadList.set(position,dlList);
        handler.post(new Runnable() {
            @Override
            public void run() {
//                catchytube.com.ajmal.rasi.com.rasi.catchytube.com.ajmal.rasi.adapter.notifyItemChanged(0);
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void OnDownloadPaused(long taskId) {
        final int token = (int) taskId;
        final int position;
        if (token==0){
            position = token;
        }else{
            position = token -1;
        }
        dlList = downloadList.get(position);
        dlList.setState(3);
        downloadList.set(position,dlList);
        Log.i(TAG, "Paused");
        handler.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    public void onDownloadProcess(long taskId, double percent, long downloadedLength, double speed, int timeLeft) {
        final int token = (int) taskId;
        final int position;
        if (token==0){
             position = token;
        }else{
             position = token -1;
        }
        dlList = downloadList.get(position);
        dlList.setTimeLeft(timeLeft);
        dlList.setDownloadSpeed(speed);
        dlList.setDownloaded(downloadedLength);
        dlList.setProgress((int) percent);
        downloadList.set(position,dlList);
        handler.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }

        });
    }

    @Override
    public void OnDownloadFinished(long taskId) {
        Log.i(TAG, "Download Finished");
        final ReportStructure dmObject = dm.singleDownloadStatus((int) taskId);
        handler.post(new Runnable() {
            @Override
            public void run() {
                dlList.setDownloaded(dmObject.fileSize);
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void OnDownloadRebuildStart(long taskId) {
        Log.i(TAG, "Rebuild Started");
        dlList.setState(4);
        handler.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void OnDownloadRebuildFinished(long taskId) {
        Log.i(TAG, "Rebuild Finished");

    }

    @Override
    public void OnAudioDownloadStarted(int taskId,int progress) {
        Log.i(TAG, "Audio Downloading = " +progress);
        final int token = taskId;
        final int position;
        if (token==0){
            position = token;
        }else{
            position = token -1;
        }
        dlList = downloadList.get(position);
        dlList.setState(TaskStates.AUDIO_DOWNLOADING);
        dlList.setProgress(progress);
        downloadList.set(position,dlList);
        handler.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void OnAudioDownloadFinished(long taskId) {
        Log.i(TAG, "Audio Download Completed");
        dlList.setState(TaskStates.AUDIO_FINISHED);
        handler.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void OnDownloadCompleted(long taskId) {
        Log.i(TAG, "Download completed");
        final ReportStructure dmObject = dm.singleDownloadStatus((int) taskId);
        dlList.setState(TaskStates.END);
        handler.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
        if (dmObject.isDash){
            if (dmObject.saveAddress!=null){
                cm = (Communicate) getContext();
                String audioPath = dmObject.saveAddress.replace("mp4", "m4a");
                cm.transcodeFiles(dmObject.saveAddress, audioPath,1);
            }
            else {
                Log.e(TAG,"Saveaddress Null");
            }
        }else if(dmObject.type.equals("m4a")){
            cm = (Communicate) getContext();
            cm.transcodeFiles("",dmObject.saveAddress,2);
        }
    }

    @Override
    public void connectionLost(long taskId) {
        Log.i(TAG, "Download Failed");
    }
}
