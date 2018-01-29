package catchytube.com.rasi.ajmal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;
import catchytube.com.rasi.ajmal.R;
import catchytube.com.rasi.ajmal.interfaces.ClickLayoutItem;
import catchytube.com.rasi.ajmal.modal.Video;
import catchytube.com.rasi.ajmal.network.VolleySingleton;

/**
 * Created by kpajm on 05-04-2017.
 */

public class PopularVideo extends RecyclerView.Adapter<PopularVideo.PopularViewHolder>{

    private ArrayList<Video> popularVideoList = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    private Context context;
    private ClickLayoutItem item;
    private static String TAG = "PopularVideo Adapter";

    public PopularVideo(Context context){

        layoutInflater=LayoutInflater.from(context);
        volleySingleton=VolleySingleton.getInstance();
        imageLoader=volleySingleton.getImageLoader();
        this.context = context;

    }

    public void setPopularVideoList(ArrayList<Video> popularVideoList){
        this.popularVideoList = popularVideoList;
        notifyItemRangeChanged(0,popularVideoList.size());
    }

    @Override
    public PopularViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.popular_video_item,parent,false);
        PopularViewHolder viewHolder = new PopularViewHolder(view);
        return viewHolder;
    }

    public void setClickListener(ClickLayoutItem item){
        this.item = item;
    }

    @Override
    public void onBindViewHolder(final PopularViewHolder holder, int position) {

        Video currentVideo = popularVideoList.get(position);
        holder.videoTitle.setText(currentVideo.getTitle());
        holder.duration.setText(currentVideo.getDuration());
        String urlThumb = currentVideo.getThumbnails();
        imageLoader.get(urlThumb, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                holder.videoThumbnail.setImageBitmap(response.getBitmap());
            }
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return popularVideoList.size();
    }

    class PopularViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView videoThumbnail;
        private TextView videoTitle,duration;

        public  PopularViewHolder(View itemView){
            super(itemView);

            itemView.setOnClickListener(this);
            videoThumbnail = (ImageView) itemView.findViewById(R.id.videoThumbnail);
            videoTitle = (TextView) itemView.findViewById(R.id.videoTitle);
            duration = (TextView) itemView.findViewById(R.id.duration);

        }

        @Override
        public void onClick(View v) {
            if (item!=null){
//                item.itemClicked(v,getAdapterPosition(),popularVideoList.get(getAdapterPosition()).getId());
                item.itemClicked(v,getAdapterPosition(),popularVideoList.get(getAdapterPosition()));
            }

        }
    }

}
