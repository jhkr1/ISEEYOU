package com.example.iseeyou;


import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.pytorch.IValue;
import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;


public class MainActivity extends AppCompatActivity implements Runnable{


    private final Executor executor = Executors.newSingleThreadExecutor();
    //????????? ?????? ??????
    private final int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS1 = new String[]{"android.permission.CAMERA"};
    private final String[] REQUIRED_PERMISSIONS2 = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};

    //View
    PreviewView mPreviewView;
    ResultView mResultView;
    ListView listView;
    ImageButton captureImage;
    ImageButton setbtn;
    ImageButton helpbtn;
    ImageButton gallbtn;
    ImageButton barcodebtn;
    ImageView picture;

    private Bitmap mBitmap = null;
    private Module mModule = null;
    private float mImgScaleX, mImgScaleY, mIvScaleX, mIvScaleY, mStartX, mStartY;

    private int state = 0;
    boolean[] label;

    ArrayList<resultData> labeDataList;
    resultAdapter reAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //?????? ??????
        restoreState();

        setContentView(R.layout.activity_main);

        mPreviewView = findViewById(R.id.previewView);
        mResultView = findViewById(R.id.resultView);
        listView = findViewById(R.id.listView);

        captureImage = findViewById(R.id.capture_btn);
        setbtn = findViewById(R.id.setting_btn);
        gallbtn = findViewById(R.id.gallery_btn);
        barcodebtn = findViewById(R.id.barcode_btn);
        picture = findViewById(R.id.pictureView);

        mResultView.setVisibility(View.INVISIBLE);
        picture.setVisibility(View.INVISIBLE);

        label = new boolean[3];


        //?????? ??????
        if(allPermissionsGranted()){
            startCamera();
        } else{
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS1, REQUEST_CODE_PERMISSIONS);
            }
            else
            {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS2, REQUEST_CODE_PERMISSIONS);
            }
        }


        //yolov5??? ????????? ?????? ????????????
        try {
            if(State.module == null){
                BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("label.txt")));
                State.module = LiteModuleLoader.load(assetFilePath(getApplicationContext(), "best.torchscript.ptl"));
                String line;
                List<String> classes = new ArrayList<>();
                while ((line = br.readLine()) != null) {
                    classes.add(line);
                }
                PrePostProcessor.mClasses = new String[classes.size()];
                classes.toArray(PrePostProcessor.mClasses);
            }
        } catch (IOException e) {
            finish();
        }

        //?????? ??????
        setbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Sound.play(3);
                Sound.vibrator.vibrate(State.vib);
                Intent intent = new Intent(getApplicationContext(), Setting.class);
                intent.putExtra("activity", 0);

                startActivity(intent);
                overridePendingTransition(0, 0);//????????? ?????? ?????????
                finish();

            }
        });

        //????????? ??????
        gallbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Sound.play(3);
                Sound.vibrator.vibrate(State.vib);
                Intent intent = new Intent(getApplicationContext(), Gallery.class);
                startActivity(intent);
                overridePendingTransition(0, 0);//????????? ?????? ?????????
                finish();
            }
        });

        //????????? ??????
        barcodebtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Sound.play(3);
                Sound.vibrator.vibrate(State.vib);
                Intent intent = new Intent(getApplicationContext(), BarcodeActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);//????????? ?????? ?????????
                finish();
            }
        });

        //?????? ??????
        mModule = State.module;

        //????????? ??????
        if (!Sound.init) {
            Sound.audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            Sound.soundPool = new SoundPool.Builder()
                    .setMaxStreams(4)
                    .setAudioAttributes(Sound.audioAttributes)
                    .build();

            Sound.audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            Sound.success = Sound.soundPool.load(this, R.raw.success, 1);
            Sound.failure = Sound.soundPool.load(this, R.raw.failure, 1);
            Sound.ing = Sound.soundPool.load(this, R.raw.ing, 0);
            Sound.btnSound = Sound.soundPool.load(this, R.raw.btnsound, 1);
            Sound.init = true;
            Sound.vibrator= (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        }




    }





    //????????? ??????
    private void startCamera() {

        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {

                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);

                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {

        //?????? ??????
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .build();

        ImageCapture.Builder builder = new ImageCapture.Builder();


        final ImageCapture imageCapture = builder
                .setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation())
                .build();

        preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageAnalysis, imageCapture);



        //???????????? ????????? ?????? && ???????????? ??????
        captureImage.setOnClickListener(v -> {
            //???????????? ????????? ?????? ?????????

            if (state == 0) {
                //??????
                SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
                String filename = mDateFormat.format(new Date()) + ".jpg";

                File file = new File(this.getFilesDir(), filename);
                ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();


                imageCapture.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {

                                //????????? ??????????????? ??????????????? ???
                                mBitmap = BitmapFactory.decodeFile(getFilesDir().getPath() + "/" + filename);
                                String filepath = getFilesDir().getPath() + "/" + filename;
                                Matrix matrix= new Matrix();
                                matrix.preRotate(90, 0, 0);
                                mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);

                                //?????? ????????? ?????????
                                picture.setImageBitmap(mBitmap);
                                picture.setVisibility(View.VISIBLE);

                                //?????? ?????????
                                mPreviewView.setVisibility(View.GONE);


                                //bitmap??? imageview??? ?????? ??? ???????????? input ???????????? ?????? ?????????
                                mImgScaleX = (float)mBitmap.getWidth() / PrePostProcessor.mInputWidth;
                                mImgScaleY = (float)mBitmap.getHeight() / PrePostProcessor.mInputHeight;

                                mIvScaleX = (mBitmap.getWidth() > mBitmap.getHeight() ? (float)picture.getWidth() / mBitmap.getWidth() : (float)picture.getHeight() / mBitmap.getHeight());
                                mIvScaleY  = (mBitmap.getHeight() > mBitmap.getWidth() ? (float)picture.getHeight() / mBitmap.getHeight() : (float)picture.getWidth() / mBitmap.getWidth());

                                mStartX = (picture.getWidth() - mIvScaleX * mBitmap.getWidth())/2;
                                mStartY = (picture.getHeight() -  mIvScaleY * mBitmap.getHeight())/2;

                                //????????? ??????
                                Thread thread = new Thread(MainActivity.this);
                                thread.start();

                                //?????? 1??? ??????(?????? ?????? ??????)
                                state = 1;

                                Sound.play(0);
                                Sound.vibrator.vibrate(State.vib);

                            }
                        });
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException error) {
                        error.printStackTrace();
                        Sound.soundPool.stop(Sound.ing);
                        Sound.play(1);
                        Sound.vibrator.vibrate(State.vib);
                    }
                });
            }
            //?????? ???????????? ????????????
            else
            {
                Sound.play(3);
                //?????? 0?????? ??????(????????? ??????)
                state = 0;
                Sound.vibrator.vibrate(State.vib);

                //?????? ????????? ?????? ?????????
                mPreviewView.setVisibility(View.VISIBLE);
                mResultView.setVisibility(View.INVISIBLE);
                picture.setVisibility(View.INVISIBLE);
                listView.setVisibility(View.GONE);

                //???????????? class ?????????
                label = new boolean[3];

            }
        });

    }


    //assetfile??? path??? ?????? ??????
    public static String assetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }


    //?????? ?????? ??????
    private boolean allPermissionsGranted(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            for(String permission : REQUIRED_PERMISSIONS1){
                if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
            return true;
        }
        else
        {
            for(String permission : REQUIRED_PERMISSIONS2){
                if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
            return true;
        }
    }

    //?????? ?????? ?????? ?????? ??????
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "?????? ????????? ???????????? ?????? ?????????????????????", Toast.LENGTH_SHORT).show();
                this.finish();
            }
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

    //?????? ??????
    protected void saveState(){
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("them", State.them);
        editor.putInt("vib", State.vib);
        editor.commit();
    }

    //?????? ??????
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

        //?????? ??????
        if(State.them)
        {
            setTheme(R.style.contrastTheme);
        }
        else
        {
            setTheme(R.style.notcontrastTheme);
        }

    }


    //???????????? ???????????? ?????????
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

    @Override
    public void onBackPressed() {
        if(state == 0)
        {
            moveTaskToBack(true); // ???????????? ?????????????????? ??????
            finishAndRemoveTask(); // ???????????? ?????? + ????????? ??????????????? ?????????

            System.exit(0);
        }
        else
        {
            //?????? 0?????? ??????(????????? ??????)
            state = 0;

            //?????? ????????? ?????? ?????????
            mPreviewView.setVisibility(View.VISIBLE);
            mResultView.setVisibility(View.INVISIBLE);
            picture.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.GONE);

            //???????????? class ?????????
            label = new boolean[3];
        }

    }


}