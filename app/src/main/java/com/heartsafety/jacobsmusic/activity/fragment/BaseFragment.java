package com.heartsafety.jacobsmusic.activity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.heartsafety.jacobsmusic.activity.MusicActivity;
import com.heartsafety.jacobsmusic.service.MusicCallbackInterface;
import com.heartsafety.jacobsmusic.service.MusicInterface;

public abstract class BaseFragment extends Fragment implements MusicInterface, MusicCallbackInterface {
    protected MusicActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MusicActivity) getActivity();
    }

    public abstract void onBackPressed();

    @Override
    public void start(int position) {
        mActivity.start(position);
    }

    @Override
    public void play() {
        mActivity.play();
    }

    @Override
    public void pause() {
        mActivity.pause();
    }

    @Override
    public void prev() {
        mActivity.prev();
    }

    @Override
    public void next() {
        mActivity.next();
    }

    @Override
    public void seekTo(int ms) {
        mActivity.seekTo(ms);
    }

    @Override
    public int getPosition() {
        return mActivity.getPosition();
    }

    @Override
    public boolean isPlaying() {
        return mActivity.isPlaying();
    }
}
