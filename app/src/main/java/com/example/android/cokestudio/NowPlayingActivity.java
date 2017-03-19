package com.example.android.cokestudio;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.cokestudio.models.Song;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

public class NowPlayingActivity extends AppCompatActivity {

    private static final String LOG_TAG = NowPlayingActivity.class.getSimpleName();

    private Song mSong;

    private ImageView mAlbumCover;
    private TextView mSongName;
    private TextView mArtistName;
    private ImageView mPlayPause;
    private ImageView mStop;
    private ImageView mForward;
    private ImageView mRewind;
    private SeekBar mSeekBarProgress;

    private MediaPlayer mMediaPlayer;
    private final Handler mHandler = new Handler();
    private int mediaFileLengthInMilliseconds;
    private final int mSkipTime = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

        mSong = (Song) getIntent().getSerializableExtra("song");

        hookUpNowPlayingUI(NowPlayingActivity.this);
    }

    /**
     * Helper Method to set up the UI elements
     */
    private void hookUpNowPlayingUI(final Context context){

        mAlbumCover = (ImageView) findViewById(R.id.iv_album_cover);
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mAlbumCover.setImageDrawable(context.getDrawable(R.drawable.ic_audiotrack_black_24dp));
                }
                else
                    mAlbumCover.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_audiotrack_black_24dp));
            }
        });
        builder.downloader(new OkHttpDownloader(context));
        builder.build().load(mSong.getCoverImageUrl()).into(mAlbumCover);

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mSongName = (TextView) findViewById(R.id.tv_song_name);
        mSongName.setText(mSong.getSong());

        mArtistName = (TextView) findViewById(R.id.tv_artists_name);
        mArtistName.setText(mSong.getArtists());

        mSeekBarProgress = (SeekBar)findViewById(R.id.seekbar);
        mSeekBarProgress.setMax(99);
        mSeekBarProgress.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(v.getId() == R.id.seekbar){
                    if(mMediaPlayer.isPlaying()){
                        SeekBar sb = (SeekBar)v;
                        int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();
                        mMediaPlayer.seekTo(playPositionInMillisecconds);
                    }
                }
                return false;
            }
        });

        mPlayPause = (ImageView)findViewById(R.id.iv_play_pause);
        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playOrPauseMusic();
            }
        });

        mStop = (ImageView)findViewById(R.id.iv_stop);
        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mForward = (ImageView) findViewById(R.id.iv_forward);
        mForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forwardMusic();
            }
        });

        mRewind = (ImageView) findViewById(R.id.iv_rewind);
        mRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rewindMusic();
            }
        });

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            }
        });

        mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                mSeekBarProgress.setSecondaryProgress(percent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mMediaPlayer.setDataSource(mSong.getUrl());
            mMediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.now_playing_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.i_favourite:
                Toast.makeText(NowPlayingActivity.this, "Favourite functionality to be implemented", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.i_download:
                Toast.makeText(NowPlayingActivity.this, "Download functionality to be implemented", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Helper Method to play OR pause music
     */
    private void playOrPauseMusic() {
        /*
        try {
            mMediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        */

        mediaFileLengthInMilliseconds = mMediaPlayer.getDuration();

        if(!mMediaPlayer.isPlaying()){
            mMediaPlayer.start();
            mPlayPause.setImageResource(R.drawable.ic_pause_black_24dp);
        }else {
            mMediaPlayer.pause();
            mPlayPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        }

        primarySeekBarProgressUpdater();
    }

    /**
     * Helper Method to forward music
     */
    private void forwardMusic() {

        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            int newPosition = mMediaPlayer.getCurrentPosition() + mSkipTime;
            if(newPosition < mediaFileLengthInMilliseconds)
                mMediaPlayer.seekTo(newPosition);
        }
    }

    /**
     * Helper Method to rewind music
     */
    private void rewindMusic() {

        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            int newPosition = mMediaPlayer.getCurrentPosition() - mSkipTime;
            if(newPosition > 0)
                mMediaPlayer.seekTo(newPosition);
        }
    }

    /**
     * Helper method to show SeekBar Progress
     */
    private void primarySeekBarProgressUpdater() {

        if(mMediaPlayer != null) {
            mSeekBarProgress.setProgress((int) (((float) mMediaPlayer.getCurrentPosition() / mediaFileLengthInMilliseconds) * 100));
            if (mMediaPlayer.isPlaying()) {
                Runnable notification = new Runnable() {
                    public void run() {
                        primarySeekBarProgressUpdater();
                    }
                };
                mHandler.postDelayed(notification, 1000);
            }
        }
    }
}
