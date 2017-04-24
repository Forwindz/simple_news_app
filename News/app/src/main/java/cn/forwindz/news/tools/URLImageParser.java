package cn.forwindz.news.tools;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/4/16.
 */

public class URLImageParser implements Html.ImageGetter {
    private Context context;
    private TextView textView;
    private Resources resources;

    public URLImageParser(TextView t, Context c, Resources res) {
        this.context = c;
        this.textView = t;
        this.resources=res;
    }

    @Override
    public Drawable getDrawable(String source) {
        URLDrawable urlDrawable = new URLDrawable();
        urlDrawable.setAlpha(255);

        // get the actual source
        ImageGetterAsyncTask asyncTask =
                new ImageGetterAsyncTask( urlDrawable);

        asyncTask.execute(source);

        // return reference to URLDrawable where I will change with actual image from
        // the src tag
        return urlDrawable;
    }

    public class ImageGetterAsyncTask extends AsyncTask<String, Void, Bitmap> {
        URLDrawable urlDrawable;

        public ImageGetterAsyncTask(URLDrawable d) {
            this.urlDrawable = d;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String source = params[0];
            return HtmlTools.getBitmapFromUrl(source);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // set the correct bound according to the result from HTTP call
            //Log.d("height",""+result.getIntrinsicHeight());
            //Log.d("width",""+result.getIntrinsicWidth());
            if (result==null)return;
            urlDrawable.setBounds(0, 0, result.getWidth(), result.getHeight());

            // change the reference of the current drawable to the result
            // from the HTTP call
            urlDrawable.bitmap = result;

            // redraw the image by invalidating the textView
            //textView.append("");
            URLImageParser.this.textView.invalidate();
            textView.setText(textView.getText());

            //URLImageParser.this.textView.setEllipsize(null);
        }

    }
}
