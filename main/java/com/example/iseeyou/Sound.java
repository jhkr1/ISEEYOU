package com.example.iseeyou;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;
import android.util.Log;

public class Sound {
    static SoundPool soundPool = null;
    static int success; //인식 성공 효과음 파일 이름
    static int failure; //인식 실패 효과음 파일 이름
    static int ing; //진행 중 효과음 파일 이름
    static int btnSound; //일반 버튼 효과음 파일 이름

    static Vibrator vibrator;

    static AudioManager audioManager = null;
    static AudioAttributes audioAttributes = null;
    static boolean init = false;
    static public void play(int i){
        switch (i){
            case 0:
                soundPool.play(success, audioManager
                        .getStreamVolume(AudioManager.STREAM_MUSIC), audioManager
                        .getStreamVolume(AudioManager.STREAM_MUSIC), 1, 0, 1f);
                break;
            case 1:
                soundPool.play(failure, audioManager
                        .getStreamVolume(AudioManager.STREAM_MUSIC), audioManager
                        .getStreamVolume(AudioManager.STREAM_MUSIC), 1, 1, 1f);
                break;
            case 2:
                soundPool.play(ing, audioManager
                        .getStreamVolume(AudioManager.STREAM_MUSIC), audioManager
                        .getStreamVolume(AudioManager.STREAM_MUSIC), 0, 0, 1f);
                break;
            case 3:
                soundPool.play(btnSound, audioManager
                        .getStreamVolume(AudioManager.STREAM_MUSIC), audioManager
                        .getStreamVolume(AudioManager.STREAM_MUSIC), 1, 0, 1f);


        }

    }
}
