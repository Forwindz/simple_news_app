package cn.forwindz.news.tools;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import cn.forwindz.news.R;

/**
 * Created by Forwindz on 2017/4/16.
 * Special solution for loading pictures from Internet.
 */

public class URLDrawable extends BitmapDrawable {

    public Bitmap bitmap;
    public static Drawable drawable;
    //protected Drawable drawable;

    @Override
    public void draw(@NonNull Canvas canvas) {
        //Log.d("draw","draw!!!");
        if(bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, getPaint());
        }else{
            if (drawable!=null)
            drawable.draw(canvas);
        }
    }

}
