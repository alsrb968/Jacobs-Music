package com.heartsafety.jacobsmusic.activity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.heartsafety.jacobsmusic.activity.MusicActivity;
import com.heartsafety.jacobsmusic.activity.model.MusicInfo;
import com.heartsafety.jacobsmusic.service.MusicCallbackInterface;
import com.heartsafety.jacobsmusic.service.MusicInterface;
import com.heartsafety.jacobsmusic.util.MusicUtils;
import com.heartsafety.jacobsmusic.util.PreferenceManager;

public abstract class BaseFragment extends Fragment implements MusicInterface, MusicCallbackInterface {
    protected MusicActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MusicActivity) getActivity();
    }

    public abstract void onBackPressed();

    public MusicInfo getMusicInfo() {
        return new MusicInfo(
                PreferenceManager.getString(mActivity, MusicUtils.Pref.ID),
                PreferenceManager.getString(mActivity, MusicUtils.Pref.ALBUM_ID),
                PreferenceManager.getString(mActivity, MusicUtils.Pref.TITLE),
                PreferenceManager.getString(mActivity, MusicUtils.Pref.ARTIST),
                PreferenceManager.getString(mActivity, MusicUtils.Pref.ALBUM),
                PreferenceManager.getInt(mActivity, MusicUtils.Pref.TOTAL_TIME));
    }

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
