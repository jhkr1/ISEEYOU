package com.example.iseeyou;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.Button;

import com.journeyapps.barcodescanner.BarcodeView;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class CodeReaderActivity extends AppCompatActivity implements DecoratedBarcodeView.TorchListener {

    private CaptureManager manager;
    //private boolean isFlashOn = false;

    private DecoratedBarcodeView barcodeView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restoreState();
        setContentView(R.layout.activity_barcode);

        barcodeView = findViewById(R.id.barcodeView);

        manager = new CaptureManager(this, barcodeView);
        manager.initializeFromIntent(getIntent(), savedInstanceState);
        manager.decode();
    }

    @Override
    public void onTorchOn(){

    }

    @Override
    public void onTorchOff(){

    }
    @Override
    protected void onResume(){
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        manager.onResume();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        manager.onDestroy();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        manager.onSaveInstanceState(outState);
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

        if(State.them)
        {
            setTheme(R.style.contrastTheme);
        }
        else
        {
            setTheme(R.style.notcontrastTheme);
        }
    }

    @Override
    public void onBackPressed() {
        // back was pressed
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        startActivity(intent);
        overridePendingTransition(0, 0);//인텐트 효과 없애기
        finish();
    }



}