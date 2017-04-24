package cn.forwindz.news.NetPicListview;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.forwindz.news.MainActivity;
import cn.forwindz.news.R;
import cn.forwindz.news.tools.GetData;


/**
 * Created by Forwindz on 2017/4/15.
 * Adapter
 */

public class MyAdapter extends BaseAdapter{


    private LayoutInflater mInflater;
    List<Map<String,String>> data=new ArrayList<>();
    List<Map<String,String>> tempData= new ArrayList<>();
    private ImageLoad imageLoad;
    private Context context;

    public MyAdapter(Context context){
        mInflater = LayoutInflater.from(context);
        refreshData();
        imageLoad = new ImageLoad();
        this.context=context;
    }


    private static class ViewHolder{
        ImageView imageView;
        TextView title;
    }

    @Override
    public int getCount() {
        return data==null?0:data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.items,parent,false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView_item_pic);
            holder.title= (TextView) convertView.findViewById(R.id.textView_item_head);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(data.get(position).get("head"));
        imageLoad.showImage(holder.imageView,data.get(position).get("pic"));
        return convertView;
    }

    ImageLoad getImageLoad() {
        return imageLoad;
    }

    public void setData(List<Map<String,String>> newData){
        //tempData=data;
        this.data=newData;
        notifyDataSetChanged();
    }

    public void setOldData(){
        data=tempData;
        notifyDataSetChanged();
    }

    //getData===============================================================================

    private GetDataAsyncTask task;

    public void refreshData(){
        if (task!=null){
            task.cancel(false);
        }
        task=new GetDataAsyncTask();
        task.execute("");
    }

    public void showNotification(String text){
        //android.support.v7.app.NotificationCompat.Builder
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.news_icon)
                        .setContentTitle("最新新闻>>>")
                        .setContentText(text);
        mBuilder.setAutoCancel(true);

        Intent resultIntent = new Intent(context,
                MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                context, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

    private class GetDataAsyncTask extends AsyncTask<String, Void, List<Map<String,String>>> {

        @Override
        protected List<Map<String,String>> doInBackground(String... params) {
            return GetData.getListData();
        }

        @Override
        protected void onPostExecute(List<Map<String,String>> newData) {
            super.onPostExecute(newData);
            imageLoad.setData(newData);
            data=newData;
            tempData=newData;
            notifyDataSetChanged();
            showNotification(newData.get(0).get("head"));
        }
    }

}
