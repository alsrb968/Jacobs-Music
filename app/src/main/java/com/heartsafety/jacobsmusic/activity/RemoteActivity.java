package com.heartsafety.jacobsmusic.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.heartsafety.jacobsmusic.R;
import com.heartsafety.jacobsmusic.activity.model.MusicInfo;
import com.heartsafety.jacobsmusic.databinding.ActivityRemoteBinding;
import com.heartsafety.jacobsmusic.service.MusicCallbackInterface;
import com.heartsafety.jacobsmusic.service.MusicInterface;
import com.heartsafety.jacobsmusic.service.MusicService;
import com.heartsafety.jacobsmusic.util.Log;
import com.heartsafety.jacobsmusic.util.MusicUtils;

import java.util.ArrayList;

public class RemoteActivity extends AppCompatActivity implements MusicInterface, MusicCallbackInterface {
    private ActivityRemoteBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindService(new Intent(this, MusicService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_remote);
        mBinding.setOnClickHandler(new OnClickHandler());
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            mService = binder.getService(RemoteActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    private MusicService mService;

    private MusicService getService() {
        if (mService == null) {
            Log.w("Service is null!");
        }
        return mService;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConnection);
        }
    }

    public class OnClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.equals(mBinding.play)) {
                play();
            } else if (v.equals(mBinding.pause)) {
                pause();
            } else if (v.equals(mBinding.pre)) {
                prev();
            } else if (v.equals(mBinding.next)) {
                next();
            }
        }
    }

    @Override
    public void start(int position) {
        getService().start(position);
    }

    @Override
    public void play() {
        getService().play();
    }

    @Override
    public void pause() {
        getService().pause();
    }

    @Override
    public void prev() {
        getService().prev();
    }

    @Override
    public void next() {
        getService().next();
    }

    @Override
    public void seekTo(int ms) {
        getService().seekTo(ms);
    }

    @Override
    public int getPosition() {
        return getService().getPosition();
    }

    @Override
    public boolean isPlaying() {
        if (mService == null) {
            return false;
        }
        return mService.isPlaying();
    }

    @Override
    public void onPlayState(int state) {
        mBinding.setPlayState(state);
    }

    @Override
    public void onPlayTime(int time) {

    }

    @Override
    public void onPosition(int position) {

    }

    @Override
    public void onMusicListInfo(ArrayList<MusicInfo> list) {

    }

    @Override
    public void onMusicCurrentInfo(MusicInfo info) {
        String albumId = info.getAlbumId();
        if (albumId != null && albumId.length() > 0) {
            Bitmap bitmap = MusicUtils.getAlbumImage(this, info.getAlbumId());
            Glide.with(this)
                    .load(bitmap)
                    .placeholder(R.drawable.me_bg_default_nor)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                    .into(mBinding.album);
        }
    }
}