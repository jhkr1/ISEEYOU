package com.example.iseeyou;


import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DetailAdapter extends BaseAdapter {
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<Detail.Result_Item> result;
    String[] drink = {"제품명", "중량", "열량(kcal)", "나트륨(mg)", "탄수화물(g)"
            , "당류(g)", "지방(g)", "트렌스지방(g)", "포화지방(g)", "콜레스테롤(mg)"
            , "단백질(g)", "타우린(mg)", "비타민(mg)", "에리스리톨(g)"};
    String[] lamen = {"제품명", "중량(g)", "칼로리(kcal)"
            , "나트륨(mg)", "탄수화물(g)", "당류(g)", "콜레스테롤"
            , "지방(g)", "트렌스지방(g)", "포화지방(g)", "단백질(g)", "칼슘(mg)"};
    String[] snack = {"제품명", "중량(g)", "열량(kcal)", "나트륨(mg)", "탄수화물(g)"
            , "당류(g)", "지방(g)", "트렌스지방(g)", "포화지방(g)", "콜레스테롤(mg)"
            , "단백질(g)", "당알콜(g)", "칼슘(mg)", "비타민A(ug RE)", "비타민B1(mg)"
            , "비타민B2(mg)", "비타민B6(mg)", "비타민C(mg)", "비타민D(ug)", "엽산(ug)"
            , "나이아신(mg)", "식이섬유(g)"};

    public DetailAdapter(Context context, ArrayList<Detail.Result_Item> data) {
        mContext = context;
        result = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return result.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ArrayList<Detail.Result_Item> getItem(int position) {
        return result;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.detail_item, null);

        Button resultbtn = view.findViewById(R.id.result_btn);
        TextView resulttxt = view.findViewById(R.id.result_txt);

        switch (Detail.class_info){
            case 0:
                resultbtn.setText(lamen[position]);
                break;
            case 1:
                resultbtn.setText(snack[position]);
                break;
            case 2:
                resultbtn.setText(drink[position]);
        }

        resulttxt.setText(result.get(position).result);
        resulttxt.setVisibility(View.GONE);


        resultbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(resulttxt.getVisibility() == View.GONE) resulttxt.setVisibility(View.VISIBLE);
                else resulttxt.setVisibility(View.GONE);
                boolean newState = !result.get(position).isSelected();
                result.get(position).selected = newState;


            }
        });

        resultbtn.setSelected(result.get(position).isSelected());
        if(result.get(position).isSelected()) resulttxt.setVisibility(View.VISIBLE);
        return view;
    }
}