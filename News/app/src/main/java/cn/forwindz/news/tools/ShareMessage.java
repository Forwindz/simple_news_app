package cn.forwindz.news.tools;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/4/18.
 */

public class ShareMessage {
    public static void shareText(Context context,String text) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setType("text/plain");
        context.startActivity(intent);
    }

    public static void shareImage(Context context,Bitmap bitmap) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, bitmap);
        intent.setType("image/*");
        context.startActivity(intent);
    }
}
