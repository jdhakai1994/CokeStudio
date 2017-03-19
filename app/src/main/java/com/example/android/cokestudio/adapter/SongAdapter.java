package com.example.android.cokestudio.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.cokestudio.R;
import com.example.android.cokestudio.models.Song;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Jayabrata Dhakai on 3/18/2017.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private static final String LOG_TAG = SongAdapter.class.getSimpleName();

    private List<Song> mDataset;
    private Context mContext;
    final private ListItemClickListener mOnClickListener;


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView cover;
        public TextView song;
        public TextView artists;

        public ViewHolder(View itemView) {
            super(itemView);
            cover = (ImageView) itemView.findViewById(R.id.iv_cover);
            song = (TextView) itemView.findViewById(R.id.tv_song);
            artists = (TextView) itemView.findViewById(R.id.tv_artists);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Song song = mDataset.get(getAdapterPosition());
            mOnClickListener.onClick(song);
        }
    }

    public interface ListItemClickListener{
        void onClick(Song object);
    }

    public SongAdapter(Context context, ListItemClickListener listener) {
        mContext = context;
        mOnClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Song object = mDataset.get(position);
        String imageUrl = object.getCoverImageUrl();
        Picasso.Builder builder = new Picasso.Builder(mContext);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.cover.setImageDrawable(mContext.getDrawable(R.drawable.ic_audiotrack_black_24dp));
                }
            }
        });
        builder.downloader(new OkHttpDownloader(mContext));
        builder.build().load(imageUrl).into(holder.cover);

        holder.song.setText(object.getSong());
        holder.artists.setText(object.getArtists());
    }

    @Override
    public int getItemCount() {
        if(mDataset == null)
            return 0;
        return mDataset.size();
    }

    public void setSongData(List<Song> songList){
        mDataset = songList;
        notifyDataSetChanged();
    }
}
