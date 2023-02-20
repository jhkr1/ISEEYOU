package com.example.iseeyou;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageButton;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

//바코드 인식
public class BarcodeActivity extends AppCompatActivity {
    IntentIntegrator intentIntegrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restoreState();
        setContentView(R.layout.activity_barcode);



        intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setCaptureActivity(CodeReaderActivity.class);
        intentIntegrator.setBeepEnabled(false);//바코드 인식시 소리
        intentIntegrator.initiateScan();

    }

    //데이터 추출
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result !=null){
            if(result.getContents() != null){

                Intent intent = new Intent(getApplicationContext(), Detail.class);
                intent.putExtra("product", result.getContents());
                Sound.vibrator.vibrate(State.vib);
                startActivity(intent);
                overridePendingTransition(0, 0);//인텐트 효과 없애기
            }
            else{
                intentIntegrator.initiateScan();
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }


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