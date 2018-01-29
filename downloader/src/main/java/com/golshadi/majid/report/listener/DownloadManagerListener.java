package com.golshadi.majid.report.listener;

/**
 * Created by Majid Golshadi on 4/20/2014.
 */
public interface DownloadManagerListener {

    void OnDownloadInit(long taskId);

    void OnDownloadStarted(long taskId);

    void OnDownloadPaused(long taskId);

    void onDownloadProcess(long taskId, double percent, long downloadedLength,double chunkPercent,int timeleft);

    void OnDownloadFinished(long taskId);

    void OnDownloadRebuildStart(long taskId);

    void OnDownloadRebuildFinished(long taskId);

    void OnDownloadCompleted(long taskId);

    void OnAudioDownloadStarted(int taskId,int progress);

    void OnAudioDownloadFinished(long taskId);

    void connectionLost(long taskId);

}
