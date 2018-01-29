package catchytube.com.rasi.ajmal.network;

/**
 * Created by kpajm on 09-04-2017.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import com.netcompss.ffmpeg4android.CommandValidationException;
import com.netcompss.ffmpeg4android.Prefs;
import com.netcompss.loader.LoadJNI;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;


public class DownloadReceiver extends Service {

    private static String TAG = "Transcoder";
    public boolean isReadyforTranscode = false;
    String workFolder = null;
    String audioPath;
    String videoPath;
    String outPath;
    File audioFile, videoFile, finalFile;
    String[] command;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if (intent != null) {
                    switch (intent.getIntExtra("Case", 1)) {
                        case 1:
                            videoPath = intent.getStringExtra("Video");
                            audioPath = intent.getStringExtra("Audio");
                            outPath = videoPath.replace(".mp4", " - Mux.mp4");
                            audioFile = new File(audioPath);
                            videoFile = new File(videoPath);
                            if (fileExist(audioFile) && fileExist(videoFile)) {
                                command = setMuxCommands(videoPath, audioPath, outPath);
                            }
                            break;
                        case 2:
                            audioPath = intent.getStringExtra("Audio");
                            outPath = audioPath.replace(".m4a", " - M4.mp3");
                            audioFile = new File(audioPath);
                            if (fileExist(audioFile)) {
                                command = setMp3Commands(audioPath, outPath);
                            }
                            break;
                        default:
                            isReadyforTranscode = false;
                            break;
                    }
                }
                if (isReadyforTranscode) {
                    writeToFile();
                    workFolder = getApplicationContext().getFilesDir().getAbsolutePath() + "/";
                    LoadJNI converter = new LoadJNI();
                    try {
                        converter.run(command, workFolder, getApplicationContext(), false);
                    } catch (CommandValidationException e) {
                        Log.e(Prefs.TAG, "Run exeption. Command validation failed", e);
                    } catch (Throwable e) {
                        Log.e(Prefs.TAG, "Run exeption.", e);
                    } finally {
                        Log.d(TAG, "Completed");
                        deleteIfCompleted(videoFile, audioFile, finalFile);
                        stopSelf();
                    }
                }else{
                    stopSelf();
                }
            }
        };

        Thread thread = new Thread(r);
        thread.setPriority(10);
        thread.setName("Converter");
        thread.start();
        return START_NOT_STICKY;
    }


    private void getIntent(Intent intent){

    }


    private void deleteIfCompleted(File videoFile, File audioFile, File finalFile) {
        if (fileExist(finalFile)) {
            try {
                if (fileExist(videoFile))
                    videoFile.delete();
                if (fileExist(audioFile))
                    audioFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //else convert again;
    }

    private String[] setMuxCommands(String inVideoPath, String inAudioPath, String outVideoPath) {
        String[] muxCommand = {"ffmpeg", "-i", "{}", "-i", "{}", "-c", "copy", "-f", "mp4", "{}"};
        finalFile = new File(outVideoPath);
        if (!fileExist(finalFile)) {
            muxCommand[2] = inVideoPath;
            muxCommand[4] = inAudioPath;
            muxCommand[9] = outPath;
            isReadyforTranscode = true;
            return muxCommand;
        }
        return null;
    }

    private String[] setMp3Commands(String inAudioPath, String outAudioPath) {
        String[] mp3Command = {"ffmpeg", "-i", "{}", "-codec:a", "libmp3lame", "-qscale:a", "4", "-f", "mp3", "{}"};
        finalFile = new File(outAudioPath);
        if (!fileExist(finalFile)) {
            mp3Command[2] = inAudioPath;
            mp3Command[9] = outAudioPath;
            isReadyforTranscode = true;
            return mp3Command;
        }
        return null;
    }

    private String getBase64toTime() {
        Date date = new Date();
        long unixTime = date.getTime() / 1000;
        String UnixString = String.valueOf(unixTime);
        byte[] data = UnixString.getBytes();
        String base64Time = Base64.encodeToString(data, Base64.NO_WRAP);
        Log.i("License Created", base64Time);
        return base64Time;
    }

    private void writeToFile() {
        File path = new File(getFilesDir().getAbsolutePath() + "/");
        // Make sure the path directory exists.
        if (!path.exists()) {
            // Make it, if it doesn't exit
            path.mkdirs();
            Log.e("Path Error", path.getPath() + " doesnt exists");
        }
        final File file = new File(path, "ffmpeglicense.lic");
        // Save your stream, don't forget to flush() it before closing it.
        try {
            //file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(this.getBase64toTime());
            myOutWriter.close();
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Log.e("Exception", "License File write failed: " + e.getMessage());
        }
    }


    private boolean fileExist(File file) {
        if(file!=null){
            return file.exists() && !file.isDirectory();
        }
        return false;
    }

}

//ffmpeg -i input.wav -c:a aac -b:a 160k output.m4a
//ffmpeg -i maudio.webm.wav -codec:a libmp3lame -qscale:a 4 output1.mp3
//ffmpeg -i video.webm -i audio.webm -c:v copy -c:a aac -b:a 160k output.mp4
//ffmpeg -i video.webm -i outpu2t.aac -c:v copy -c:a aac output.mp4
//ffmpeg -i mvvideo.mp4 -i audio.webm -c:v copy -c:a aac -b:a 160k finalmvideot.mp4