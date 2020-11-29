package com.heartsafety.jacobsmusic.service;

import com.heartsafety.jacobsmusic.activity.model.MusicDto;

import java.util.ArrayList;

public interface MusicCallbackInterface {
    void onPlayState(int state);
    void onPlayTime(int time);
    void onTotalTime(int time);
    void onPosition(int position);
    void onMusicListInfo(ArrayList<MusicDto> list);
    void onMusicCurrentInfo(MusicDto info);
}
