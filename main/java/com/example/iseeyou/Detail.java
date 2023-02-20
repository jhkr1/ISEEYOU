
package com.example.iseeyou;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;


public class Detail extends AppCompatActivity {


    TextView name;

    ArrayList<Result_Item> items;

    ListView listView;
    DetailAdapter reAdapter;
    Long resultCode;
    static int class_info;



    public class Result_Item{
        boolean selected;
        String result;
        Result_Item(boolean b, String r){
            selected = b;
            result = r;
        }
        public boolean isSelected(){
            return selected;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //테마 설정
        if(State.them)
        {
            setTheme(R.style.contrastTheme);
        }
        else
        {
            setTheme(R.style.notcontrastTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        listView = findViewById(R.id.listView);
        name = findViewById(R.id.productname);
        ImageButton backbtn = findViewById(R.id.back_btn);
        ImageButton setbtn = findViewById(R.id.setting_btn);
        TextView nottxt = findViewById(R.id.sorrytxt);
        nottxt.setVisibility(View.GONE);

        //비어있을 때만 제품 정보 로드
        if(State.product == null)
        {
            State.makeArray(getApplicationContext());

        }

        //인식된 제품 정보 불러오기
        class_info = 3;
        int size = State.product.size() - 1;
        int index = -1;
        try{

            resultCode = Long.parseLong(getIntent().getExtras().getString("product"));
            index = serch(0, size);


        } catch (RuntimeException ignored)
        {

        }

        if(index > -1){

            String[] answer = State.product.get(index).toString().split("\t");
            initItems(answer);

            //상세정보 리스트 뷰로 나타내기
            reAdapter = new DetailAdapter(this, items);
            listView.setAdapter(reAdapter);

            listView.setVisibility(View.VISIBLE);
            name.setText(items.get(0).result); //이름 띄우기
            Sound.play(0);
        }
        else {
            //등록되지 않은 상품입니다. 아있슈가 더 노력하겠습니다! 라는 문구 뜨게 하기 + 에러 소리도
            nottxt.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            Sound.play(1);
        }



        //뒤로 가기 버튼
        backbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Sound.play(3);
                Sound.vibrator.vibrate(State.vib);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                startActivity(intent);
                overridePendingTransition(0, 0);//인텐트 효과 없애기
                finish();
            }
        });

        //설정 버튼
        setbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Sound.play(3);
                Sound.vibrator.vibrate(State.vib);
                Intent intent = new Intent(getApplicationContext(), Setting.class);
                intent.putExtra("activity", 2);

                startActivity(intent);
                overridePendingTransition(0, 0);//인텐트 효과 없애기
            }
        });
    }

    //리스트뷰에 표시할 아이템 초기화
    private void initItems(String[] code){
        items = new ArrayList<Result_Item>();
        for(int i = 0; i < code.length - 2; i++) {
            Result_Item result_item = new Result_Item(false, code[i]);
            items.add(result_item);
        }
        class_info = Integer.parseInt(code[-1]);
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

        int a = Integer.parseInt((State.product.get(3)).get(4).toString());
    }


    private int serch(int l, int r){
        //바코드로 제품 정보 찾기
        //시작, 끝, 찾고자 하는 것 즉, 0, Arraylist<Arraylist> product.size()
        if (r >= l) {
            int mid = l + (r - l) / 2;
            int codeIndex = State.product.get(mid).size() - 2;
                if (Long.parseLong(State.product.get(mid).get(codeIndex).toString()) == resultCode)
                    return mid;

                // If element is smaller than mid, then
                // it can only be present in left subarray
                if (Long.parseLong(State.product.get(mid).get(codeIndex).toString()) > resultCode)
                    return serch(l, mid - 1);

                // Else the element can only be present
                // in right subarray
                return serch(mid + 1, r);
            }

            return -1;
        }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);//인텐트 효과 없애기
        finish();

    }

}



