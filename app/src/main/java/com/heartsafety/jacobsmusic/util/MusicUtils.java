package com.heartsafety.jacobsmusic.util;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

public class MusicUtils {
    public static class Page {
        public static final int LIST = 0;
        public static final int PLAYER = 1;
    }

    public static class PlayState {
        public static final int PLAY = 0;
        public static final int PAUSE = 1;
    }

    public static class Pref {
        public static final String ID = "id";
        public static final String ALBUM_ID = "album_id";
        public static final String TITLE = "title";
        public static final String ARTIST = "artist";
        public static final String ALBUM = "album";
        public static final String POSITION = "position";
        public static final String PLAY_STATE = "play_state";
        public static final String PLAY_TIME = "play_time";
        public static final String TOTAL_TIME = "total_time";
    }

    public static Bitmap getAlbumImage(Context context, String album_id) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // NOTE: There is in fact a 1 pixel frame in the ImageView used to
        // display this drawable. Take it into account now, so we don't have to
        // scale later.
        ContentResolver res = context.getContentResolver();
        Uri uri = Uri.parse("content://media/external/audio/albumart/" + album_id);
        if (uri != null) {
            ParcelFileDescriptor fd = null;
            try {
                fd = res.openFileDescriptor(uri, "r");

                return BitmapFactory.decodeFileDescriptor(
                        fd.getFileDescriptor(), null, options);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fd != null)
                        fd.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String getTimeString(int millis) {
        StringBuilder buf = new StringBuilder();

        int hours = millis / (1000 * 60 * 60);
        int minutes = (millis % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = ((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000;

        if (hours > 0) {
            buf.append(String.format(Locale.KOREA, "%d", hours)).append(":");
        }
        buf.append(String.format(Locale.KOREA, "%d", minutes))
                .append(":")
                .append(String.format(Locale.KOREA, "%02d", seconds));

        return buf.toString();
    }
}
