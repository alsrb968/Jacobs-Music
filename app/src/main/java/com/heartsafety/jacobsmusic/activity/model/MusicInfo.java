package com.heartsafety.jacobsmusic.activity.model;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;

public class MusicInfo implements Serializable {
    private String id;
    private String albumId;
    private String title;
    private String artist;
    private String album;
    private int duration;

    public MusicInfo() {
    }

    public MusicInfo(Cursor cursor) {
        setId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
        setAlbumId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
        setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
        setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
        setDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
    }

    public MusicInfo(String id, String albumId, String title, String artist, String album, int duration) {
        this.id = id;
        this.albumId = albumId;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "MusicInfo{" +
                "id='" + id + '\'' +
                ", albumId='" + albumId + '\'' +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", duration=" + duration +
                '}';
    }
}