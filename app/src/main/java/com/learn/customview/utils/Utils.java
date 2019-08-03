package com.learn.customview.utils;


import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class Utils {

    /**
     * 0xff000000 类型转 RGB
     * @param color
     * @return
     */
    public static String toRGB(int color){
        int alpha = (color & 0xff000000) >>> 24;
        int red = (color & 0x00ff0000) >> 16;
        int green = (color & 0x0000ff00) >> 8;
        int blue = (color & 0x000000ff);
        String hex = String.format("#%02X%02X%02X", red, green, blue);
        return hex;
    }


    public static Integer[] RGB(int color){
        int alpha = (color & 0xff000000) >>> 24;
        int red = (color & 0x00ff0000) >> 16;
        int green = (color & 0x0000ff00) >> 8;
        int blue = (color & 0x000000ff);
        Integer[] rgb = new Integer[3];
        rgb[0] = red;
        rgb[1] = green;
        rgb[2] = blue;
        return rgb;
    }

    /**
     * RGB转16进制
     * @param red
     * @param green
     * @param blue
     * @return
     */
    public static String toHex(int red, int green, int blue){
        String hr = Integer.toHexString(red);
        String hg = Integer.toHexString(green);
        String hb = Integer.toHexString(blue);
        return "#" + hr + hg + hb;
    }


    public static String toHexArgb(int alpha, int red, int green, int blue){
        String hex = String.format("#%d%02X%02X%02X",alpha, red, green, blue);
        return hex;
    }


    /**
     * 显示键盘
     * @param context
     * @param et
     */
    public static void showKeyBoard(Context context, EditText et) {
        if (context == null || et == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 隐藏键盘
     * @param context
     * @param et
     */
    public static void hideKeyBoard(Context context, EditText et) {
        if (context == null || et == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

}
