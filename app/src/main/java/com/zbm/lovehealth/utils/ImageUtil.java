package com.zbm.lovehealth.utils;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.zbm.lovehealth.utils.CacheUtil;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ImageUtil {

    private static Map<String,ImageTask> imageLoadingTasks=new HashMap<>();
    private ImageUtil(){ }

//    /**
//     * 该方法必须在子线程中调用
//     */
//    public static Drawable getImageByUrl(String url){
//        Drawable drawable=CacheUtil.getCacheUtilInstance().getImageFromMemory(url);
//        if (drawable!=null){
//            return drawable;
//        }else{
//            try {
//                URL imageUrl = new URL(url);
//                drawable = Drawable.createFromStream(imageUrl.openStream(), "img");
//            } catch (Exception e) {
//                return null;
//            }
//            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable
//                    .getIntrinsicHeight());
//            CacheUtil.getCacheUtilInstance().addImageToCache(url,drawable);
//            return drawable;
//        }
//    }

    /**
     * 该方法会先从内存中获取图片，没有则从磁盘，最后再从网路获取（注：调用该方法已默认app联网,否则ImageView不会被设置）
     * @param url 图片的url地址
     * @param imageView 图片设置目标ImageView对象
     */
    public static void getImageByUrl(String url, ImageView imageView){
        imageView.setTag(url);
        Log.d("ImageUtil","url:"+url+" imageView:"+imageView);

        Drawable drawable= CacheUtil.getCacheUtilInstance().getImageFromMemory(url);
        if (drawable==null){
            drawable=CacheUtil.getCacheUtilInstance().getImageFromDisk(url);
            if (drawable==null){
                if (imageLoadingTasks==null)
                    imageLoadingTasks=new HashMap<>();
                //如果加载任务表已经有该url的任务则不添加
                if (!imageLoadingTasks.containsKey(url)){
                    ImageTask task=new ImageTask(imageView);
                    task.execute(url);
                    imageLoadingTasks.put(url,task);
                }
            }else{
                imageView.setImageDrawable(drawable);
            }

        }else
            imageView.setImageDrawable(drawable);
    }

    /**
     * 如果模块有使用到ImageUtil，则在退出最后一个使用该类的模块时调用该方法停止网络加载图片
     */
    public static void emptyImageTask(){
        if (imageLoadingTasks!=null){
            for (ImageTask task:imageLoadingTasks.values()){
                if (task.getStatus()== AsyncTask.Status.RUNNING)
                    task.cancel(true);
            }
            imageLoadingTasks.clear();
            imageLoadingTasks=null;
        }

    }

    private static class ImageTask extends AsyncTask<String,Void,Drawable>{
        private WeakReference<ImageView> reference;
        private String tag;
        ImageTask(ImageView imageView){
            reference=new WeakReference<>(imageView);
        }
        @Override
        protected Drawable doInBackground(String... strings) {
            URL imageURL;
            Drawable drawable=null;
            try{
                tag=strings[0];
                imageURL = new URL(strings[0]);
                //获得连接
                HttpURLConnection conn=(HttpURLConnection)imageURL.openConnection();
                //设置超时时间为6000毫秒，conn.setConnectionTime(0);表示没有时间限制
                conn.setConnectTimeout(6000);
                //连接设置获得数据流
                conn.setDoInput(true);
                //不使用缓存
                conn.setUseCaches(false);
                //这句可有可无，没有影响
                //conn.connect();
                //得到数据流
                InputStream is = conn.getInputStream();
                //解析得到图片
                drawable = Drawable.createFromStream(is,"img");
                //关闭数据流
                is.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            return drawable;
        }

        @Override
        protected void onPostExecute(Drawable result) {
            imageLoadingTasks.remove(tag);
            Log.d("Image","是否相等："+reference.get().getTag().equals(tag));
            if(result!=null) {
                if (reference.get().getTag().equals(tag))
                    reference.get().setImageDrawable(result);
                CacheUtil.getCacheUtilInstance().addImageToCache(tag, result);
            }
        }
    }
}
