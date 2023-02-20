package com.example.iseeyou;

import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Gallery extends AppCompatActivity implements Runnable {

    private int state = 0;
    boolean[] label;

    ListView listView;
    GridView gridView;
    private ResultView mResultView;
    ImageButton setbtn;
    ImageButton backbtn;
    ImageButton detailbtn;
    ImageView imageView;
    ImageButton removebtn;

    private Bitmap mBitmap = null;
    private Module mModule = null;
    private float mImgScaleX, mImgScaleY, mIvScaleX, mIvScaleY, mStartX, mStartY;

    resultAdapter reAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //테마 적용
        if(State.them)
        {
            setTheme(R.style.contrastTheme);
        }
        else
        {
            setTheme(R.style.notcontrastTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);


        imageView = findViewById(R.id.imageView);
        detailbtn = findViewById(R.id.serch_btn);
        setbtn = findViewById(R.id.setting_btn);
        backbtn = findViewById(R.id.back_btn);
        mResultView = findViewById(R.id.resultView);
        listView = findViewById(R.id.listView);
        gridView = findViewById(R.id.gridView);
        removebtn = findViewById(R.id.remove_btn);

        imageView.setVisibility(View.GONE);
        detailbtn.setVisibility(View.GONE);
        mResultView.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.GONE);
        removebtn.setVisibility(View.GONE);

        File imgDirPath = this.getFilesDir();
        String getFileName = imgDirPath.getPath();


        label = new boolean[3];

        //저장된 사진 리스트 불러오기
        String[] imgList = imgDirPath.list(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                Boolean b = false;
                if(filename.toLowerCase().endsWith(".jpg")) b = true;
                return b;
            }
        });

        //사진 이름 파일 path로 변경해서 저장하기
        String[] imgfiles = new String[imgList.length];
        for(int i = 0; i < imgList.length;i++)
        {
            imgfiles[i] = getFileName + "/" +imgList[i];
        }

        //사진 최신순으로 정렬
        Arrays.sort(imgfiles, Collections.reverseOrder());


        galleryAdapter galleryAdapter = new galleryAdapter(this, imgfiles);
        gridView.setAdapter(galleryAdapter);

        //사진 누르면 view바꾸기
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Sound.play(3);
                Sound.vibrator.vibrate(State.vib);
                //사진 불러오기
                Glide.with(getApplicationContext()).load(imgfiles[position]).into(imageView);
                imageView.setVisibility(View.VISIBLE);

                gridView.setVisibility(View.GONE);
                detailbtn.setVisibility(View.VISIBLE);
                removebtn.setVisibility(View.VISIBLE);

                //사진 상세 모드
                state = 1;

                //상세 버튼 클릭시 인공지능 실행
                detailbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Sound.play(0);
                        Sound.vibrator.vibrate(State.vib);
                        //비트맵 불러와서 쓰레드 실행
                        Matrix matrix= new Matrix();
                        matrix.preRotate(90, 0, 0);
                        mBitmap = BitmapFactory.decodeFile(imgfiles[position]);
                        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);

                        mImgScaleX = (float)mBitmap.getWidth() / PrePostProcessor.mInputWidth;
                        mImgScaleY = (float)mBitmap.getHeight() / PrePostProcessor.mInputHeight;

                        mIvScaleX = (mBitmap.getWidth() > mBitmap.getHeight() ? (float)imageView.getWidth() / mBitmap.getWidth() : (float)imageView.getHeight() / mBitmap.getHeight());
                        mIvScaleY  = (mBitmap.getHeight() > mBitmap.getWidth() ? (float)imageView.getHeight() / mBitmap.getHeight() : (float)imageView.getWidth() / mBitmap.getWidth());

                        mStartX = (imageView.getWidth() - mIvScaleX * mBitmap.getWidth())/2;
                        mStartY = (imageView.getHeight() -  mIvScaleY * mBitmap.getHeight())/2;

                        Thread thread = new Thread(Gallery.this);
                        thread.start();
                    }
                });
                //제거 버튼
                removebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        File f = new File(imgfiles[position]);
                        f.delete();
                        imageView.setVisibility(View.GONE);
                        gridView.setVisibility(View.VISIBLE);
                        detailbtn.setVisibility(View.GONE);
                        mResultView.setVisibility(View.INVISIBLE);
                        listView.setVisibility(View.GONE);
                        removebtn.setVisibility(View.GONE);
                        state = 0;
                        Sound.play(0);
                        Sound.vibrator.vibrate(State.vib);
                        Intent intent = new Intent(getApplicationContext(), Gallery.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);//인텐트 효과 없애기
                        finish();

                    }
                });

            }
        });

        //설정 버튼
        setbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Sound.play(3);
                Sound.vibrator.vibrate(State.vib);
                Intent intent = new Intent(getApplicationContext(), Setting.class);
                intent.putExtra("activity", 1);

                startActivity(intent);
                overridePendingTransition(0, 0);//인텐트 효과 없애기

            }
        });


        //뒤로가기 버튼
        backbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Sound.play(3);
                Sound.vibrator.vibrate(State.vib);
                if(state == 0) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);//인텐트 효과 없애기
                    finish();
                }
                else{
                    imageView.setVisibility(View.GONE);
                    gridView.setVisibility(View.VISIBLE);
                    detailbtn.setVisibility(View.GONE);
                    mResultView.setVisibility(View.INVISIBLE);
                    listView.setVisibility(View.GONE);
                    removebtn.setVisibility(View.GONE);
                    state = 0;
                }
            }
        });



        mModule = State.module;

    }

    @Override
    public void onBackPressed() {
        if(state == 0) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);//인텐트 효과 없애기
            finish();
        }
        else{
            imageView.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
            detailbtn.setVisibility(View.GONE);
            mResultView.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.GONE);
            removebtn.setVisibility(View.GONE);
            state = 0;
        }
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


    //인공지능 인식하는 쓰레드
    @Override
    public void run() {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(mBitmap, PrePostProcessor.mInputWidth, PrePostProcessor.mInputHeight, true);
        final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(resizedBitmap, PrePostProcessor.NO_MEAN_RGB, PrePostProcessor.NO_STD_RGB);
        IValue[] outputTuple = mModule.forward(IValue.from(inputTensor)).toTuple();
        final Tensor outputTensor = outputTuple[0].toTensor();
        final float[] outputs = outputTensor.getDataAsFloatArray();
        final ArrayList<Result> results =  PrePostProcessor.outputsToNMSPredictions(outputs, mImgScaleX, mImgScaleY, mIvScaleX, mIvScaleY, mStartX, mStartY);

        for (Result result : results) {
            if(result != null){
                label[result.classIndex] = true;
            }
        }

        reAdapter = new resultAdapter(this, label);

        runOnUiThread(() -> {
            mResultView.setResults(results);
            mResultView.invalidate();
            mResultView.setVisibility(View.VISIBLE);

            listView.setAdapter(reAdapter);
            listView.setVisibility(View.VISIBLE);

        });
    }

}