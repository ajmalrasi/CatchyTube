package catchytube.com.rasi.ajmal;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import catchytube.com.rasi.ajmal.adapter.PopularVideo;
import catchytube.com.rasi.ajmal.interfaces.ClickLayoutItem;
import catchytube.com.rasi.ajmal.modal.Video;
import catchytube.com.rasi.ajmal.network.VolleySingleton;

import static catchytube.com.rasi.ajmal.network.Keys.popular.*;

public class SearchActivity extends AppCompatActivity implements ClickLayoutItem{

    public static final String TAG = "Search";
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private AppContext app = new AppContext();
    private ArrayList<Video> videoList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PopularVideo recyclerAdapter;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set Actionbar Title
        this.setTitle("");
        setContentView(R.layout.search_activity);

        //assign RequestQueue and init VolleySingleton
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();

        //set recycler view
        recyclerView = (RecyclerView) findViewById(R.id.search_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerAdapter = new PopularVideo(this);
        recyclerAdapter.setClickListener(this);
        recyclerView.setAdapter(recyclerAdapter);

        query = handleIntent(getIntent());
        if (query != null) {
            try{
                getSupportActionBar().setTitle("Search results for \"" + query + "\"");
            } catch(Exception e){
                e.printStackTrace();
            }

            query = urlEncode(query);
            sendJsonRequest(query);
        }
    }


    public void sendJsonRequest(String query) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                app.getSearchRequestUrl(query, 20), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    videoList = parseJsonResponse(response);
                    recyclerAdapter.setPopularVideoList(videoList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(request);
    }

    private ArrayList<Video> parseJsonResponse(JSONObject response) throws JSONException {
        ArrayList<Video> videoList = new ArrayList<>();
        if (response == null || response.length() == 0) {
            return null;
        }

        if (response.has(NEXT_PAGE_TOKEN)) {
            String nextPageToken = response.getString(NEXT_PAGE_TOKEN);
        }
        JSONArray jsonItemsArray = response.getJSONArray(ITEMS);
        for (int i = 0; i < jsonItemsArray.length(); i++) {
            JSONObject jsonObject = jsonItemsArray.getJSONObject(i);
            JSONObject jsonSnippetObject = jsonObject.getJSONObject(SNIPPET);
            JSONObject jsonIDObject = jsonObject.getJSONObject(ID);
            String id = jsonIDObject.getString(VIDEO_ID);
            String publishedDate = jsonSnippetObject.getString(PUBLISHED);
            String thumbnail = jsonSnippetObject.getJSONObject(THUMBNAILS).getJSONObject(IMAGE_MEDIUM).getString(IMAGE_URL);
            String title = jsonSnippetObject.getString(TITLE);
            String channelTitle = jsonSnippetObject.getString(CHANNEL);
            String description = jsonSnippetObject.getString(DESCRIPTION);

            Video video = new Video();
            video.setId(id);
            video.setDescription(description);
            video.setChannel(channelTitle);
            video.setThumbnails(thumbnail);
            video.setTitle(title);
            video.setPublishedDate(publishedDate);

            videoList.add(video);
        }
        return videoList;
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        Log.i(TAG, fragment.toString());
        super.onAttachFragment(fragment);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private String handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            return intent.getStringExtra(SearchManager.QUERY);
        }
        return null;
    }

    @Override
    public void itemClicked(View view, int position, Video video) {
//        FragmentManager fm = getSupportFragmentManager();
//        FormatList dialogFragment = new FormatList();
//        Bundle bundle = new Bundle();
//        bundle.putString("id", id);
//        dialogFragment.setArguments(bundle);
//        dialogFragment.setCommunicator((Communicate) MainActivity.ct);
//        dialogFragment.show(fm, "DownloadDialog");
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                view.findViewById(R.id.videoThumbnail),view.findViewById(R.id.videoThumbnail).getTransitionName());
        Intent intent = new Intent(this, VideoInfo.class);
        intent.putExtra("Video",video);
        startActivity(intent,compat.toBundle());
    }

    public String urlEncode(String string) {
        try {
            return URLEncoder.encode(string,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

}
