package catchytube.com.rasi.ajmal.downloadview;


import com.golshadi.majid.report.ReportStructure;

import java.text.DecimalFormat;

/**
 * Created by kpajm on 01-04-2017.
 */

public class DownloadListItems {
    private static String VIDEO_BASE_THUMB = "https://i.ytimg.com/vi/";
    private static String DEFAULT_THUMB = "/mqdefault.jpg";
    private String title;
    private String videoId;
    private String videoThumbnail;
    private double progress;
    private double fileSize;
    private int timeLeft;
    private double downloadSpeed;
    private double downloaded;
    private String url;
    private int state;
    private int token;
    private String quailty;
    private String type;

    private DecimalFormat decimalFormat = new DecimalFormat("0.##");


    public DownloadListItems() {
//        this.title = "Unboxing the new Galaxy S8, The Best Smartphone on the market.";
//        this.videoThumbnail = "https://i.ytimg.com/vi/ZbTzVpYWBgI/mqdefault.jpg";
//        this.progress = 62;
//        this.fileSize = 55614122;
//        this.timeLeft = 40;
//        this.downloadSpeed = 2745;
//        this.downloaded = 4556415;
//        this.url = "http://ajmalrasi.com";
//        this.state = 5;
//        this.token = 2;
    }

    public DownloadListItems(ReportStructure reportStructure) {
        this.title = reportStructure.name;
        this.videoThumbnail = VIDEO_BASE_THUMB+reportStructure.videoId+DEFAULT_THUMB;
        this.progress = reportStructure.percent;
        this.fileSize = reportStructure.fileSize;
        this.timeLeft = 0;
        this.downloadSpeed = 0;
        this.downloaded = reportStructure.downloadLength;
        this.url = reportStructure.saveAddress;
        this.state = reportStructure.state;
        this.token = reportStructure.id;
        this.quailty = reportStructure.quality;
        this.type= reportStructure.type;
        this.videoId=reportStructure.videoId;

    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public String getQuailty() {
        return quailty;
    }

    public void setQuailty(String quailty) {
        this.quailty = quailty;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVideoThumbnail(String videoThumbnail) {
        this.videoThumbnail = VIDEO_BASE_THUMB+videoThumbnail+DEFAULT_THUMB;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setFileSize(double fileSize) {
        this.fileSize = fileSize;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public void setDownloadSpeed(double downloadSpeed) {
        this.downloadSpeed = downloadSpeed;
    }

    public void setDownloaded(double downloaded) {
        this.downloaded = downloaded;
    }

    public static String formatFileSize(double size) {
        String hrSize = null;
        double b = size;
        double k = size / 1024.0;
        double m = ((size / 1024.0) / 1024.0);
        double g = (((size / 1024.0) / 1024.0) / 1024.0);

        DecimalFormat dec = new DecimalFormat("0.0");

        if (g > 1) {
            hrSize = dec.format(g).concat(" GB");
        } else if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
        } else if (k > 1) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" B");
        }
        return hrSize;

    }


    public String getDownloaded() {
        if (this.downloaded != 0) {
            return formatFileSize(this.downloaded) + " / ";
        }
        return "0 / ";
    }

    public String getTitle() {
        return title;
    }

    public String getVideoThumbnail() {
        return videoThumbnail;
    }

    public int getProgress() {
        return (int)progress;
    }

    public String getFileSize() {
        return formatFileSize(fileSize);
    }

    public String getTimeLeft() {
        if (timeLeft >= 60) {
            return timeLeft / 60 + " min";
        }
        return (timeLeft + " sec");
    }

    public String getDownloadSpeed() {
        if (downloadSpeed>=1024.0){
            return decimalFormat.format(downloadSpeed/1024)+ " MB/s";
        }
        return (int)downloadSpeed + " KB/s";
    }
}
