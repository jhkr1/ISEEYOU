package com.example.iseeyou;

import android.content.Context;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.util.Log;

import org.pytorch.Module;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class State {
    //테마
    public static boolean them;

    //진동 여부
    public static int vib;
    //모델
    public static Module module = null;

    //제품 정보
    static ArrayList<ArrayList> product;



    public static void makeArray(Context context){
        product = new ArrayList<>();
            try{
                BufferedReader bf = new BufferedReader(new InputStreamReader(context.getAssets().open("Product.txt")));

                String line;
                while((line = bf.readLine()) != null)
                {
                    ArrayList<String> small = new ArrayList<>();
                    String[] splitedStr = line.split("\t");

                    for (String s : splitedStr) {
                        if (Objects.equals(s, "-1")) break;
                        small.add(s);
                    }
                    product.add(small);
                }
                bf.close();
            }
            catch (IOException e){
                e.printStackTrace();

            }

    }

}
