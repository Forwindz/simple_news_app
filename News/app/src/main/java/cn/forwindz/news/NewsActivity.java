package cn.forwindz.news;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import cn.forwindz.news.tools.FileTools;
import cn.forwindz.news.tools.GetData;
import cn.forwindz.news.tools.LinkMovementMethodExt;
import cn.forwindz.news.tools.SerializableMap;
import cn.forwindz.news.tools.ShareMessage;
import cn.forwindz.news.tools.URLDrawable;
import cn.forwindz.news.tools.URLImageParser;

public class NewsActivity extends AppCompatActivity implements View.OnClickListener {

    GetNewsAsyncTask task;
    TextView textView;
    Context context = this;
    ActivityFloatWindow floatWindow;
    ImageView imageViewShowPic;
    FloatingActionButton fab;
    Map<String, String> map;
    String url;
    boolean isCollected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        url = intent.getStringExtra("web-url");
        map = ((SerializableMap) intent.getSerializableExtra("map")).myMap;
        String head = map.get("head");
        setTitle(head);
        isCollected = FileTools.checkExistCollection(url);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCollected) {
                    Snackbar.make(view, "收藏成功！", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    FileTools.appendList(map);
                } else {
                    Snackbar.make(view, "取消收藏了！", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    FileTools.removeURL(url);
                }
                isCollected = !isCollected;
                changeFabState();
            }
        });
        changeFabState();
        if (task != null) {
            task.cancel(false);
        }
        task = new GetNewsAsyncTask();
        task.execute(url);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        floatWindow = new ActivityFloatWindow(getWindowManager(),
                (ViewGroup) inflater.inflate(R.layout.window_float, null));
        floatWindow.setOnClickListener(this);
        imageViewShowPic = (ImageView) floatWindow.view.findViewById(R.id.imageView_show_image);

        textView = (TextView) findViewById(R.id.textview_news);
        textView.setMovementMethod(
                LinkMovementMethodExt.getInstance(
                        new MyHandler(floatWindow), ImageSpan.class));

        CollapsingToolbarLayout ctl = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout_news);
        ctl.setTitle(map.get("head"));
    }

    @Override
    public void onBackPressed() {
        if (floatWindow.isRunning()) {
            Log.d("Activity", "close");
            floatWindow.removeWindow();
            textView.setText(textView.getText());
        } else
            super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        floatWindow.removeWindow();
        imageViewShowPic.setImageDrawable(null);
        //floatWindow=null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_news_share) {
            ShareMessage.shareText(this,
                    "分享的新闻：" + url + "\n" +
                            textView.getText().subSequence(0, 60).toString().replace("原标题", "[") + "...]");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static final int CALL_REQUEST_CODE = 1;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private boolean requestStorage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, CALL_REQUEST_CODE);
            return false;
        }
        return true;
    }

    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CALL_REQUEST_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    savePic();
                } else {
                    Toast.makeText(this, "权限被拒绝，无法保存", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    public void savePic() {
        boolean b = FileTools.savePictures(imageViewShowPic.getDrawable(), (String) imageViewShowPic.getTag());
        if (b) {
            Toast.makeText(this, "保存完毕", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "保存失败（文件已存在或空间不足）", Toast.LENGTH_LONG).show();
        }
    }

    public void changeFabState() {
        if (isCollected) {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.star_full));
        } else {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.star));
        }
    }

    //OnClickListener===============================================

    @Override
    public void onClick(View v) {
        Log.d("click", "clicked!");
        int id = v.getId();
        switch (id) {
            case R.id.imageView_show_image:
                floatWindow.removeWindow();
                break;
            case R.id.imageView_image_share:

                //imageViewShowPic.setDrawingCacheEnabled(true);
                //Bitmap bitmap = Bitmap.createBitmap(imageViewShowPic.getDrawingCache());
                Drawable drawable = imageViewShowPic.getDrawable();
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

                //imageViewShowPic.setDrawingCacheEnabled(false);
                //Bitmap bitmap = imageViewShowPic.getDrawable();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                byte[] bytes = baos.toByteArray();
                Log.d("bitmapShare", bitmap.toString() + " bytes=" + bytes.length);
                ShareMessage.shareImage(this, BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                break;
            case R.id.imageView_image_download:
                if (requestStorage()) {
                    //Log.d("Permission", "has passed");
                    savePic();
                }
                break;
            case R.id.frameLayout_showImage:
                floatWindow.removeWindow();
                break;
        }
    }

    //==============================================================

    private static class MyHandler extends Handler {
        private static final String LOG_TAG = "Handler-ImageSpan";

        private ImageView image;
        private ActivityFloatWindow window;
        //private Activity activity;
        //private WeakReference<Activity> activity;

        public MyHandler(ActivityFloatWindow window) {
            this.window = window;
            image = (ImageView) window.view.findViewById(R.id.imageView_show_image);
            // this.activity=activity;
        }

        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == 200) {
                LinkMovementMethodExt.MessageSpan ms = (LinkMovementMethodExt.MessageSpan) msg.obj;
                Object[] spans = (Object[]) ms.getObj();
                for (Object span : spans) {
                    if (span instanceof ImageSpan) {
                        ImageSpan is = (ImageSpan) span;
                        Log.d(LOG_TAG, "点击了图片" + is.getSource());
                        //image.setDrawingCacheEnabled(true);
                        image.setImageBitmap(((URLDrawable) is.getDrawable()).bitmap);
                        //image.setImageBitmap(((BitmapDrawable)is.getDrawable()).getBitmap());
                        //image.invalidate();
                        image.setTag(is.getSource());
                        //if (activity.get()!=null&&!activity.get().isFinishing())
                        window.showWindow();
                    }
                }
            }
        }
    }

    //==============================================================

    private class GetNewsAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return GetData.getNewsContent(params[0]);
        }

        @Override
        protected void onPostExecute(String html) {
            super.onPostExecute(html);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                textView.setText(Html.fromHtml(html, 0,
                        new URLImageParser(textView, context, getResources()), null));
            } else {
                textView.setText(Html.fromHtml(html,
                        new URLImageParser(textView, context, getResources()), null));
            }
        }

    }
}
