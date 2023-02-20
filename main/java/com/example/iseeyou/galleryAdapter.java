package com.example.iseeyou;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;


public class galleryAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    String[] arrImage;

    public galleryAdapter(Context context, String[] arrImage){
        this.context = context;
        this.arrImage = arrImage;
    }

    @Override
    public int getCount() {
        return arrImage.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if(inflater == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(view == null){
            view = inflater.inflate(R.layout.img_layout, null);
        }
        ImageView Image = view.findViewById(R.id.Image);

        Glide.with(context).load(arrImage[position]).into(Image);
        return view;
    }
}
