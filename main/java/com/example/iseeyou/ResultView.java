package com.example.iseeyou;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;


public class ResultView extends View {

    private final static int TEXT_X = 40;
    private final static int TEXT_Y = 35;
    private final static int TEXT_WIDTH = 260;
    private final static int TEXT_HEIGHT = 50;

    private Paint mPaintRectangle;

    private ArrayList<Result> mResults;

    public ResultView(Context context) {
        super(context);
    }

    public ResultView(Context context, AttributeSet attrs){
        super(context, attrs);
        mPaintRectangle = new Paint();
        mPaintRectangle.setColor(Color.YELLOW);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mResults == null) return;
        for (Result result : mResults) {
            mPaintRectangle.setStrokeWidth(5);
            mPaintRectangle.setStyle(Paint.Style.STROKE);
            canvas.drawRect(result.rect, mPaintRectangle);


        }

    }

    public void setResults(ArrayList<Result> results) {
        mResults = results;
    }
}