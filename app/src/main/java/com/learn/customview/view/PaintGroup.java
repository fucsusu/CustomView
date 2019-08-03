package com.learn.customview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.Group;

import com.learn.customview.bean.ToolsFormType;
import com.learn.customview.bean.ToolsPenType;
import com.learn.customview.bean.ToolsType;

/**
 * Created by fucc
 * Date: 2019-08-03 17:16
 */
public class PaintGroup extends ViewGroup {

    private PaintPad topPaint;

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
        topPaint.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        this.addView(topPaint);
    }


    @Override
    protected void onLayout(boolean b, int l, int i1, int i2, int i3) {
        int cCount = getChildCount();
        for (int j = 0; j < cCount; j++) {
            View childView = getChildAt(j);
            childView.layout(0, 0, i2, i3);
        }
    }

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
}
