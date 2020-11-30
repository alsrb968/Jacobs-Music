package com.heartsafety.jacobsmusic.service;

import com.heartsafety.jacobsmusic.activity.model.MusicInfo;

import java.util.ArrayList;

public interface MusicCallbackInterface {
    void onPlayState(int state);
    void onPlayTime(int time);
    void onPosition(int position);
    void onMusicListInfo(ArrayList<MusicInfo> list);
    void onMusicCurrentInfo(MusicInfo info);
}
