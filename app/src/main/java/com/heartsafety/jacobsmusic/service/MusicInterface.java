package com.heartsafety.jacobsmusic.service;

public interface MusicInterface {
    void start(int position);
    void play();
    void pause();
    void prev();
    void next();
    void seekTo(int ms);
    int getPosition();
    boolean isPlaying();
}
