package catchytube.com.rasi.ajmal;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import catchytube.com.rasi.ajmal.dialog.FormatList;
import catchytube.com.rasi.ajmal.interfaces.Communicate;
import catchytube.com.rasi.ajmal.modal.Video;

public class VideoInfo extends AppCompatActivity implements View.OnClickListener{

    private Video video;
    TextView titleText,channelText,viewsText,publishedText;
    ProgressBar likeCount;
    ImageView thumbView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= 21) {
            TransitionInflater transitionInflater = TransitionInflater.from(this);
            Transition transition = transitionInflater.inflateTransition(R.transition.shared_transition);
            Transition transition2 = transitionInflater.inflateTransition(R.transition.transition_a);
            Transition transition2_exit = transitionInflater.inflateTransition(R.transition.transition_a_exit);
            getWindow().setSharedElementEnterTransition(transition);
            getWindow().setEnterTransition(transition2);
            getWindow().setReturnTransition(transition2_exit);

        }
        video = getIntent().getExtras().getParcelable("Video");
        setContentView(R.layout.video_info);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(this);
        thumbView = (ImageView) findViewById(R.id.thumbnailView);
        titleText = (TextView) findViewById(R.id.videoTitle2);
        channelText = (TextView) findViewById(R.id.channelTitle);
        viewsText = (TextView) findViewById(R.id.viewsText);
        publishedText = (TextView) findViewById(R.id.publishedText);
        likeCount = (ProgressBar) findViewById(R.id.likeProgress);


        Picasso.with(this.getApplicationContext()).load(video.getThumbnails()).into(thumbView, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError() {
            }
        });
        titleText.setText(video.getTitle());
        channelText.setText(video.getChannel());
        viewsText.setText(video.getViews());
        publishedText.setText(video.getPublishedDate());
        likeCount.setProgress(video.getLikes());
    }

    @Override
    public void onClick(View v) {
        FragmentManager fm = getSupportFragmentManager();
        FormatList dialogFragment = new FormatList();
        Bundle bundle = new Bundle();
        bundle.putString("id", video.getId());
        dialogFragment.setArguments(bundle);
        dialogFragment.setCommunicator((Communicate) MainActivity.ct);
        dialogFragment.show(fm, "DownloadDialog");
    }
}