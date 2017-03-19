package com.example.android.cokestudio;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.android.cokestudio.adapter.SongAdapter;
import com.example.android.cokestudio.models.Song;
import com.example.android.cokestudio.utilities.JSONUtils;
import com.example.android.cokestudio.utilities.NetworkUtils;

import java.util.List;

public class PlaylistActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Song>>, SongAdapter.ListItemClickListener{

    private static final String LOG_TAG = PlaylistActivity.class.getSimpleName();

    public final int FETCH_SONG_ID = 1;

    private RecyclerView mRecyclerView;
    private TextView mEmptyView;
    private SongAdapter mSongAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_main_ui);
        mEmptyView = (TextView) findViewById(R.id.tv_empty_view);
        mLayoutManager = new LinearLayoutManager(PlaylistActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mSongAdapter = new SongAdapter(PlaylistActivity.this, this);
        mRecyclerView.setAdapter(mSongAdapter);

        LoaderManager.LoaderCallbacks<List<Song>> loaderCallback = PlaylistActivity.this;

        // if the device is not connected to internet change the text of the empty view
        if(!isConnectedToInternet()) {
            mEmptyView.setText(getString(R.string.no_internet));
        } else{
            // Initialise the custom loader
            getSupportLoaderManager().initLoader(FETCH_SONG_ID, null, loaderCallback);
        }
    }

    @Override
    public void onClick(Song object) {
        Intent songIntent = new Intent(PlaylistActivity.this, NowPlayingActivity.class);
        songIntent.putExtra("song", object);
        startActivity(songIntent);
    }

    @Override
    public Loader<List<Song>> onCreateLoader(int id, Bundle args) {
        switch (id){
            case FETCH_SONG_ID:
                return new AsyncTaskLoader<List<Song>>(PlaylistActivity.this) {
                    @Override
                    protected void onStartLoading() {
                        forceLoad();
                    }

                    @Override
                    public List<Song> loadInBackground() {
                        String JSONResponse = NetworkUtils.makeHTTPRequest();
                        return JSONUtils.parseJSON(JSONResponse);
                    }
                };
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<Song>> loader, List<Song> song) {
        if (song != null && !song.isEmpty())
            mSongAdapter.setSongData(song);
        else
            mEmptyView.setText(R.string.no_data_fetched);
    }

    @Override
    public void onLoaderReset(Loader<List<Song>> loader) {

    }

    /*
     * Helper method to check if the device is connected to the internet
     */
    public boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
