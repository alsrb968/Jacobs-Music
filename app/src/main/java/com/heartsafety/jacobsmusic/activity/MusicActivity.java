package com.heartsafety.jacobsmusic.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.heartsafety.jacobsmusic.R;
import com.heartsafety.jacobsmusic.activity.fragment.BaseFragment;
import com.heartsafety.jacobsmusic.activity.fragment.ListFragment;
import com.heartsafety.jacobsmusic.activity.fragment.PlayerFragment;
import com.heartsafety.jacobsmusic.activity.model.MusicDto;
import com.heartsafety.jacobsmusic.databinding.ActivityMusicBinding;
import com.heartsafety.jacobsmusic.service.MusicCallbackInterface;
import com.heartsafety.jacobsmusic.service.MusicInterface;
import com.heartsafety.jacobsmusic.service.MusicService;
import com.heartsafety.jacobsmusic.util.Log;
import com.heartsafety.jacobsmusic.util.MusicUtils;

import java.util.ArrayList;

public class MusicActivity extends AppCompatActivity implements MusicInterface, MusicCallbackInterface {
    private ActivityMusicBinding mBinding;
    private ArrayList<MusicDto> mList;
    private MusicDto mMusicDto;
    private int mTotalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        bindService(new Intent(this, MusicService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_music);

        switchFragment(MusicUtils.Page.LIST, 0);

    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            mService = binder.getService(MusicActivity.this);
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

    @Override
    public void onBackPressed() {
        mFragment.onBackPressed();
    }

    private BaseFragment mFragment;
    private int mPage = -1;

    public void switchFragment(int page, int position) {
        Log.d("page: " + page + ", position: " + position);
        if (mPage == page) {
            Log.w("duplicated page!");
            return;
        }
        mPage = page;
        BaseFragment fragment;
        switch (page) {
            case MusicUtils.Page.LIST:
                fragment = ListFragment.newInstance(position);
                break;
            case MusicUtils.Page.PLAYER:
                fragment = PlayerFragment.newInstance(position);
                break;
            default:
                return;
        }

        mFragment = fragment;

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, 0)
                .replace(mBinding.fragmentLayout.getId(), fragment)
                .commit();
    }

    private static final String[] permission_list = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public void checkPermission(){
        //현재 안드로이드 버전이 6.0미만이면 메서드를 종료한다.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;

        for (String permission : permission_list){
            //권한 허용 여부를 확인한다.
            int chk = checkCallingOrSelfPermission(permission);

            if (chk == PackageManager.PERMISSION_DENIED){
                //권한 허용을여부를 확인하는 창을 띄운다
                requestPermissions(permission_list,0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            for (int grantResult : grantResults) {
                //허용됬다면
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "앱권한설정하세요", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    public ArrayList<MusicDto> getMusicListInfo() {
        return mList;
    }

    public MusicDto getMusicCurrentInfo() {
        return mMusicDto;
    }

    public int getTotalTime() {
        return mTotalTime;
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
        return getService().isPlaying();
    }

    @Override
    public void onPlayState(int state) {
        mFragment.onPlayState(state);
    }

    @Override
    public void onPlayTime(int time) {
        mFragment.onPlayTime(time);
    }

    @Override
    public void onTotalTime(int time) {
        mTotalTime = time;
        mFragment.onTotalTime(time);
    }

    @Override
    public void onPosition(int position) {
        mFragment.onPosition(position);
    }

    @Override
    public void onMusicListInfo(ArrayList<MusicDto> list) {
        mList = list;
        mFragment.onMusicListInfo(list);
    }

    @Override
    public void onMusicCurrentInfo(MusicDto info) {
        mMusicDto = info;
        mFragment.onMusicCurrentInfo(info);
    }
}