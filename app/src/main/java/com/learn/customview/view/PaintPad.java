package com.learn.customview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.learn.customview.bean.PaintActionBean;
import com.learn.customview.bean.ToolsFormType;
import com.learn.customview.bean.ToolsPenType;
import com.learn.customview.bean.ToolsType;
import com.learn.customview.utils.ColorUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义画板  切换颜色和粗细
 * 1.实现画笔
 * 2.实现图形 矩形 椭圆
 * <p>
 * <p>
 * 文字
 * 橡皮檫
 * 缩放
 * 笔迹的回退
 * 分层缓存处理
 */
public class PaintPad extends View {

    //画笔数据
    private PaintActionBean mCurrentPaintActionBean = null;
    //画笔类型
    private PaintActionBean.PAType mActionMode = null;
    //画笔空心还是实心
    private boolean mActionfill;
    //画笔宽度
    private int mPenWidth = 10;
    //画笔颜色
    private int mPenColor = 0xff5AC9FA;

    //当前view宽
    private int mViewWidth = -1;
    //当前view高
    private int mViewHeight = -1;

    //显示画布大小
    private RectF m_rcBK = new RectF();
    //文字
    private RectF m_orgRcBK = null;
    //工具类型
    private ToolsType mToolsType = ToolsType.pen;

    //笔类型
    private ToolsPenType mToolsPenType = ToolsPenType.fountainPen;
    //笔颜色
    private int mToolsPenColor = 0xff5AC9FA;
    //笔宽度
    private int mToolsPenProgress = 10;

    //文字颜色颜色
    private int mToolsFontColor = 0xff5AC9FA;
    //文字大小
    private int mToolsFontSize = 30;

    //形状
    private ToolsFormType mToolsFormType = ToolsFormType.hollow_circle;
    //颜色
    private int mToolsFormColor = 0xff5AC9FA;
    //宽度
    private int mToolsFormWidth = 10;
    //橡皮宽高
    private int mToolsEraserWidth = 10;
    //存放画笔数据
    public List<PaintActionBean> paintActionBeans = new ArrayList<>();
    private Paint linePaint = new Paint();
    private Paint bgPaint = new Paint();


    public PaintPad(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private String[] whiteBoardbackground = {"#ffffff", "#000000", "#415646",
            "#ffc973", "#5d4245", "#9ad0ea", "#756691", "#558289"};

    //自定义白板底色的色值索引
    private int colorIndex = 0;

    {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    public void onDraw(Canvas cvs) {
        if (this.isInEditMode()) return;
        if (PaintBk(cvs)) {
            PaintActions(cvs);
        }
    }


    //画板创建
    boolean PaintBk(Canvas cvs) {
        bgPaint.setColor(Color.parseColor(whiteBoardbackground[colorIndex]));

        cvs.drawRect(m_rcBK, bgPaint);

        RectF rectF = new RectF(m_rcBK.left, m_rcBK.top, m_rcBK.right, m_rcBK.bottom);
        Paint paint = new Paint();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cvs.saveLayer(rectF, paint);
        } else {
            cvs.saveLayer(m_rcBK.left, m_rcBK.top, m_rcBK.right, m_rcBK.bottom, paint, Canvas.ALL_SAVE_FLAG);
        }
        return true;
    }

    /**
     * 绘制图形前数据梳理
     *
     * @param cvs
     */
    void PaintActions(Canvas cvs) {
        for (int i = 0; i < paintActionBeans.size(); i++) {
            paintActions(paintActionBeans.get(i), cvs);
        }
        if (mCurrentPaintActionBean != null) {
            paintActions(mCurrentPaintActionBean, cvs);
        }

    }

    /***
     *    绘制图形
     * @param tl_pa
     * @param cvs
     */
    void paintActions(PaintActionBean tl_pa, Canvas cvs) {
        switch (tl_pa.nActionMode) {
            case pa_line: {   //线
                linePaint.setColor(tl_pa.nPenColor);
                linePaint.setStrokeWidth(tl_pa.nPenWidth);
                PointF ptStart = null, ptstop = null;
                if (tl_pa.alActionPoint.size() == 2) {
                    ptStart = unRelativePoint(tl_pa.alActionPoint.get(0));
                    ptstop = unRelativePoint(tl_pa.alActionPoint.get(1));
                } else if (tl_pa.alActionPoint.size() == 1) {
                    ptStart = unRelativePoint(tl_pa.alActionPoint.get(0));
                    ptstop = unRelativePoint(tl_pa.paMoveEndPoint);
                }

                if (ptStart == null || ptstop == null) {
                    break;
                }

                cvs.drawLine(ptStart.x, ptStart.y, ptstop.x, ptstop.y, linePaint);
            }
            break;
            case pa_markerPen: {   //标记
                linePaint.setColor(tl_pa.nPenColor);
                linePaint.setStrokeWidth(tl_pa.nPenWidth);
                //转化成统一高度下的百分比
                int nSize = tl_pa.alActionPoint.size();
                if (nSize <= 2) {
                    PointF pointF = unRelativePoint(tl_pa.alActionPoint.get(0));
                    cvs.drawPoint(pointF.x, pointF.y, linePaint);
                    break;
                }

                cvs.drawPath(getMarkPenPath(tl_pa), linePaint);
            }
            break;
            case pa_Eraser: {  //橡皮擦
                linePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                linePaint.setStrokeWidth(tl_pa.nPenWidth);

                int nSize = tl_pa.alActionPoint.size();
                if (nSize <= 2) {
                    PointF pointF = unRelativePoint(tl_pa.alActionPoint.get(0));
                    cvs.drawPoint(pointF.x, pointF.y, linePaint);
                    break;
                }
                cvs.drawPath(getMarkPenPath(tl_pa), linePaint);
            }
            break;
            case pa_Ellipse: {    //椭圆
                linePaint.setColor(tl_pa.nPenColor);
                linePaint.setStrokeWidth(tl_pa.nPenWidth);
                if (tl_pa.bIsFill) linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
                PointF ptStart = null, ptstop = null;
                if (tl_pa.alActionPoint.size() == 2) {
                    ptStart = unRelativePoint(tl_pa.alActionPoint.get(0));
                    ptstop = unRelativePoint(tl_pa.alActionPoint.get(1));
                } else if (tl_pa.alActionPoint.size() == 1) {
                    ptStart = unRelativePoint(tl_pa.alActionPoint.get(0));
                    ptstop = unRelativePoint(tl_pa.paMoveEndPoint);
                }

                if (ptStart == null || ptstop == null) {
                    break;
                }

                if (ptStart.x == ptstop.x && ptStart.y == ptstop.y) {
                    cvs.drawPoint(ptStart.x, ptStart.y, linePaint);
                } else {
                    RectF ovalrect = new RectF();
                    ovalrect.left = Math.min(ptStart.x, ptstop.x);
                    ovalrect.top = Math.min(ptStart.y, ptstop.y);
                    ovalrect.right = Math.max(ptStart.x, ptstop.x);
                    ovalrect.bottom = Math.max(ptStart.y, ptstop.y);
                    cvs.drawOval(ovalrect, linePaint);
                }

                PointF sendPf = new PointF();
                sendPf.x = ptstop.x;
                sendPf.y = ptStart.y;
            }
            break;
            case pa_Rectangle: {   //矩形
                linePaint.setColor(tl_pa.nPenColor);
                linePaint.setStrokeWidth(tl_pa.nPenWidth);
                if (tl_pa.bIsFill) linePaint.setStyle(Paint.Style.FILL_AND_STROKE);//

                PointF ptStart = null, ptstop = null;
                if (tl_pa.alActionPoint.size() == 2) {
                    ptStart = unRelativePoint(tl_pa.alActionPoint.get(0));
                    ptstop = unRelativePoint(tl_pa.alActionPoint.get(1));
                } else if (tl_pa.alActionPoint.size() == 1) {
                    ptStart = unRelativePoint(tl_pa.alActionPoint.get(0));
                    ptstop = unRelativePoint(tl_pa.paMoveEndPoint);
                }

                if (ptStart == null || ptstop == null) {
                    break;
                }

                RectF ovalrect = new RectF();
                ovalrect.left = Math.min(ptStart.x, ptstop.x);
                ovalrect.top = Math.min(ptStart.y, ptstop.y);
                ovalrect.right = Math.max(ptStart.x, ptstop.x);
                ovalrect.bottom = Math.max(ptStart.y, ptstop.y);
                if (ovalrect.top == ovalrect.bottom || ovalrect.left == ovalrect.right) {
                    cvs.drawLine(ovalrect.left, ovalrect.top, ovalrect.right, ovalrect.bottom, linePaint);
                } else {
                    cvs.drawRect(ovalrect, linePaint);
                }
                PointF sendPf = new PointF();
                sendPf.x = ptstop.x;
                sendPf.y = ptStart.y;
            }
            break;
            case pa_Text: {   //文本
                TextPaint textPaint = new TextPaint();
                textPaint.setColor(tl_pa.nPenColor);
                textPaint.setTextSize(tl_pa.nPenWidth);
                PointF ptStart = unRelativePoint(tl_pa.alActionPoint.get(0));
                cvs.save();
                if (ptStart == null) {
                    break;
                }

                StaticLayout layout;
                if (cvs.getWidth() > ptStart.x) {
                    layout = new StaticLayout(tl_pa.sText, textPaint, (int) (cvs.getWidth() - ptStart.x), Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
                } else {
                    layout = new StaticLayout(tl_pa.sText, textPaint, cvs.getWidth(), Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true); //闂佽法鍠愰弸濠氬箯閻戣姤鏅搁柡鍌樺�栫�氾拷
                }
                cvs.translate(ptStart.x, ptStart.y);
                layout.draw(cvs);
                cvs.restore();
            }
            break;
            case pa_arrowLine: {   //箭头
                PointF ptStart = null, ptstop = null;
                if (tl_pa.alActionPoint.size() == 2) {
                    ptStart = unRelativePoint(tl_pa.alActionPoint.get(0));
                    ptstop = unRelativePoint(tl_pa.alActionPoint.get(1));
                } else if (tl_pa.alActionPoint.size() == 1) {
                    ptStart = unRelativePoint(tl_pa.alActionPoint.get(0));
                    ptstop = unRelativePoint(tl_pa.paMoveEndPoint);
                }

                if (ptStart == null || ptstop == null) {
                    break;
                }

                getArrowPath(cvs, ptStart, ptstop, tl_pa.nPenColor, tl_pa.nPenWidth);
            }
            break;
            default:
                break;
        }
        linePaint.setXfermode(null);
        linePaint.setStyle(Paint.Style.STROKE);
    }


    /**
     * 转换为当前view绘制区域坐标点 三端统一高度为960 如果是默认16：9 其他以获取宽高为主
     * 钢笔和橡皮擦转换方法
     *
     * @param pointF
     * @return
     */
    private PointF UnWhithXYLinePath(PointF pointF) {
        double hight = 960 * 1.0;
        double width = hight / mViewHeight * mViewWidth;
        PointF a1 = new PointF();
        a1.x = (float) (pointF.x / width);
        a1.y = (float) (pointF.y / hight);
        return a1;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int nAction = event.getAction();
        boolean bhandle = true;
        switch (nAction) {
            case MotionEvent.ACTION_DOWN:
                bhandle = OnTouchDown(event);
                break;
            case MotionEvent.ACTION_UP:
                bhandle = OnTouchUp(event);
                break;
            case MotionEvent.ACTION_MOVE: {
                bhandle = OnTouchMove(event);
                if (bhandle) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            }
        }
        if (bhandle) {
            getParent().requestDisallowInterceptTouchEvent(true);
            return true;
        }
        return super.onTouchEvent(event);
    }


    public boolean OnTouchDown(MotionEvent event) {
        //无画笔类型
        if (mToolsType == ToolsType.defaule) {
            return true;
        }
        //笔
        else if (mToolsType == ToolsType.pen) {
            if (mToolsPenType == ToolsPenType.fountainPen) {
                mActionMode = PaintActionBean.PAType.pa_markerPen;
                mActionfill = true;
            } else if (mToolsPenType == ToolsPenType.nitePen) {
                mActionMode = PaintActionBean.PAType.pa_markerPen;
                mActionfill = false;
            } else if (mToolsPenType == ToolsPenType.line) {
                mActionMode = PaintActionBean.PAType.pa_line;
                mActionfill = true;
            } else if (mToolsPenType == ToolsPenType.arrows) {
                mActionMode = PaintActionBean.PAType.pa_arrowLine;
                mActionfill = true;
            }
            mPenWidth = mToolsPenProgress;
            mPenColor = mToolsPenColor;
        }
        //文字
        else if (mToolsType == ToolsType.font) {
            mActionMode = PaintActionBean.PAType.pa_Text;
            mActionfill = true;
            mPenWidth = mToolsFontSize;
            mPenColor = mToolsFontColor;
        }
        //图形
        else if (mToolsType == ToolsType.form) {
            if (mToolsFormType == ToolsFormType.hollow_rectangle) {
                mActionMode = PaintActionBean.PAType.pa_Rectangle;
                mActionfill = false;
            } else if (mToolsFormType == ToolsFormType.solid_rectangle) {
                mActionMode = PaintActionBean.PAType.pa_Rectangle;
                mActionfill = true;
            } else if (mToolsFormType == ToolsFormType.hollow_circle) {
                mActionMode = PaintActionBean.PAType.pa_Ellipse;
                mActionfill = false;
            } else if (mToolsFormType == ToolsFormType.solid_circle) {
                mActionMode = PaintActionBean.PAType.pa_Ellipse;
                mActionfill = true;
            }
            mPenWidth = mToolsFormWidth;
            mPenColor = mToolsFormColor;
        }
        //橡皮擦
        else if (mToolsType == ToolsType.eraser) {
            mActionMode = PaintActionBean.PAType.pa_Eraser;
            mActionfill = true;
            mPenWidth = mToolsEraserWidth;
        }

        if (mActionMode == null) return true;

        float downX = event.getX();
        float downY = event.getY();
        switch (mActionMode) {
            case pa_markerPen:
            case pa_arrowLine:
            case pa_line:
            case pa_Rectangle:
            case pa_Ellipse:
            case pa_Eraser: {
                if (mCurrentPaintActionBean == null) {
                    mCurrentPaintActionBean = new PaintActionBean();
                    mCurrentPaintActionBean.nActionMode = mActionMode;

                    double penwidth = mPenWidth * 1.0 * 60 / 100;
                    mCurrentPaintActionBean.nPenWidth = (int) penwidth;
                    if (mActionMode == PaintActionBean.PAType.pa_markerPen && !mActionfill) {
                        Integer[] rgb = ColorUtils.RGB(mPenColor);
                        String argb = ColorUtils.toHexArgb(80, rgb[0], rgb[1], rgb[2]);
                        mCurrentPaintActionBean.nPenColor = Color.parseColor(argb);
                    } else {
                        mCurrentPaintActionBean.nPenColor = mPenColor;
                    }

                    mCurrentPaintActionBean.bIsFill = mActionfill;

                    mCurrentPaintActionBean.alActionPoint.add(relativePoint(new PointF(downX, downY)));
                }
            }
            break;
            case pa_Text: {
                insertText(downX, downY);
            }
            break;
            default:
                break;
        }
        return true;
    }

    public boolean OnTouchMove(MotionEvent event) {
        if (mActionMode == null) return true;

        if (mToolsType != ToolsType.defaule) {
            float moveX = event.getX();
            float moveY = event.getY();
            switch (mActionMode) {
                case pa_arrowLine:
                case pa_line:
                case pa_Rectangle:
                case pa_Ellipse: {
                    if (mCurrentPaintActionBean != null) {
                        mCurrentPaintActionBean.paMoveEndPoint = relativePoint(new PointF(moveX, moveY));
                        this.invalidate();
                    }
                }
                break;
                case pa_Eraser:
                case pa_markerPen: {
                    if (mCurrentPaintActionBean != null) {
                        mCurrentPaintActionBean.alActionPoint.add(relativePoint(new PointF(moveX, moveY)));
                        this.invalidate();
                    }
                }
                break;
                default:
                    break;

            }
        }
        return true;
    }

    public boolean OnTouchUp(MotionEvent event) {
        if (mActionMode == null) return true;
        float upX = event.getX();
        float upY = event.getY();
        switch (mActionMode) {
            case pa_markerPen:
            case pa_arrowLine:
            case pa_line:
            case pa_Rectangle:
            case pa_Ellipse:
            case pa_Eraser: {
                if (mCurrentPaintActionBean != null) {
                    mCurrentPaintActionBean.alActionPoint.add(relativePoint(new PointF(upX, upY)));
                    paintActionBeans.add(mCurrentPaintActionBean);
                    mCurrentPaintActionBean = null;
                    this.invalidate();
                }
            }
            break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        m_rcBK.left = w;
        m_rcBK.bottom = h;
    }

    /**
     * 文字输入pop
     *
     * @param x
     * @param y
     */
    public void insertText(final float x, final float y) {

        m_orgRcBK = new RectF(m_rcBK);

        //TODO 输入文字
    }

    /**
     * 文字输入
     *
     * @param strtext
     * @param x
     * @param y
     */
    public void onInsertText(String strtext, float x, float y) {
        mCurrentPaintActionBean = new PaintActionBean();
        mCurrentPaintActionBean.nActionMode = PaintActionBean.PAType.pa_Text;
        double penwidth = mPenWidth * 1.0 * 60 / 100;
        mCurrentPaintActionBean.nPenWidth = (int) penwidth;
        mCurrentPaintActionBean.nPenColor = mPenColor;
        mCurrentPaintActionBean.bIsFill = mActionfill;
        mCurrentPaintActionBean.alActionPoint = new ArrayList<PointF>();
        mCurrentPaintActionBean.alActionPoint.add(relativePoint(new PointF(x, y)));
        mCurrentPaintActionBean.sText = strtext;
        this.invalidate();
        mCurrentPaintActionBean = null;
    }

    /**
     * 箭头 三角形 x 轴长度
     * (pH + H) / (pX + X) = pH / pX  --> X = (pH + H) * pX / pH - pX
     * <p>
     * 箭头 三角形 y 轴长度
     * (pH + H) / (pY + Y) = pH / pY  --> Y = (pH + H) * pY / pH - pY
     */
    public void getArrowPath(Canvas cvs, PointF aF, PointF bF, int penColor, int width) {
        final float normalarrowsize = width /** dbZoomScale*/;

        // 底边的一半
        double L = normalarrowsize;
        // 箭头高度
        double H = L * 4.5 / 3;
        int x3 = 0;
        int y3 = 0;
        int x4 = 0;
        int y4 = 0;
        // 箭头角度
        double awrad = Math.atan(L / H);
        // 箭头的长度
        double arraow_len = Math.sqrt(L * L + H * H);

        //计算箭头三角形顶点的位置
        double pH = Math.sqrt((bF.x - aF.x) * (bF.x - aF.x) + (bF.y - aF.y) * (bF.y - aF.y));
        double x = (H + pH) * (bF.x - aF.x) / pH - (bF.x - aF.x);
        double y = (H + pH) * (bF.y - aF.y) / pH - (bF.y - aF.y);
        //箭头三角形顶点坐标
        float endX = (float) (bF.x + x);
        float endY = (float) (bF.y + y);

        double[] arrXY_1 = rotateVec(endX - aF.x, endY - aF.y, awrad, true, arraow_len);
        double[] arrXY_2 = rotateVec(endX - aF.x, endY - aF.y, -awrad, true, arraow_len);
        double x_3 = endX - arrXY_1[0]; // (3,y3)是第一端点
        double y_3 = endY - arrXY_1[1];
        double x_4 = endX - arrXY_2[0]; // (x4,y4)是第二端点
        double y_4 = endY - arrXY_2[1];
        Double X3 = new Double(x_3);
        x3 = X3.intValue();
        Double Y3 = new Double(y_3);
        y3 = Y3.intValue();
        Double X4 = new Double(x_4);
        x4 = X4.intValue();
        Double Y4 = new Double(y_4);
        y4 = Y4.intValue();

        // 画线
        Paint linePaint = new Paint();
        linePaint.setColor(penColor);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStrokeWidth(normalarrowsize);
        cvs.drawLine(aF.x, aF.y, bF.x, bF.y, linePaint);

        //画三角形
        Path triangle = new Path();
        triangle.moveTo(endX, endY);
        triangle.lineTo(x3, y3);
        triangle.lineTo(x4, y4);
        triangle.close();

        Paint trianglePaint = new Paint();
        trianglePaint.setColor(penColor);
        trianglePaint.setAntiAlias(true);

        cvs.drawPath(triangle, trianglePaint);

    }

    // 计算
    public double[] rotateVec(double px, double py, double ang, boolean isChLen, double newLen) {
        double mathstr[] = new double[2];
        // 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度、新长度
        double vx = px * Math.cos(ang) - py * Math.sin(ang);
        double vy = px * Math.sin(ang) + py * Math.cos(ang);

        if (isChLen) {
            double d = Math.sqrt(vx * vx + vy * vy);
            vx = vx / d * newLen;
            vy = vy / d * newLen;
            mathstr[0] = vx;
            mathstr[1] = vy;
        }
        return mathstr;
    }


    /**
     * 计算当前坐标在区域内坐标比值
     *
     * @param point
     * @return
     */
    public PointF relativePoint(PointF point) {
        PointF real = new PointF();
        //是否是文字输入坐标
        if (this.m_orgRcBK != null) {
            real.x = (point.x - m_orgRcBK.left) / m_orgRcBK.width();
            real.y = (point.y - m_orgRcBK.top) / m_orgRcBK.height();
            m_orgRcBK = null;
        } else {
            real.x = (point.x - m_rcBK.left) / m_rcBK.width();
            real.y = (point.y - m_rcBK.top) / m_rcBK.height();
        }
        return real;
    }


    //从百分比转换成位置
    public PointF unRelativePoint(PointF point) {
        PointF real = new PointF();
        if (point != null) {
            real.x = m_rcBK.left + m_rcBK.width() * point.x;//(0.0,168.0,480.0,648.0)
            real.y = m_rcBK.top + m_rcBK.height() * point.y;//(0,168)
        }
        return real;
    }

    /**
     * 钢笔 橡皮  以多点为主的画笔 转换
     *
     * @param tl_pa
     * @return
     */
    public Path getMarkPenPath(PaintActionBean tl_pa) {
        if (tl_pa.nActionMode == PaintActionBean.PAType.pa_markerPen || tl_pa.nActionMode == PaintActionBean.PAType.pa_Eraser) {
            Path lines = new Path();
            float x = 0, y = 0;
            for (int i = 0; i < tl_pa.alActionPoint.size(); i++) {
                PointF ptf = unRelativePoint(tl_pa.alActionPoint.get(i));

                if (i == 0) {
                    lines.moveTo(ptf.x, ptf.y);
                    x = ptf.x;
                    y = ptf.y;
                } else {
//                                        lines.lineTo(ptf.x, ptf.y);
                    lines.quadTo(x, y, (ptf.x + x) / 2, (ptf.y + y) / 2);
                    x = ptf.x;
                    y = ptf.y;
                }
            }
            return lines;
        }
        return null;
    }


    /**
     * 设置绘制类型
     *
     * @param type
     */
    public void setToolsType(ToolsType type) {
        this.mToolsType = type;
    }

    /**
     * 画笔类型
     *
     * @param mToolsPenType
     */
    public void setmToolsPenType(ToolsPenType mToolsPenType) {
        this.mToolsPenType = mToolsPenType;
    }

    /**
     * 画笔颜色
     *
     * @param mToolsPenColor
     */
    public void setmToolsPenColor(int mToolsPenColor) {
        this.mToolsPenColor = mToolsPenColor;
    }

    /**
     * 画笔大小
     *
     * @param mToolsPenProgress
     */
    public void setmToolsPenProgress(int mToolsPenProgress) {
        this.mToolsPenProgress = mToolsPenProgress;
    }

    /**
     * 文字颜色
     *
     * @param mToolsFontColor
     */
    public void setmToolsFontColor(int mToolsFontColor) {
        this.mToolsFontColor = mToolsFontColor;
    }

    /**
     * 文字大小
     *
     * @param mToolsFontSize
     */
    public void setmToolsFontSize(int mToolsFontSize) {
        this.mToolsFontSize = mToolsFontSize;
    }

    /**
     * 形状类型
     *
     * @param mToolsFormType
     */
    public void setmToolsFormType(ToolsFormType mToolsFormType) {
        this.mToolsFormType = mToolsFormType;
    }

    /**
     * 形状颜色
     *
     * @param mToolsFormColor
     */
    public void setmToolsFormColor(int mToolsFormColor) {
        this.mToolsFormColor = mToolsFormColor;
    }

    /**
     * 形状宽度
     *
     * @param mToolsFormWidth
     */
    public void setmToolsFormWidth(int mToolsFormWidth) {
        this.mToolsFormWidth = mToolsFormWidth;
    }

    /**
     * 橡皮檫宽度
     *
     * @param mToolsEraserWidth
     */
    public void setmToolsEraserWidth(int mToolsEraserWidth) {
        this.mToolsEraserWidth = mToolsEraserWidth;
    }

    //清除画笔数据
    public void cleanActions() {
        paintActionBeans.clear();
        mCurrentPaintActionBean = null;
        postInvalidate();
    }
}




