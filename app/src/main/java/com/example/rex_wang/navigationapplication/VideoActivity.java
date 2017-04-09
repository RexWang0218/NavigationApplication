package com.example.rex_wang.navigationapplication;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {
    private final String TAG = "--- 系統測試 VideoActivity ---";
    private MediaController mMediaControl;
    private VideoView mVideoView;
    int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Log.d(TAG, " onCreate ");

        // 全螢幕設置
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 保持不休眠
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


//        onDestroy -> onCreate
//        if (savedInstanceState != null) {
//            pos = savedInstanceState.getInt("pos", 0);
//        }

        mMediaControl = new MediaController(this);
        mVideoView = (VideoView) findViewById(R.id.vv_video);

        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, " onPause ");
        if (mVideoView.isPlaying()) {
            pos = mVideoView.getCurrentPosition();
            mVideoView.stopPlayback();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, " onResume ");
        mVideoView.seekTo(pos);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, " onDestroy ");
        super.onDestroy();
    }

//    將onDestroy
//    @Override
//    protected void onSaveInstanceState(Bundle bundle) {
//        super.onSaveInstanceState(bundle);
//        Log.d(TAG, " onSaveInstanceState ");
//        bundle.putInt("pos", pos);
//    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d(TAG, " landscape ");
        } else if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d(TAG, " portrait ");
        }
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
        }
    }

    private void init() {
        Log.d(TAG, " init ");
        mMediaControl.setAnchorView(mVideoView);
        mVideoView.setMediaController(mMediaControl);

//        String audioFile = "android.resource://" + getPackageName() + "/" + R.raw.sample;
        Intent it = this.getIntent();
        String audioFile = null;
        if (it.getStringExtra("url") != null) {
            audioFile = it.getStringExtra("url");
        }

        mVideoView.setVideoURI(Uri.parse(audioFile));
        mVideoView.requestFocus();

        mVideoView.start();
    }

}
