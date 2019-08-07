package com.learn.customview.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatEditText;


/**
 * Created by fucc
 * Date: 2019-08-05 11:37
 */
public class MyEditText extends AppCompatEditText implements TextWatcher {
    public MyEditText(Context context) {
        super(context);
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Paint paint;

    {
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        paint.setColor(Color.RED);
        addTextChangedListener(this);
    }

    // 计算出该TextView中文字的长度(像素)
    public float getTextViewLength() {
        TextPaint paint = getPaint();
        float textLength = paint.measureText(getText().toString());
        return textLength;
    }

    //获取文字的高度
    public int getFontHeight() {
        Paint paint = getPaint();
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.top) + 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float min = Math.min(getTextViewLength() + 5, getMeasuredWidth());
        if ((min < 150)) {
            min = 150;
        }
        canvas.drawRect(1, 1, min,
                (getTextViewLength() / getMeasuredWidth() + 1) * getFontHeight(), paint);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        Log.e("aaaa", "beforeTextChanged: ");
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.e("aaaa", "onTextChanged: ");
    }

    @Override
    public void afterTextChanged(Editable editable) {
        Log.e("aaaaaa", "afterTextChanged: ");
        postInvalidate();
    }
}
