package com.learn.customview.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.learn.customview.R;
import com.learn.customview.bean.ToolsFormType;
import com.learn.customview.bean.ToolsPenType;
import com.learn.customview.bean.ToolsType;

/**
 * Created by fucc
 * Date: 2019-08-03 17:16
 */
public class PaintGroup extends FrameLayout {

    private PaintPad topPaint;
    private ViewGroup mViewGroup;
    //文本输入框
    public EditText mInputEt;
    //文字输入框完成图片大小
    private int mETCWidth = 50;
    private int mEtCHeight = 50;
    public ImageView saveImg;
    public ImageView delectImg;
    public float downX;
    public float downY;

    public PaintGroup(Context context) {
        super(context);
        initView();
    }

    public PaintGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PaintGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        topPaint = new PaintPad(getContext());
        topPaint.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        topPaint.setPaintDownXY(mPaintDownXY);
        this.addView(topPaint);

        mViewGroup = this;

        mInputEt = new MyEditText(getContext());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mInputEt.setLayoutParams(params);
        mInputEt.setPadding(0, 0, 0, 0);
        mInputEt.setBackgroundColor(Color.TRANSPARENT);
        mInputEt.setFocusable(true);
        mInputEt.setLineSpacing(0, 1.0f);
        mInputEt.setGravity(Gravity.TOP);

        //完成按钮
        LayoutParams imgLp = new FrameLayout.LayoutParams(mETCWidth, mEtCHeight);
        saveImg = new ImageView(getContext());
        saveImg.setImageResource(R.mipmap.ed_save);
        saveImg.setLayoutParams(imgLp);
        saveImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewGroup.removeView(mInputEt);
                saveImg.setVisibility(GONE);
                delectImg.setVisibility(GONE);
                topPaint.onInsertText(mInputEt.getText().toString(), downX, downY);
                mInputEt.setText("");
            }
        });

        //删除按钮
        delectImg = new ImageView(getContext());
        delectImg.setImageResource(R.mipmap.ed_delect);
        delectImg.setLayoutParams(imgLp);
        delectImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImg.setVisibility(GONE);
                delectImg.setVisibility(GONE);
                mViewGroup.removeView(mInputEt);
                topPaint.onInsertText("", 0, 0);
                mInputEt.setText("");
            }
        });

        mViewGroup.addView(saveImg);
        mViewGroup.addView(delectImg);
        delectImg.setVisibility(GONE);
        saveImg.setVisibility(GONE);
    }


    @Override
    protected void onLayout(boolean b, int l, int i1, int i2, int i3) {
        saveImg.setTranslationX(getWidth() - mETCWidth * 2);
        saveImg.setTranslationY(mETCWidth * 2);
        delectImg.setTranslationX(getWidth() - mETCWidth * 4);
        delectImg.setTranslationY(mETCWidth * 2);
        super.onLayout(b, l, i1, i2, i3);
        for (int j = 0; j < getChildCount(); j++) {
            View childView = getChildAt(j);
            if (childView instanceof PaintPad) {
                childView.layout(0, 0, i2, i3);
            } else if (childView instanceof EditText) {
                childView.layout(0, 0, (int) (i2 - downX), (int) (i3 - downY));
            } else {
                childView.layout(0, 0, childView.getLayoutParams().width, childView.getLayoutParams().height);
            }
        }
    }

    PaintDownXY mPaintDownXY = new PaintDownXY() {
        @Override
        public void paintDownXY(float x, float y) {
            downX = x;
            downY = y;
            //点击添加view
            mInputEt.setTranslationX(x);
            mInputEt.setTranslationY(y);
            mInputEt.getLayoutParams().width = (int) (getWidth() - x);
            mInputEt.getLayoutParams().height = (int) (getHeight() - y);
            mViewGroup.addView(mInputEt);

            saveImg.setVisibility(VISIBLE);
            delectImg.setVisibility(VISIBLE);
            postInvalidate();
        }

        @Override
        public void penPropertyChange(int penWith, int penColor) {
            //参数变化
            mInputEt.setTextSize(TypedValue.COMPLEX_UNIT_PX, penWith);
            mInputEt.setTextColor(penColor);
        }
    };

    /**
     * 画笔大小
     *
     * @param mToolsPenProgress
     */
    public void setmToolsPenProgress(int mToolsPenProgress) {
        topPaint.setmToolsPenProgress(mToolsPenProgress);
    }

    /**
     * 橡皮檫宽度
     *
     * @param mToolsEraserWidth
     */
    public void setmToolsEraserWidth(int mToolsEraserWidth) {
        topPaint.setmToolsEraserWidth(mToolsEraserWidth);
    }

    /**
     * 形状宽度
     *
     * @param mToolsFormWidth
     */
    public void setmToolsFormWidth(int mToolsFormWidth) {
        topPaint.setmToolsFormWidth(mToolsFormWidth);
    }

    /**
     * 画笔颜色
     *
     * @param mToolsPenColor
     */
    public void setmToolsPenColor(int mToolsPenColor) {
        topPaint.setmToolsPenColor(mToolsPenColor);
    }

    /**
     * 形状颜色
     *
     * @param mToolsFormColor
     */
    public void setmToolsFormColor(int mToolsFormColor) {
        topPaint.setmToolsFormColor(mToolsFormColor);
    }

    //清除画笔数据
    public void cleanActions() {
        topPaint.cleanActions();
    }

    /**
     * 画笔类型
     *
     * @param mToolsPenType
     */
    public void setmToolsPenType(ToolsPenType mToolsPenType) {
        topPaint.setmToolsPenType(mToolsPenType);
    }

    /**
     * 形状类型
     *
     * @param mToolsFormType
     */
    public void setmToolsFormType(ToolsFormType mToolsFormType) {
        topPaint.setmToolsFormType(mToolsFormType);
    }

    /**
     * 设置绘制类型
     *
     * @param type
     */
    public void setToolsType(ToolsType type) {
        topPaint.setToolsType(type);
    }

    /**
     * 文字颜色
     *
     * @param mToolsFontColor
     */
    public void setmToolsFontColor(int mToolsFontColor) {
        topPaint.setmToolsFontColor(mToolsFontColor);
    }

    /**
     * 文字大小
     *
     * @param mToolsFontSize
     */
    public void setmToolsFontSize(int mToolsFontSize) {
        topPaint.setmToolsFontSize(mToolsFontSize);
    }


    //绘制点击事件
    interface PaintDownXY {
        //画笔点击位置
        void paintDownXY(float x, float y);

        //画笔属性修改
        void penPropertyChange(int penWith, int penColor);
    }
}
