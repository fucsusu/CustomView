package com.learn.customview;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.learn.customview.view.PaintGroup;
import com.learn.customview.bean.ToolsFormType;
import com.learn.customview.bean.ToolsPenType;
import com.learn.customview.bean.ToolsType;

import java.util.Random;

public class MainActivity extends AppCompatActivity {


    public TextView penTypeContent;
    public PaintGroup paintGroup;
    //画笔的类型
    private int toolsType = 0;
    private ToolsType[] toolsTypes = {ToolsType.pen, ToolsType.form, ToolsType.eraser};
    //铅笔模式
    private int toolsPenType = 0;
    private ToolsPenType[] toolsPenTypes = {ToolsPenType.fountainPen, ToolsPenType.line, ToolsPenType.arrows, ToolsPenType.nitePen};
    //图形
    private int toolsFormType = 0;
    private ToolsFormType[] toolsFormTypes = {ToolsFormType.hollow_circle, ToolsFormType.solid_circle, ToolsFormType.hollow_rectangle, ToolsFormType.solid_rectangle};
    public Random random;
    public SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        penTypeContent = findViewById(R.id.pen_type_content);
        paintGroup = findViewById(R.id.wb_paint_canvas);
        seekBar = findViewById(R.id.wb_paint_width);
        showPaintContent();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                paintGroup.setmToolsPenProgress(seekBar.getProgress());
                paintGroup.setmToolsFormWidth(seekBar.getProgress());
                paintGroup.setmToolsEraserWidth(seekBar.getProgress());
            }
        });
    }

    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.wb_change_mode:
                setPaintMode();
                break;
            case R.id.wb_change_type:
                setPaintType();
                break;
            case R.id.wb_change_color:
                int randomColor = getRandomColor();
                paintGroup.setmToolsPenColor(randomColor);
                paintGroup.setmToolsFormColor(randomColor);
                break;
            case R.id.wb_clean:
                paintGroup.cleanActions();
                break;
        }
        showPaintContent();
    }

    //设置样式
    private void setPaintType() {
        switch (toolsType) {
            case 0://铅笔
                toolsPenType = (toolsPenType + 1) % toolsPenTypes.length;
                paintGroup.setmToolsPenType(toolsPenTypes[toolsPenType]);
                break;
            case 1://图形
                toolsFormType = (toolsFormType + 1) % toolsFormTypes.length;
                paintGroup.setmToolsFormType(toolsFormTypes[toolsFormType]);
                break;
        }
    }

    //设置模式
    private void setPaintMode() {
        toolsType = (toolsType + 1) % toolsTypes.length;
        paintGroup.setToolsType(toolsTypes[toolsType]);
    }


    public int getRandomColor() {
        if (random == null) {
            random = new Random();
        }
        int r = 0;
        int g = 0;
        int b = 0;
        for (int i = 0; i < 2; i++) {
            int temp = random.nextInt(16);
            r = r * 16 + temp;
            temp = random.nextInt(16);
            g = g * 16 + temp;
            temp = random.nextInt(16);
            b = b * 16 + temp;
        }
        return Color.rgb(r, g, b);
    }

    private void showPaintContent() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("模式：");
        switch (toolsType) {
            case 0:
                stringBuilder.append("画笔\n样式：");
                switch (toolsPenType) {
                    case 0:
                        stringBuilder.append("钢笔");
                        break;
                    case 1:
                        stringBuilder.append("直线");
                        break;
                    case 2:
                        stringBuilder.append("箭头");
                        break;
                    case 3:
                        stringBuilder.append("荧光笔");
                        break;
                }
                break;
            case 1:
                stringBuilder.append("图形\n样式：");
                switch (toolsFormType) {
                    case 0:
                        stringBuilder.append("空心圆");
                        break;
                    case 1:
                        stringBuilder.append("实心圆");
                        break;
                    case 2:
                        stringBuilder.append("空心矩形");
                        break;
                    case 3:
                        stringBuilder.append("实心矩形");
                        break;
                }
                break;
            case 2:
                stringBuilder.append("橡皮檫");
                break;
        }
        penTypeContent.setText(stringBuilder.toString());
    }
}
