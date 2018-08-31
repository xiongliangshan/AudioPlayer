package com.xls.lib_ijkplayer;

import android.util.Log;

import java.util.Locale;

public class BaseAudioPlayer {

    public static final int AUDIO_STATUS_PLAYING = 0;
    public static final int AUDIO_STATUS_PAUSE = 1;
    public static final int AUDIO_STATUS_STOP = 2;

    public static final int MEDIA_PROGRESS = 3001;





    public static String generateTime(long position) {
        int totalSeconds = (int) (position / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        String result = null;
        if (hours > 0) {
            result =  String.format(Locale.US, "%02d:%02d:%02d", hours, minutes,
                    seconds).toString();
            Log.d("xls","hours>0, result = "+result);
        } else {
            result = String.format(Locale.US, "%02d:%02d", minutes, seconds)
                    .toString();
            Log.d("xls","hours<0, result = "+result);
        }

        return result;
    }




}
