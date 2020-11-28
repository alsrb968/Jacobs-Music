package com.heartsafety.util;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MusicUtils {

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
}
