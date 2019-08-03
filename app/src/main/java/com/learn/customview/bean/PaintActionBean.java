package com.learn.customview.bean;


import android.graphics.PointF;

import java.util.ArrayList;

/**
 * 画笔数据的实体类
 *
 * @author
 */
public class PaintActionBean {

    public enum PAType {
        pa_markerPen,//标记
        pa_arrowLine,//箭头
        pa_line,     //线
        pa_Rectangle,//矩形
        pa_Ellipse, //椭圆
        pa_Text,  //文本
        pa_Eraser  //橡皮擦
    }
    //模式
    public PAType nActionMode;
    //模型宽度
    public int nPenWidth;
    //模型颜色
    public int nPenColor;
    //画笔在当前view比值
    public ArrayList<PointF> alActionPoint = new ArrayList<PointF>();
    public String sText = "";
    //模型类型 （空心 实心）
    public boolean bIsFill = true;
    //存放移动最后的一个点
    public PointF paMoveEndPoint;
}
