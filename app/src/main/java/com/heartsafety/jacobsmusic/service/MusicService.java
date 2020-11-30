package com.heartsafety.jacobsmusic.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;

import com.heartsafety.jacobsmusic.activity.MusicActivity;
import com.heartsafety.jacobsmusic.activity.model.MusicInfo;
import com.heartsafety.jacobsmusic.util.Log;
import com.heartsafety.jacobsmusic.util.MusicUtils;
import com.heartsafety.jacobsmusic.util.PreferenceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;


public class MusicService extends Service implements MusicInterface, MusicCallbackInterface {
    private MusicActivity mActivity;
    private final IBinder mBinder = new MusicBinder();

    public static ArrayList<MusicInfo> mList;

    private AudioManager mAudioManager;
    private MediaPlayer mMediaPlayer;

    private int mPosition = 0;
    private int mPlayState = MusicUtils.PlayState.PAUSE;

    public class MusicBinder extends Binder {
        public MusicService getService(Context context) {
            mActivity = (MusicActivity) context;
            mActivity.onMusicListInfo(mList);
            return MusicService.this;
        }
    }

    private MusicActivity getActivity() {
        if (mActivity == null) {
            Log.w("Activity is null!");
        }
        return mActivity;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getMusicList();

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(mp -> {
            next();
        });

        mProgressUpdate = new ProgressUpdate();
        mProgressUpdate.start();

        mPosition = PreferenceManager.getInt(this, MusicUtils.Pref.POSITION);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onPlayState(MusicUtils.PlayState.PAUSE);
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void getMusicList() {
        mList = new ArrayList<>();
        //가져오고 싶은 컬럼 명을 나열합니다. 음악의 아이디, 앰블럼 아이디, 제목, 아스티스트 정보를 가져옵니다.
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION
        };

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, orderBy(MediaStore.Audio.Media.TITLE));

        if (cursor == null) return;
        while (cursor.moveToNext()) {
            MusicInfo musicInfo = new MusicInfo(cursor);
//            Log.d(musicDto.toString());
            mList.add(musicInfo);
        }
        cursor.close();
    }

    private String orderBy(String keyString) {
        boolean isKorean = getResources().getConfiguration().locale.getLanguage().toLowerCase().equals(Locale.KOREA.getLanguage());
        return (isKorean ? ("CASE WHEN "+ keyString + " >= '가' AND " + keyString + " < '\uD7A4' THEN 1 ") : "CASE ") +
                "WHEN "+ keyString + " >= 'A' AND " + keyString + " < '[' THEN 2 " +
                "WHEN "+ keyString + " >= 'a' AND " + keyString + " < '{' THEN 3 " +
                "WHEN "+ keyString + " >= '0' AND " + keyString + " < ':' THEN 4 " +
                "ELSE 5 END ASC, " + keyString + " ASC";
    }

    private ProgressUpdate mProgressUpdate;

    private class ProgressUpdate extends Thread {
        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    Thread.sleep(500);
                    if (mMediaPlayer != null &&
                            mPlayState == MusicUtils.PlayState.PLAY) {
                        onPlayTime(mMediaPlayer.getCurrentPosition());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void requestAudioFocus() {
        AudioAttributes playbackAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        AudioFocusRequest focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(playbackAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener(mOnAudioFocusChangeListener)
                .build();
        mAudioManager.requestAudioFocus(focusRequest);
    }

    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = focusChange -> {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                play();
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                break;
        }
    };

    @Override
    public void start(int position) {
        mPosition = position;
        Uri uri = Uri.withAppendedPath(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                mList.get(position).getId());
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(this, uri);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        onPlayState(MusicUtils.PlayState.PLAY);
        onMusicCurrentInfo(mList.get(position));
        onPosition(position);
    }

    @Override
    public void play() {
        mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition());
        mMediaPlayer.start();
        onPlayState(MusicUtils.PlayState.PLAY);
    }

    @Override
    public void pause() {
        mMediaPlayer.pause();
        onPlayState(MusicUtils.PlayState.PAUSE);
    }

    @Override
    public void prev() {
        if (mMediaPlayer.getCurrentPosition() > 5000 ||
                mPosition == 0) {
            mMediaPlayer.seekTo(0);
        } else {
            start(--mPosition);
        }
    }

    @Override
    public void next() {
        if (mPosition + 1 < mList.size()) {
            start(++mPosition);
        }
    }

    @Override
    public void seekTo(int ms) {
        mMediaPlayer.seekTo(ms);
    }

    @Override
    public int getPosition() {
        return mPosition;
    }

    @Override
    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            mPlayState = mMediaPlayer.isPlaying() ? MusicUtils.PlayState.PLAY : MusicUtils.PlayState.PAUSE;
        }
        return mPlayState == MusicUtils.PlayState.PLAY;
    }

    @Override
    public void onPlayState(int state) {
        mPlayState = state;
        if (state == MusicUtils.PlayState.PLAY) {
            requestAudioFocus();
        }
        getActivity().onPlayState(state);
        PreferenceManager.setInt(this, MusicUtils.Pref.PLAY_STATE, state);
    }

    @Override
    public void onPlayTime(int time) {
        getActivity().onPlayTime(time);
        PreferenceManager.setInt(this, MusicUtils.Pref.PLAY_TIME, time);
    }

    @Override
    public void onPosition(int position) {
        Log.d(String.valueOf(position));
        getActivity().onPosition(position);
        PreferenceManager.setInt(this, MusicUtils.Pref.POSITION, position);
    }

    @Override
    public void onMusicListInfo(ArrayList<MusicInfo> list) {
        getActivity().onMusicListInfo(list);
    }

    @Override
    public void onMusicCurrentInfo(MusicInfo info) {
        getActivity().onMusicCurrentInfo(info);
        PreferenceManager.setString(this, MusicUtils.Pref.ID, info.getId());
        PreferenceManager.setString(this, MusicUtils.Pref.ALBUM_ID, info.getAlbumId());
        PreferenceManager.setString(this, MusicUtils.Pref.TITLE, info.getTitle());
        PreferenceManager.setString(this, MusicUtils.Pref.ARTIST, info.getArtist());
        PreferenceManager.setString(this, MusicUtils.Pref.ALBUM, info.getAlbum());
        PreferenceManager.setInt(this, MusicUtils.Pref.TOTAL_TIME, info.getDuration());
    }
}