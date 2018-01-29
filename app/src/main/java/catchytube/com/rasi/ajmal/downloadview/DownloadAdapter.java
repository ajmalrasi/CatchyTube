package catchytube.com.rasi.ajmal.downloadview;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import catchytube.com.rasi.ajmal.R;

/**
 * Created by kpajm on 02-04-2017.
 */

public class DownloadAdapter extends RecyclerView.Adapter<BodyViewHolder> implements View.OnClickListener {

    private Fragment dActivity;
    private ArrayList<DownloadListItems> dDownloadList;
    private LayoutInflater dInflater;
    private DownloadListener dListener;

    public DownloadAdapter(Fragment dActivity, DownloadListener dListener) {
        this.dActivity = dActivity ;
        this.dListener = dListener;
        dInflater = dActivity.getActivity().getLayoutInflater();
        dDownloadList = new ArrayList<>();
    }

    public ArrayList<DownloadListItems> getDownloadList() {
        return dDownloadList;
    }

    @Override
    public BodyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view  = dInflater.inflate(R.layout.list_download_item,parent,false);
        view.setOnClickListener(this);
        return new BodyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BodyViewHolder holder, int position) {
//        System.out.println(position+"----"+(getDownloadList().size()-position-1));
        holder.populate(dActivity.getActivity(),dDownloadList.get(getDownloadList().size()-position-1));
    }

    @Override
    public int getItemCount() {
        return dDownloadList.size();
    }

    @Override
    public void onClick(View view) {
        if(view.getTag() instanceof  DownloadListItems){
            DownloadListItems downloadListItems = (DownloadListItems) view.getTag();
            dListener.OnDownloadListClicked(downloadListItems);
        }
    }

    public interface DownloadListener{
        void OnDownloadListClicked(DownloadListItems downloadListItems);
    }
}
