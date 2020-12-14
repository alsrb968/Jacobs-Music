package com.heartsafety.jacobsmusic.util;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.heartsafety.jacobsmusic.activity.model.MusicInfo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
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

    public static class KoreanChar {

        public static final int CHOSEONG_COUNT = 19;
        public static final int JUNGSEONG_COUNT = 21;
        public static final int JONGSEONG_COUNT = 28;
        public static final int HANGUL_SYLLABLE_COUNT = CHOSEONG_COUNT * JUNGSEONG_COUNT * JONGSEONG_COUNT;
        public static final int HANGUL_SYLLABLES_BASE = 0xAC00;
        public static final int HANGUL_SYLLABLES_END = HANGUL_SYLLABLES_BASE + HANGUL_SYLLABLE_COUNT;

        public static final int[] COMPAT_CHOSEONG_MAP = new int[]{
                0x3131, 0x3132, 0x3134, 0x3137, 0x3138, 0x3139, 0x3141, 0x3142, 0x3143, 0x3145,
                0x3146, 0x3147, 0x3148, 0x3149, 0x314A, 0x314B, 0x314C, 0x314D, 0x314E
        };

        public KoreanChar() {
            // Can never be instantiated.
        }


        public static boolean isSyllable(char c) {
            return HANGUL_SYLLABLES_BASE <= c && c < HANGUL_SYLLABLES_END;
        }

        public static char getCompatChoseong(char value) {
            if (!isSyllable(value))
                return '\0';

            final int choseongIndex = getChoseongIndex(value);
            return (char) COMPAT_CHOSEONG_MAP[choseongIndex];
        }

        public static int getChoseongIndex(char syllable) {
            final int syllableIndex = syllable - HANGUL_SYLLABLES_BASE;
            return syllableIndex / (JUNGSEONG_COUNT * JONGSEONG_COUNT);
        }
    }

    public static class OrderByComparator {
        private static final int LEFT_FIRST = -1;
        private static final int RIGHT_FIRST = 1;

        public static Comparator<String> getComparator(Context context, boolean folderPath) {
            return (left, right) -> compare(left, right, context.getResources().getConfiguration().locale.getLanguage().toLowerCase().equals("ko"), folderPath);
        }

        public static Comparator<MusicInfo> getComparator2(Context context, boolean folderPath) {
            return (o1, o2) -> compare(o1.getTitle(), o2.getTitle(), context.getResources().getConfiguration().locale.getLanguage().toLowerCase().equals("ko"), folderPath);
        }

        private static int compare(String left, String right, boolean isKorean, boolean isFolderPath) {
            if (isFolderPath) {
                left.substring(left.lastIndexOf("/") + 1);
                right.substring(right.lastIndexOf("/") + 1);
            }
            left = left.replaceAll(" ", "");
            right = right.replaceAll(" ", "");
            int leftLen = left.length();
            int rightLen = right.length();
            int minLen = Math.min(leftLen, rightLen);
            for (int index = 0; index < minLen; index++) {
                char leftChar = left.charAt(index);
                char rightChar = right.charAt(index);
                if (leftChar == rightChar) {
                    continue;
                } else if (isKorean && isKorean(leftChar) && !isKorean(rightChar)) {
                    return LEFT_FIRST;
                } else if (isKorean && !isKorean(leftChar) && isKorean(rightChar)) {
                    return RIGHT_FIRST;
                } else if (isEnglish(leftChar) && !isEnglish(rightChar)) {
                    return LEFT_FIRST;
                } else if (!isEnglish(leftChar) && isEnglish(rightChar)) {
                    return RIGHT_FIRST;
                } else if (isUpper(leftChar) && isLower(rightChar)) {
                    return LEFT_FIRST;
                } else if (isLower(leftChar) && isUpper(rightChar)) {
                    return RIGHT_FIRST;
                } else if (isNumber(leftChar) && !isNumber(rightChar)) {
                    return LEFT_FIRST;
                } else if (!isNumber(leftChar) && isNumber(rightChar)) {
                    return RIGHT_FIRST;
                } else if (leftChar < rightChar) {
                    return LEFT_FIRST;
                } else if (leftChar > rightChar) {
                    return RIGHT_FIRST;
                }
            }
            if (leftLen <= rightLen) {
                return LEFT_FIRST;
            } else {
                return RIGHT_FIRST;
            }
        }

        public static boolean isKorean(char ch) {
            return (ch >= Integer.parseInt("AC00", 16) && ch <= Integer.parseInt("D7A3", 16));
        }

        private static boolean isEnglish(char ch) {
            return isUpper(ch) || isLower(ch);
        }

        private static boolean isUpper(char ch) {
            return (ch >= (int) 'A' && ch <= (int) 'Z');
        }

        private static boolean isLower(char ch) {
            return (ch >= (int) 'a' && ch <= (int) 'z');
        }

        private static boolean isNumber(char ch) {
            return (ch >= (int) '0' && ch <= (int) '9');
        }
    }

    // ----------
    // Charset functions
    private static String getCharset(String s) {
        byte[] BOM = s.getBytes();
        if (4 <= BOM.length) {
            if (((BOM[0] & 0xFF) == 0xEF) && ((BOM[1] & 0xFF) == 0xBB) && ((BOM[2] & 0xFF) == 0xBF)) {
                return "UTF-8";
            } else if (((BOM[0] & 0xFF) == 0xFE) && ((BOM[1] & 0xFF) == 0xFF)) {
                return "UTF-16BE";
            } else if (((BOM[0] & 0xFF) == 0xFF) && ((BOM[1] & 0xFF) == 0xFE)) {
                return "UTF-16LE";
            } else if (((BOM[0] & 0xFF) == 0x00) && ((BOM[1] & 0xFF) == 0x00) && ((BOM[0] & 0xFF) == 0xFE) && ((BOM[1] & 0xFF) == 0xFF)) {
                return "UTF-32BE";
            } else if (((BOM[0] & 0xFF) == 0xFF) && ((BOM[1] & 0xFF) == 0xFE) && ((BOM[0] & 0xFF) == 0x00) && ((BOM[1] & 0xFF) == 0x00)) {
                return "UTF-32LE";
            } else {
                String convertStr;
                try {
                    convertStr = new String(s.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.ISO_8859_1);
                    if (s.equals(convertStr)) {
                        if(check_ksc_string(s))
                            return "MBCS"; // workaround
                        return "ISO-8859-1";
                    }
                    convertStr = new String(s.getBytes("EUC-KR"), "EUC-KR");
                    if (s.equals(convertStr)) {
                        return "EUC-KR";
                    }
                    convertStr = new String(s.getBytes("KSC5601"), "KSC5601");
                    if (s.equals(convertStr)) {
                        return "KSC5601";
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return "NONE";
    }

    private static boolean check_ksc_string(String s) {
        boolean have_ksc = false;
        int c1 = 0;
        int c2 = 0;
        for (int i = 0; i < s.length(); i++) {
            c2 = (int)s.charAt(i);
            if ((c2 & 0xFF00) == 0xFF00) {
                //	not MBCS, but UNICODE
                break;
            }
            if ((c2 & 0xFF) < 0x80)
                continue;
            if (c1 == 0) {
                c1 = c2;
                continue;
            } else {
                have_ksc = check_ksc_char((byte)c1, (byte)c2);
                c1 = 0;
                if (have_ksc)
                    break;
            }
        }
        return have_ksc;
    }

    private static boolean check_ksc_char(byte c1, byte c2) {
        if((0xA1 <= (c1 & 0xFF)) && (0xFE >= (c1 & 0xFF)) &&
                (0xA1 <= (c2 & 0xFF)) && (0xFE >= (c2 & 0xFF))) {
            return true;
        }
        return false;
    }

    public static String setCharset(String value) {
        if ((null != value) && !value.isEmpty()) {
            String charset = getCharset(value);
            if (charset.equals("MBCS")) {
                try {
                    value = new String(value.getBytes(), "UTF-8");
                    byte[] bytes = value.getBytes("ISO-8859-1");
                    value = new String(bytes, "EUC-KR");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else if (!charset.equals("NONE")) {
                try {
                    value = new String(value.getBytes(charset), charset);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }
}
