package catchytube.com.rasi.ajmal.fragment;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import catchytube.com.rasi.ajmal.adapter.PopularVideo;
import catchytube.com.rasi.ajmal.AppContext;
import catchytube.com.rasi.ajmal.VideoInfo;
import catchytube.com.rasi.ajmal.R;
import catchytube.com.rasi.ajmal.interfaces.ClickLayoutItem;
import catchytube.com.rasi.ajmal.modal.Video;
import catchytube.com.rasi.ajmal.network.VolleySingleton;

import static catchytube.com.rasi.ajmal.network.Keys.popular.*;


public class VideoPage extends Fragment implements ClickLayoutItem,BaseSliderView.OnSliderClickListener{

    public static final String TAG = "VideoPage";
    private static final String VIEW_MUSIC = "music";
    private static final String VIEW_MOVIE = "movie";
    private static final String VIEW_MUSIC_US = "music_us";
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private AppContext myApp = new AppContext();
    private ArrayList<Video> videoList = new ArrayList<>();
    private ArrayList<Video> videoList2 = new ArrayList<>();
    private ArrayList<Video> videoList3 = new ArrayList<>();
    private RecyclerView popularVideos,mainMusic,mainMusicUS;
    private PopularVideo popularVideoAdapter,mainMusicAdapter,mainMusicUSAdapter;
    //slider
    TextSliderView textSliderView;
    SliderLayout sliderShow;

    public VideoPage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >=21){
//            Transition transition = new Explode();
//            transition.setDuration(300);
//            getActivity().getWindow().setSharedElementExitTransition(transition);
        }
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        //System.out.println("OnCreate called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        //slider
        sliderShow = (SliderLayout) view.findViewById(R.id.slider);

        //first
        popularVideos = (RecyclerView) view.findViewById(R.id.popular);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        popularVideos.setLayoutManager(layoutManager);
        popularVideoAdapter = new PopularVideo(getActivity());
        popularVideoAdapter.setClickListener(this);
        popularVideos.setNestedScrollingEnabled(false);
        popularVideos.setAdapter(popularVideoAdapter);


        //second
        mainMusic = (RecyclerView) view.findViewById(R.id.music_main);
        GridLayoutManager layoutManager2 = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        mainMusic.setLayoutManager(layoutManager2);
        mainMusicAdapter = new PopularVideo(getActivity());
        mainMusicAdapter.setClickListener(this);
        mainMusic.setNestedScrollingEnabled(false);
        mainMusic.setAdapter(mainMusicAdapter);

        //third
        mainMusicUS = (RecyclerView) view.findViewById(R.id.music_US_main);
        GridLayoutManager layoutManager3 = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        mainMusicUS.setLayoutManager(layoutManager3);
        mainMusicUSAdapter = new PopularVideo(getActivity());
        mainMusicUSAdapter.setClickListener(this);
        mainMusicUS.setNestedScrollingEnabled(false);
        mainMusicUS.setAdapter(mainMusicUSAdapter);

        if (videoList.size() != 0) {
            popularVideoAdapter.setPopularVideoList(videoList);
            mainMusicAdapter.setPopularVideoList(videoList2);
            mainMusicUSAdapter.setPopularVideoList(videoList3);
            //System.out.println("videoList not empty");
        } else if (savedInstanceState != null) {
            videoList = savedInstanceState.getParcelableArrayList(VIEW_MUSIC);
            videoList2 = savedInstanceState.getParcelableArrayList(VIEW_MOVIE);
            videoList3 = savedInstanceState.getParcelableArrayList(VIEW_MUSIC_US);
            popularVideoAdapter.setPopularVideoList(videoList);
            mainMusicAdapter.setPopularVideoList(videoList2);
            mainMusicUSAdapter.setPopularVideoList(videoList3);
           // System.out.println("SavedInstances Not NULL and videoList empty");
            //System.out.println("initial " + popularVideoAdapter.getItemCount());
        } else {
           // System.out.println("New Request");
            sendJsonRequest(10,"IN",1,VIEW_MOVIE);
            sendJsonRequest(10,"IN",10,VIEW_MUSIC);
            sendJsonRequest(10,"US",10,VIEW_MUSIC_US);
        }
        return view;
    }

    private void sendJsonRequest(int count, String geo, int category, final String state) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                myApp.getVideoRequestUrl(count, geo, category),
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    switch (state) {
                        case VIEW_MOVIE:
                            videoList = parseJsonResponse(response);
                            popularVideoAdapter.setPopularVideoList(videoList);
                            for (int i=0;i<5;i++){
                                textSliderView = new TextSliderView(getContext());
                                textSliderView
                                        .description(videoList.get(i).getTitle())
                                        .image(videoList.get(i).getThumbnails());
                                sliderShow.addSlider(textSliderView);
                            }
                            break;
                        case  VIEW_MUSIC:
                            videoList2 = parseJsonResponse(response);
                            mainMusicAdapter.setPopularVideoList(videoList2);
                            break;
                        case  VIEW_MUSIC_US:
                            videoList3 = parseJsonResponse(response);
                            mainMusicUSAdapter.setPopularVideoList(videoList3);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null) {
//                    error.printStackTrace();
                    Log.e("Volley json error",error.getMessage());
                }
            }
        });
        requestQueue.add(request);
    }

    private ArrayList<Video> parseJsonResponse(JSONObject response) throws JSONException {
        ArrayList<Video> videoList = new ArrayList<>();
        if (response == null || response.length() == 0) {
            return null;
        }
        String nextPageToken = response.getString(NEXT_PAGE_TOKEN);
        JSONArray jsonItemsArray = response.getJSONArray(ITEMS);
        for (int i = 0; i < jsonItemsArray.length(); i++) {
            JSONObject jsonObject = jsonItemsArray.getJSONObject(i);
            JSONObject jsonSnippetObject = jsonObject.getJSONObject(SNIPPET);
            JSONObject jsonStatisticsObject = jsonObject.getJSONObject(STATISTICS);
            JSONObject jsonContentDetailsObject = jsonObject.getJSONObject(CONTENT_DETAILS);

            String id = jsonObject.getString(ID);
            String publishedDate = jsonSnippetObject.getString(PUBLISHED);
            String thumbnail = jsonSnippetObject.getJSONObject(THUMBNAILS).getJSONObject(IMAGE_MEDIUM).getString(IMAGE_URL);
            String title = jsonSnippetObject.getString(TITLE);
            String views = jsonStatisticsObject.getString(VIEWS);
            String duration = jsonContentDetailsObject.getString(DURATION);
            String categoryId = jsonSnippetObject.getString(CATEGORY_ID);
            String channelTitle = jsonSnippetObject.getString(CHANNEL);
            String likeCount = jsonStatisticsObject.getString(LIKES);
            String disLikeCount = jsonStatisticsObject.getString(DISLIKES);
            String description = jsonSnippetObject.getString(DESCRIPTION);

            Video video = new Video();
            video.setId(id);
            video.setDescription(description);
            video.setChannel(channelTitle);
            video.setLikes(likeCount);
            video.setDisLikes(disLikeCount);
            video.setCategory(categoryId);
            video.setThumbnails(thumbnail);
            video.setViews(views);
            video.setTitle(title);
            video.setDuration(duration);
            video.setPublishedDate(publishedDate);

            videoList.add(video);
        }
        return videoList;
    }

    @Override
    public void itemClicked(View view, int position, Video video) {
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        ImageView imageView = (ImageView) view.findViewById(R.id.videoThumbnail);
        Pair<View, String> p1 = Pair.create((View)imageView,imageView.getTransitionName());
        Pair<View, String> p2 = Pair.create((View)fab,fab.getTransitionName());

        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this.getActivity(),p1,p2);
        Intent intent = new Intent(this.getActivity(), VideoInfo.class);
        intent.putExtra("Video",video);
        startActivity(intent,compat.toBundle());
//        Fade fade = new Fade();
//        fade.setDuration(2000);
//        FragmentManager fm = getFragmentManager();
//        FormatList dialogFragment = new FormatList();
//        Bundle bundle = new Bundle();
//        bundle.putString("id", id);
//        dialogFragment.setCommunicator((Communicate) getActivity());
//        dialogFragment.setArguments(bundle);
//        dialogFragment.show(fm, "DownloadDialog");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(VIEW_MUSIC, videoList);
        outState.putParcelableArrayList(VIEW_MOVIE, videoList2);
        outState.putParcelableArrayList(VIEW_MUSIC_US, videoList3);
       // System.out.println("SavedInstanceState Called");

    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(getContext(),"Hello",Toast.LENGTH_SHORT).show();
    }
}
