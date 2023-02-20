package com.example.iseeyou;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;

import androidx.fragment.app.Fragment;

public class soundFragment extends Fragment {
    SeekBar seekVolumn;
    ImageButton vibtogglebtn;
    AudioManager audioManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sound, container, false);
        seekVolumn = (SeekBar) view.findViewById(R.id.seekBar);
        audioManager = Sound.audioManager;
        int nMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);


        seekVolumn.setMax(nMax);
        seekVolumn.setProgress(Sound.audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        vibtogglebtn = view.findViewById(R.id.thembtn);

        if(State.vib == 100)
        {
            vibtogglebtn.setImageResource(R.drawable.settoggle);
        }
        else
        {
            vibtogglebtn.setImageResource(R.drawable.settoggleoff);
        }

        vibtogglebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sound.play(0);
                Sound.vibrator.vibrate(State.vib);
                if(State.vib == 100)
                {
                    //진동 끄기
                    vibtogglebtn.setImageResource(R.drawable.settoggleoff);
                    State.vib = 0;
                }
                else
                {
                    //진동 켜기
                    vibtogglebtn.setImageResource(R.drawable.settoggle);
                    State.vib = 100;
                }
            }
        });

        seekVolumn.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, 0);
                Sound.play(2);
            }
        });


        return view;
    }



}