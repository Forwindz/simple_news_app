package cn.forwindz.news.NetPicListview;

import android.app.TaskStackBuilder;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.forwindz.news.R;

import static cn.forwindz.news.tools.HtmlTools.getBitmapFromUrl;

/**
 * Created by Forwindz on 2017/4/16.
 * 加载图片
 */

public class ImageLoad {



    private int maxMemory = (int) Runtime.getRuntime().maxMemory();
    private int cacheSizes = maxMemory/6;
    private List<Map<String,String>> data;
    private ListView listView;
    private LinkedList<NewsAsyncTask> tasks= new LinkedList<>();

    /*public ImageLoad(List<Map<String,String>> data){
        this.data=data;
        //this.listView=listView;
    }*/

    public void setData(List<Map<String,String>> data){
        this.data=data;
    }

    public void setListView(ListView listView){
        this.listView=listView;
    }
    private LruCache<String,Bitmap> mMemoryCaches= new LruCache<String,Bitmap>(cacheSizes){
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount();
        }
    };
/*
    public void showImageByAsyncTask(ImageView imageView, String url) {
        if (imageView.getTag().equals(url)) return;//正在加载或已经加载，不需要再次加载
        imageView.setTag(url);
        Bitmap temp=mMemoryCaches.get(url);
        if (temp!=null){
            imageView.setImageBitmap(temp);
            return;
        }
        new NewsAsyncTask(imageView, url).execute(url);
    }*/

    public void loadImages(int start,int end){
        for(int i=start;i<end;i++){
            String url = data.get(i).get("pic");
            if (mMemoryCaches.get(url) != null) {
                ImageView imageView = (ImageView) listView
                        .findViewWithTag(url);
                if(imageView!=null)
                imageView.setImageBitmap(mMemoryCaches.get(url));
            } else {
                NewsAsyncTask mNewsAsyncTask = new NewsAsyncTask(url);
                tasks.add(mNewsAsyncTask);
                mNewsAsyncTask.execute(url);
            }
        }
    }

    public void cancelAllAsyncTask() {
        if (tasks != null) {
            for (NewsAsyncTask newsAsyncTask : tasks) {
                newsAsyncTask.cancel(false);
            }
            tasks.clear();
        }
    }

    public void showImage(ImageView imageView, String url) {

        Bitmap bitmap =mMemoryCaches.get(url);
        imageView.setTag(url);
        if (bitmap == null) {
            imageView.setImageResource(R.drawable.blank_picture);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    private class NewsAsyncTask extends AsyncTask<String, Void, Bitmap> {

        //private ImageView mImageView;
        private String mUrl;

        NewsAsyncTask(String url) {
            //this.mImageView = imageView;
            mUrl = url;
        }

        //String...params是可变参数接受execute中传过来的参数
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap=getBitmapFromUrl(params[0]);
            if (bitmap!=null){
                mMemoryCaches.put(mUrl,bitmap);
            }
            return bitmap;
        }

        //这里的bitmap是从doInBackgroud中方法中返回过来的
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView imageView = (ImageView) listView
                    .findViewWithTag(mUrl);
            if (imageView!=null)
                imageView.setImageBitmap(mMemoryCaches.get(mUrl));
            //if (!mUrl.equals(mImageView.getTag())) {
            //    mImageView.setImageBitmap(bitmap);
            //}
        }
    }
}
