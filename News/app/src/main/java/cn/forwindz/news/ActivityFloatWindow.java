package cn.forwindz.news;

import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import static android.view.WindowManager.*;

/**
 * Created by Forwindz on 2017/4/17.
 * 用于悬浮显示
 */

public class ActivityFloatWindow {

    public LayoutParams layoutParams;
    public ViewGroup view;
    public WindowManager windowManager;
    private boolean isRunning;

    public ActivityFloatWindow(WindowManager wm, ViewGroup v) {
        this.windowManager = wm;
        this.view = v;
        layoutParams = new LayoutParams();
        layoutParams.type = LayoutParams.TYPE_APPLICATION;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = LayoutParams.MATCH_PARENT;
        layoutParams.height = LayoutParams.MATCH_PARENT;
    }

    public void showWindow() {
        if (isRunning)return;
        windowManager.addView(view, layoutParams);
        isRunning=true;
    }

    public void updateWindow() {
        if (!isRunning)return;
        windowManager.updateViewLayout(view, layoutParams);
    }

    public void removeWindow() {
        if (!isRunning)return;
        windowManager.removeView(view);
        isRunning=false;
    }

    public boolean isRunning(){
        return isRunning;
    }

    public void setOnClickListener(View.OnClickListener ocl) {
        view.setOnClickListener(ocl);
        for(int i=0;i<view.getChildCount();i++){
            View v = view.getChildAt(i);
            v.setOnClickListener(ocl);
        }
    }

    public void setOnTouchListener(View.OnTouchListener otl) {
        view.setOnTouchListener(otl);
    }
}
