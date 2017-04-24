package cn.forwindz.news.NetPicListview;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import cn.forwindz.news.NewsActivity;
import cn.forwindz.news.tools.SerializableMap;

/**
 * Created by Administrator on 2017/4/16.
 */

public class NetPicListview extends ListView implements ListView.OnScrollListener,ListView.OnItemClickListener{

    private ImageLoad imageLoad;
    private MyAdapter myAdapter;

    public NetPicListview(Context context) {
        super(context);
        init();
    }

    public NetPicListview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NetPicListview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public NetPicListview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        setOnScrollListener(this);
        setOnItemClickListener(this);
    }

    public void setMyAdapter(MyAdapter myAdapter){
        setAdapter(myAdapter);
        imageLoad=myAdapter.getImageLoad();
        imageLoad.setListView(this);
        this.myAdapter =myAdapter;
    }

    //Scroll==============================================================

    private int mStart=0;
    private int mEnd=0;
    private boolean isFirstIn=true;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        if(scrollState==SCROLL_STATE_IDLE){
            imageLoad.loadImages(mStart,mEnd);
        }else{
            imageLoad.cancelAllAsyncTask();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        mStart=firstVisibleItem;
        mEnd=firstVisibleItem+visibleItemCount;

        if(isFirstIn&&visibleItemCount>0){
            imageLoad.loadImages(mStart,mEnd);
            isFirstIn=false;
        }

    }


    //ItemClick==========================================================

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getContext(), NewsActivity.class);
        intent.putExtra("web-url",myAdapter.data.get(position).get("link"));
        intent.putExtra("map", new SerializableMap<>(myAdapter.data.get(position)));
        getContext().startActivity(intent);
    }

    //===================================================================
}
