package catchytube.com.rasi.ajmal.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import catchytube.com.rasi.ajmal.R;
import catchytube.com.rasi.ajmal.interfaces.Communicate;
import catchytube.com.rasi.extractor.VideoMeta;
import catchytube.com.rasi.extractor.YouTubeExtractor;
import catchytube.com.rasi.extractor.YtFile;

public class FormatList extends DialogFragment {

    private static final int ITAG_FOR_AUDIO = 140;
    ArrayAdapter<String> formatAdapter;
    ProgressBar loadProgress;
    ListView formatList;
    TextView reportText;
    Communicate cm;
    private ArrayList<String> formatListitems = new ArrayList<>();
    private String youtubeLink;
    private List<YtFragmentedVideo> formatsToShowList;

    public FormatList() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        setCommunicator((Communicate) context);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String ytLink = getArguments().getString("id");
        if (savedInstanceState == null) {
            if (ytLink != null) {
                youtubeLink = ytLink;
                youtubeLink = "https://www.youtube.com/watch?v=" + youtubeLink;
                getYoutubeDownloadUrl(youtubeLink);
            } else {
                Toast.makeText(getActivity(), R.string.error_no_yt_link, Toast.LENGTH_LONG).show();
                dismiss();
            }
        } else {
            dismiss();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_download_receiver, null);
        builder.setView(dialogView);

        builder.setTitle(R.string.dl_dialog_title);
        loadProgress = (ProgressBar) dialogView.findViewById(R.id.downloadListFragment);
        formatList = (ListView) dialogView.findViewById(R.id.formatList);
        reportText = (TextView) dialogView.findViewById(R.id.reportText);
        loadProgress.setVisibility(View.VISIBLE);
        return builder.create();
    }

    public void setCommunicator(Communicate c) {
        this.cm = c;
    }

    private void getYoutubeDownloadUrl(String youtubeLink) {
        new YouTubeExtractor(getContext()) {
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                if (getDialog() != null) {

                    if (ytFiles == null) {
                        reportText.setVisibility(View.VISIBLE);
                        reportText.setText(R.string.app_update);
                        dismiss();
                        Toast.makeText(getActivity(), R.string.app_update, Toast.LENGTH_LONG).show();
                        return;
                    }
                    formatsToShowList = new ArrayList<>();
                    for (int i = 0, itag; i < ytFiles.size(); i++) {
                        itag = ytFiles.keyAt(i);
                        YtFile ytFile = ytFiles.get(itag);
                        if (ytFile.getFormat().getHeight() == -1 || ytFile.getFormat().getHeight() >= 360) {
                            addFormatToList(ytFile, ytFiles);
                        }
                    }
                    Collections.sort(formatsToShowList, new Comparator<YtFragmentedVideo>() {
                        @Override
                        public int compare(YtFragmentedVideo lhs, YtFragmentedVideo rhs) {
                            return lhs.height - rhs.height;
                        }
                    });
                    loadProgress.setVisibility(View.GONE);
                    formatList.setVisibility(View.VISIBLE);
                    formatAdapter = new ArrayAdapter<>(getActivity(), R.layout.dialog_layout_download, formatListitems);
                    for (YtFragmentedVideo files : formatsToShowList) {
                        addButtonToMainLayout(vMeta.getVideoId(),vMeta.getTitle(), files);
                    }
                    formatList.setAdapter(formatAdapter);
                }

            }
        }.extract(youtubeLink, true, false);
    }

    private void addFormatToList(YtFile ytFile, SparseArray<YtFile> ytFiles) {
        int height = ytFile.getFormat().getHeight();
        if (height != -1) {
            for (YtFragmentedVideo frVideo : formatsToShowList) {
                if (frVideo.height == height && (frVideo.videoFile == null ||
                        frVideo.videoFile.getFormat().getFps() == ytFile.getFormat().getFps())) {
                    return;
                }
            }
        }
        YtFragmentedVideo frVideo = new YtFragmentedVideo();
        frVideo.height = height;
        if (ytFile.getFormat().isDashContainer()) {
            if (height > 0) {
                frVideo.videoFile = ytFile;
                frVideo.audioFile = ytFiles.get(ITAG_FOR_AUDIO);
            } else {
                frVideo.audioFile = ytFile;
            }
        } else {
            frVideo.videoFile = ytFile;
        }
        formatsToShowList.add(frVideo);
    }

    private void addButtonToMainLayout(final String videoId, final String videoTitle, final YtFragmentedVideo ytFrVideo) {
        String btnText;
        if (ytFrVideo.height == -1)
            btnText = "Audio " + ytFrVideo.audioFile.getFormat().getAudioBitrate() + " kbit/s";
        else
            btnText = (ytFrVideo.videoFile.getFormat().getFps() == 60) ? ytFrVideo.height + "p60" :
                    ytFrVideo.height + "p";
        formatListitems.add(btnText);
        formatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filename;
                YtFragmentedVideo ytFrV = formatsToShowList.get(position);
//                Log.w("FormatList y Threads",Thread.currentThread().getName());
                if (videoTitle!=null){
                    if (videoTitle.length() > 65) {
                        filename = videoTitle.substring(0, 65);
                    } else {
                        filename = videoTitle;
                    }
                }else{
                    filename ="Video file";
                }
                filename = filename.replaceAll("\\\\|>|<|\"|\\||\\*|\\?|%|:|#|/|'", "");
                filename += (ytFrV.height == -1) ? "" : "-" + ytFrV.height + "p";
                filename += (ytFrV.height != -1) ? "" : "-" + ytFrV.audioFile.getFormat().getAudioBitrate()+"kbit/s";
                String downloadIds = "";
                //Dash audio and video
                if (ytFrV.videoFile != null) {
                    if (ytFrV.videoFile.getFormat().isDashContainer()) {
                        String videoUrl = ytFrV.videoFile.getUrl();
                        String videoFileName = filename + "." + ytFrV.videoFile.getFormat().getExt();
                        String audioUrl = ytFrV.audioFile.getUrl();
                        String audioFileName = filename + "." + ytFrV.audioFile.getFormat().getExt();
                        downloadIds += downloadFromUrl(videoId,videoUrl, videoFileName, audioUrl, audioFileName);
                        downloadIds += "-";
                    } else {
                        //standalone download
                        String videoUrl = ytFrV.videoFile.getUrl();
                        String videoFileName = filename + "." + ytFrV.videoFile.getFormat().getExt();
                        downloadIds += downloadFromUrl(videoId,videoUrl, videoFileName);
                    }
                } else if (ytFrV.audioFile != null) {
                    //audio download
                    String audioUrl = ytFrV.audioFile.getUrl();
                    String audioFileName = filename + "." + ytFrV.audioFile.getFormat().getExt();
                    ytFrV.audioFile.getFormat().getAudioBitrate();
                    downloadIds += downloadFromUrl(videoId,audioUrl, audioFileName);
                }
//                if (ytFrVideo.audioFile != null)
//                    cacheDownloadIds(downloadIds);
                dismiss();
            }
        });

    }

    private long downloadFromUrl(String videoId, String youtubeVideoDlUrl, String videoFileName, String youtubeAudioDlUrl, String audioFileName) {
//        Log.w("FormatList Threads",Thread.currentThread().getName());
        if (cm!=null) {
            cm.sendData(videoId, youtubeVideoDlUrl, videoFileName, youtubeAudioDlUrl, audioFileName);
        }
        return 1;
    }

    private long downloadFromUrl(String videoId,String youtubeAudioDlUrl, String audioFileName) {
//        Log.w("FormatList Threads",Thread.currentThread().getName());
        if (cm!=null){
            cm.sendData(videoId,youtubeAudioDlUrl, audioFileName);
        }

        return 2;
    }

    private void cacheDownloadIds(String downloadIds) {
        File dlCacheFile = new File(getActivity().getCacheDir().getAbsolutePath() + "/" + downloadIds);
        try {
            dlCacheFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class YtFragmentedVideo {
        int height;
        YtFile audioFile;
        YtFile videoFile;
    }
}
