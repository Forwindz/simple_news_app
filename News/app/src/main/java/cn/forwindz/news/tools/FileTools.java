package cn.forwindz.news.tools;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/17.
 *
 */

public class FileTools {

    public static final String FILE_PATH = "/myNew/";
    public static String INTERNAL_PATH = null;

    private static void checkDir(String dir) {
        File temp = new File(dir);
        temp.mkdir();
    }

    public static boolean savePictures(Drawable drawable, String filename) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            checkDir(Environment.getExternalStorageDirectory() + FILE_PATH);
            File file = new File(Environment.getExternalStorageDirectory() + FILE_PATH,

                    filename.replace("/", "").replace("\\", "").replace("-", "")
                            .replace(" ", "").replace(".", "").replace("sinaimg", "")
                            .substring(21).replace("jpg", "")
                            + ".png");
            if (file.exists()) {
                return false;
            }
            BitmapDrawable bd = (BitmapDrawable) drawable;
            try {
                Log.d("saveFile", file + "");
                //file.mkdir();
                file.createNewFile();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bd.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, baos);

                FileOutputStream fs = new FileOutputStream(file);
                fs.write(baos.toByteArray());
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static final String PREFERENCES_LIST =  "/list/";

    public static void saveList(List<Map<String, String>> data) {
        checkDir(INTERNAL_PATH +PREFERENCES_LIST);
        File file = new File(INTERNAL_PATH +PREFERENCES_LIST + "list.dat");
        PrintStream ps = null;
        try {
            if (!file.exists())
                file.createNewFile();
            ps = new PrintStream(new FileOutputStream(file));
            ps.println(""+data.size());
            //Log.d("save",""+data.size());
            if (data.size() != 0)
                for (Map<String, String> map : data) {
                    ps.println(map.get("head").replace('\n',' '));
                    //Log.d("save",map.get("head").replace('\n',' '));
                    ps.println(map.get("link"));
                    //Log.d("save",map.get("link"));
                    ps.println(map.get("pic"));
                    //Log.d("save",map.get("pic"));
                }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    @Nullable
    public static List<Map<String, String>> readList() {
        checkDir(INTERNAL_PATH + PREFERENCES_LIST);
        File file = new File(INTERNAL_PATH +PREFERENCES_LIST + "list.dat");
        if (!file.exists()) return null;

        boolean success = false;
        List<Map<String, String>> data = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), "UTF-8"));
            int size = Integer.valueOf(br.readLine());
            data = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                HashMap<String, String> map = new HashMap<>(3);
                map.put("head", br.readLine());
                map.put("link", br.readLine());
                map.put("pic", br.readLine());
                data.add(map);
            }
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (success) {
            return data;
        } else {
            return new ArrayList<>();
        }
    }

    public static boolean checkExistCollection(String url) {
        List<Map<String, String>> a = readList();
        if (a==null)return false;
        for (Map<String, String> map : a) {
            String link = map.get("link");
            if (url.equals(link)) return true;
        }
        return false;
    }

    @org.jetbrains.annotations.Contract("null -> false")
    public static boolean appendList(Map<String, String> map) {
        if (map==null)return false;
        List<Map<String,String>> list=readList();
        if (list==null)list = new ArrayList<>(1);
        list.add(map);
        saveList(list);
        return true;/*
        checkDir(INTERNAL_PATH + PREFERENCES_LIST);
        File file = new File(INTERNAL_PATH + PREFERENCES_LIST + "list.dat");
        if (!file.exists()) {
            saveList(new ArrayList<Map<String, String>>(0));
        }
        PrintStream ps = null;
        try {
            //file.createNewFile();
            ps = new PrintStream(new FileOutputStream(file));
            ps.append(map.get("head")).append('\n');
            ps.append(map.get("link")).append('\n');
            ps.append(map.get("pic")).append('\n');
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
        return false;*/
    }

    public static void removeURL(String url) {
        List<Map<String, String>> a = readList();
        if (a==null)return;
        Map temp = null;
        for (Map<String, String> map : a) {
            String link = map.get("link");
            if (url.equals(link)) {
                temp = map;
                break;
            }
        }
        if (temp != null) {
            a.remove(temp);
            saveList(a);
        }
    }
}
