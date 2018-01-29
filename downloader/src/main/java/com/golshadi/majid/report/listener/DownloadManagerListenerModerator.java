package com.golshadi.majid.report.listener;

/**
 * Created by Majid Golshadi on 4/21/2014.
 */
public class DownloadManagerListenerModerator {

    private DownloadManagerListener downloadManagerListener;

    public DownloadManagerListenerModerator(DownloadManagerListener listener){
        downloadManagerListener = listener;
    }

    public void OnDownloadInit(long taskId) {
        if (downloadManagerListener != null) {
            downloadManagerListener.OnDownloadInit(taskId);
        }
    }

    public void OnDownloadStarted(long taskId) {
        if (downloadManagerListener != null) {
            downloadManagerListener.OnDownloadStarted(taskId);
        }
    }

    public void OnDownloadPaused(long taskId) {
        if (downloadManagerListener != null) {
            downloadManagerListener.OnDownloadPaused(taskId);
        }
    }

    public void onDownloadProcess(long taskId, double percent, long downloadedLength,double chunkPercent,int timeleft) {
        if (downloadManagerListener != null) {
            downloadManagerListener.onDownloadProcess(taskId, percent, downloadedLength,chunkPercent,timeleft);
        }
    }

    public void OnDownloadFinished(long taskId) {
        if (downloadManagerListener != null) {
            downloadManagerListener.OnDownloadFinished(taskId);
        }
    }

    public void OnDownloadRebuildStart(long taskId) {
        if (downloadManagerListener != null) {
            downloadManagerListener.OnDownloadRebuildStart(taskId);
        }
    }


    public void OnDownloadRebuildFinished(long taskId) {
        if (downloadManagerListener != null) {
            downloadManagerListener.OnDownloadRebuildFinished(taskId);
        }
    }

    public void OnDownloadCompleted(long taskId) {
        if (downloadManagerListener != null) {
            downloadManagerListener.OnDownloadCompleted(taskId);
        }
    }

    public void OnAudioDownloadStarted(int taskId,int progress) {
        if (downloadManagerListener != null) {
            downloadManagerListener.OnAudioDownloadStarted(taskId,progress);
        }
    }


    public void OnAudioDownloadFinished(long taskId) {
        if (downloadManagerListener != null) {
            downloadManagerListener.OnAudioDownloadFinished(taskId);
        }
    }
    
    public void ConnectionLost(long taskId){
    	if (downloadManagerListener != null) {
			downloadManagerListener.connectionLost(taskId);
		}
    }
}
