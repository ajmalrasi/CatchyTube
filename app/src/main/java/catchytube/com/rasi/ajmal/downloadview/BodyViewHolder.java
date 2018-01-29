package catchytube.com.rasi.ajmal.downloadview;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import catchytube.com.rasi.ajmal.R;


/**
 * Created by kpajm on 01-04-2017.
 */

public class BodyViewHolder extends RecyclerView.ViewHolder {

    private ImageView dType;
    private ImageView dImage;
    private ProgressBar dProgressbar, dImageProgress;

    private TextView dTitleText, dDownloadedText, dFileSizeText, dTimeLeft, dSpeedText, dStatusText, dQualityText;


    public BodyViewHolder(View itemView) {
        super(itemView);
        dImage = (ImageView) itemView.findViewById(R.id.download_image_view);
        dSpeedText = (TextView) itemView.findViewById(R.id.download_speed_text_view);
        dProgressbar = (ProgressBar) itemView.findViewById(R.id.download_progress_bar);
        dTitleText = (TextView) itemView.findViewById(R.id.download_title_text_view);
        dDownloadedText = (TextView) itemView.findViewById(R.id.downloaded_text_view);
        dFileSizeText = (TextView) itemView.findViewById(R.id.filesize_text_view);
        dTimeLeft = (TextView) itemView.findViewById(R.id.time_left_text_view);
        dImageProgress = (ProgressBar) itemView.findViewById(R.id.image_Loading);
        dStatusText = (TextView) itemView.findViewById(R.id.statusText);
        dQualityText = (TextView) itemView.findViewById(R.id.qualityText);
        dType = (ImageView) itemView.findViewById(R.id.dType);


    }

    public void populate(Context context, DownloadListItems videoInfo) {

        itemView.setTag(videoInfo);
        dTitleText.setText(videoInfo.getTitle());
        dProgressbar.setProgress(videoInfo.getProgress());
        dSpeedText.setText(videoInfo.getDownloadSpeed());
        dDownloadedText.setText(videoInfo.getDownloaded());
        dFileSizeText.setText(videoInfo.getFileSize());
        dTimeLeft.setText(videoInfo.getTimeLeft());
        dQualityText.setText(videoInfo.getQuailty());
        Picasso.with(context)
                .load(videoInfo.getVideoThumbnail())
                .into(dImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        dImageProgress.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {

                    }
                });
        //Video Type
        if (videoInfo.getType() != null) {
            //error
            if (videoInfo.getType().equals("mp4")) {
                dType.setImageResource(R.drawable.ic_video);
            } else if(videoInfo.getType().equals("m4a")) {
                dType.setImageResource(R.drawable.ic_music);
            }
        }
        //status based on state
        switch (videoInfo.getState()) {
            case 1:
                dProgressbar.setVisibility(View.VISIBLE);
                dStatusText.setText("Initializing");
                dStatusText.setTextColor(Color.parseColor("#ff669900"));
            case 2:
                if (dSpeedText.getVisibility() == View.INVISIBLE) {
                    dSpeedText.setVisibility(View.VISIBLE);
                    dProgressbar.setVisibility(View.VISIBLE);
                    dStatusText.setText("Downloading");
                    dStatusText.setTextColor(Color.parseColor("#ff669900"));
                    dTimeLeft.setVisibility(View.VISIBLE);
                    dDownloadedText.setVisibility(View.VISIBLE);
                }
                break;
            case 3:
                dProgressbar.setVisibility(View.VISIBLE);
                dDownloadedText.setVisibility(View.VISIBLE);
                dStatusText.setTextColor(Color.parseColor("#f17a0a"));
                dStatusText.setText("Paused");
                dSpeedText.setVisibility(View.INVISIBLE);
                dTimeLeft.setVisibility(View.INVISIBLE);
                break;
            case 4:
                dStatusText.setTextColor(Color.parseColor("#ff0099cc"));
                dStatusText.setText("Rebuilding");
                dSpeedText.setVisibility(View.INVISIBLE);
                dTimeLeft.setVisibility(View.INVISIBLE);
                break;
            case 5:
                if (dProgressbar.getVisibility()==View.INVISIBLE){
                    dProgressbar.setVisibility(View.VISIBLE);
                    dStatusText.setTextColor(Color.parseColor("#ff0099cc"));
                    dStatusText.setText("Audio");
                    dSpeedText.setVisibility(View.INVISIBLE);
                    dTimeLeft.setVisibility(View.INVISIBLE);
                }
                break;
            case 6:
                dStatusText.setTextColor(Color.parseColor("#ff0099cc"));
                dStatusText.setText("Converting");
                dProgressbar.setVisibility(View.INVISIBLE);
                dDownloadedText.setVisibility(View.GONE);
                dSpeedText.setVisibility(View.INVISIBLE);
                dTimeLeft.setVisibility(View.INVISIBLE);
                break;
            case 7:
                dStatusText.setTextColor(Color.parseColor("#ff0099cc"));
                dStatusText.setText("Completed");
                dProgressbar.setVisibility(View.INVISIBLE);
                dDownloadedText.setVisibility(View.GONE);
                dSpeedText.setVisibility(View.INVISIBLE);
                dTimeLeft.setVisibility(View.INVISIBLE);
                break;
        }

    }
}
