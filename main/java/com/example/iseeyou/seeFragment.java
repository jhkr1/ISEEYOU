package com.example.iseeyou;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

public class seeFragment extends Fragment {

    ImageButton themtogglebtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Setting.nCurrentVolumn = Sound.audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        Sound.audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                Setting.nCurrentVolumn, 0);


        View rootview = inflater.inflate(R.layout.fragment_see, container, false);
        themtogglebtn = rootview.findViewById(R.id.thembtn);
        if(State.them)
        {
            themtogglebtn.setImageResource(R.drawable.settoggle);
        }
        else
        {
            themtogglebtn.setImageResource(R.drawable.settoggleoff);
        }



        themtogglebtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Sound.play(0);
                Sound.vibrator.vibrate(State.vib);
                if(State.them)
                {
                    //고대비 끄기
                    getContext().setTheme(R.style.notcontrastTheme);
                    themtogglebtn.setImageResource(R.drawable.settoggleoff);
                    State.them = false;
                    Intent intent = new Intent(getContext(), Setting.class);
                    intent.putExtra("activity", Setting.back);
                    startActivity(intent);
                    getActivity().finish();

                }
                else
                {
                    //고대비 켜기
                    getContext().setTheme(R.style.contrastTheme);
                    themtogglebtn.setImageResource(R.drawable.settoggle);
                    State.them = true;
                    Intent intent = new Intent(getContext(), Setting.class);
                    intent.putExtra("activity", Setting.back);
                    startActivity(intent);
                    getActivity().finish();

                }
                getActivity().overridePendingTransition(0, 0);//인텐트 효과 없애기
            }
        });

        return rootview;
    }



}