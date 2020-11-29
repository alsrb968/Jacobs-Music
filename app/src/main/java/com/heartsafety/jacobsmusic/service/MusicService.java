package com.heartsafety.jacobsmusic.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;

import com.heartsafety.jacobsmusic.activity.MusicActivity;
import com.heartsafety.jacobsmusic.activity.model.MusicDto;
import com.heartsafety.jacobsmusic.util.Log;
import com.heartsafety.jacobsmusic.util.MusicUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;


public class MusicService extends Service implements MusicInterface, MusicCallbackInterface {
    private MusicActivity mActivity;
    private final IBinder mBinder = new MusicBinder();

    public static ArrayList<MusicDto> mList;

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

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getMusicList();

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(mp -> {
            next();
        });

        mProgressUpdate = new ProgressUpdate();
        mProgressUpdate.start();
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
        };

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, orderBy(MediaStore.Audio.Media.TITLE));

        while (cursor.moveToNext()) {
            MusicDto musicDto = new MusicDto();
            musicDto.setId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
            musicDto.setAlbumId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
            musicDto.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            musicDto.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
//            Log.d(musicDto.toString());
            mList.add(musicDto);
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

    private MusicActivity getActivity() {
        if (mActivity == null) {
            Log.w("Activity is null!");
        }
        return mActivity;
    }

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
        onTotalTime(mMediaPlayer.getDuration());
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
    public void onPlayState(int state) {
        mPlayState = state;
        getActivity().onPlayState(state);
    }

    @Override
    public void onPlayTime(int time) {
        getActivity().onPlayTime(time);
    }

    @Override
    public void onTotalTime(int time) {
        getActivity().onTotalTime(time);
    }

    @Override
    public void onMusicListInfo(ArrayList<MusicDto> list) {
        getActivity().onMusicListInfo(list);
    }

    @Override
    public void onMusicCurrentInfo(MusicDto info) {
        getActivity().onMusicCurrentInfo(info);
    }

}