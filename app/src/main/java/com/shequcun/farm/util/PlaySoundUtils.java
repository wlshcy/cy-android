package com.shequcun.farm.util;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by mac on 15/10/8.
 */
public class PlaySoundUtils {

    public static void doPlay(Activity mAct, int resouceId) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        AssetFileDescriptor file = mAct.getResources().openRawResourceFd(
                resouceId);
        try {
            mediaPlayer.setDataSource(file.getFileDescriptor(),
                    file.getStartOffset(), file.getLength());
            file.close();
            mediaPlayer.prepare();
        } catch (IOException ioe) {
            //Log.w(TAG, ioe);
            mediaPlayer = null;
            return;
        }

        mediaPlayer.start();

        mediaPlayer
                .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer player) {
                        player.seekTo(0);
                        player.release();
                    }
                });
    }
}
