package com.example.iseeyou;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class Setting extends AppCompatActivity {

    ImageView thembtn;
    ImageView soundbtn;
    ImageButton backbtn;

    static Drawable drawable;
    SeekBar seekBar;

    Fragment sound_frag;


    private long backKeyPressedTime = 0;


    static int back;

    int state = 0;
    static int nCurrentVolumn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(State.them)
        {
            setTheme(R.style.contrastTheme);
            Setting.drawable = getDrawable(R.drawable.setbtn2_c);

        }
        else
        {
            setTheme(R.style.notcontrastTheme);
            Setting.drawable = getDrawable(R.drawable.setbtn2);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);




        Fragment see_frag = new seeFragment();
        sound_frag = new soundFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, see_frag).commitAllowingStateLoss();

        state = 0;
        backbtn = findViewById(R.id.back_btn);
        thembtn = findViewById(R.id.them_btn);
        soundbtn = findViewById(R.id.sound_btn);
        thembtn.setImageDrawable(drawable);

        TextView them_txt = findViewById(R.id.textView2);
        TextView sound_txt = findViewById(R.id.textView3);

        if(State.them)
        {
            them_txt.setTextColor(getResources().getColor(R.color.textColor));
            sound_txt.setTextColor(getResources().getColor(R.color.black));
        }
        else
        {
            them_txt.setTextColor(getResources().getColor(R.color.black));
            sound_txt.setTextColor(getResources().getColor(R.color.white));
        }



        back = getIntent().getExtras().getInt("activity");

        backbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Sound.play(3);
                Sound.vibrator.vibrate(State.vib);
                switch (back){
                    case 0:
                        Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent1);
                        break;
                    case 1:
                        Intent intent2 = new Intent(getApplicationContext(), Gallery.class);
                        startActivity(intent2);
                        break;
                    case 2:
                        Intent intent3 = new Intent(getApplicationContext(), Detail.class);
                        startActivity(intent3);
                }
                overridePendingTransition(0, 0);//인텐트 효과 없애기
                finish();
            }
        });

        thembtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view){
                state = 0;
                Sound.play(3);
                Sound.vibrator.vibrate(State.vib);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, see_frag).commitAllowingStateLoss();

                Sound.vibrator.vibrate(State.vib);

                thembtn.setImageDrawable(drawable);
                soundbtn.setImageResource(R.drawable.setstate);
                if(State.them)
                {
                    them_txt.setTextColor(getResources().getColor(R.color.textColor));
                    sound_txt.setTextColor(getResources().getColor(R.color.black));
                }
                else
                {
                    them_txt.setTextColor(getResources().getColor(R.color.black));
                    sound_txt.setTextColor(getResources().getColor(R.color.white));
                }

            }
        });

        soundbtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view){
                state = 1;
                Sound.play(3);
                Sound.vibrator.vibrate(State.vib);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, sound_frag).commitAllowingStateLoss();
                Sound.vibrator.vibrate(State.vib);
                soundbtn.setImageDrawable(drawable);
                thembtn.setImageResource(R.drawable.setstate);

                if(State.them)
                {
                    them_txt.setTextColor(getResources().getColor(R.color.black));
                    sound_txt.setTextColor(getResources().getColor(R.color.textColor));
                }
                else
                {
                    them_txt.setTextColor(getResources().getColor(R.color.white));
                    sound_txt.setTextColor(getResources().getColor(R.color.black));
                }

            }
        });



    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try{
            if(keyCode == KeyEvent.KEYCODE_BACK)
            {
                switch (back) {
                    case 0:
                        Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent1);
                        break;
                    case 1:
                        Intent intent2 = new Intent(getApplicationContext(), Gallery.class);
                        startActivity(intent2);
                        break;
                    case 2:
                        Intent intent3 = new Intent(getApplicationContext(), Detail.class);
                        startActivity(intent3);
                    }
                    overridePendingTransition(0, 0);//인텐트 효과 없애기
                finish();
                return true;

            }
            else {
                if(state == 1){
                    seekBar = sound_frag.getView().findViewById(R.id.seekBar);
                    seekBar.setProgress(Sound.audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_VOLUME_DOWN:
                            seekBar.setProgress(seekBar.getProgress() - 1);
                            break;
                        case KeyEvent.KEYCODE_VOLUME_UP:
                            seekBar.setProgress(seekBar.getProgress() + 1);
                            break;
                    }
                    return true;
                }

            }

        }catch (Error e){

        }

        nCurrentVolumn = Sound.audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        Sound.audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                nCurrentVolumn, 0);

        Intent intent = new Intent(getApplicationContext(), Setting.class);
        intent.putExtra("activity", Setting.back);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);//인텐트 효과 없애기
        return false;
    }

    @Override
    protected void onPause(){
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume(){
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        restoreState();
    }

    protected void saveState(){
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("them", State.them);
        editor.putInt("vib", State.vib);
        editor.commit();
    }
    protected void restoreState(){
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        if((pref != null) && (pref.contains("them")))
        {
            State.them = pref.getBoolean("them", true);
        }
        if((pref != null) && (pref.contains("vib")))
        {
            State.vib = pref.getInt("vib", 100);
        }

    }



}