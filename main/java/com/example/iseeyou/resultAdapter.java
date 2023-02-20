package com.example.iseeyou;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class resultAdapter extends BaseAdapter {
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    boolean[] result;
    String[] label = {"컵라면", "과자", "음료"};

    public resultAdapter(Context context, boolean[] data) {
        mContext = context;
        result = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return result.length;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return result[position];
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.resultlist_layout, null);

        if(result[position])
        {
            TextView resulttxt = view.findViewById(R.id.resultView);
            resulttxt.setText(label[position]);
        }
        else{
            TextView resulttxt = view.findViewById(R.id.resultView);
            resulttxt.setVisibility(View.GONE);
        }

        return view;
    }
}
