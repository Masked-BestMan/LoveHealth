package com.zbm.lovehealth.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.show.api.ShowApiRequest;

import java.lang.ref.WeakReference;
import java.util.Map;

public class HttpUtil {

    /**
     * 网络请求工具类
     *
     * 在无网络连接或连接上网络不能上网下会调用OnReceiveDataListener.onError()；
     * 该工具类只保证正确返回json数据时才调用onReceive()
     */
    private DataTask dataTask;
    private static HttpUtil httpUtil=null;
    private OnReceiveDataListener onReceiveDataListener;

    private HttpUtil(){}

    public static HttpUtil getHttpUtil() {
        if (httpUtil==null){
            httpUtil=new HttpUtil();
        }
        return httpUtil;
    }

    public void retrieveDataFromServer(String url,Map<String,String> params,OnReceiveDataListener onReceiveDataListener){
        this.onReceiveDataListener=onReceiveDataListener;
        ShowApiRequest apiRequest=new ShowApiRequest(url,"65330","1d4eac78973c438ca1d76235697fcd84");
        if (params!=null)
            for (Map.Entry<String,String> entry:params.entrySet())
                apiRequest.addTextPara(entry.getKey(),entry.getValue());
        dataTask=new DataTask(this);
        dataTask.execute(apiRequest);
    }

    public void stopDataRequest(){
        onReceiveDataListener=null;
        if (dataTask!=null){
            if (dataTask.getStatus()== AsyncTask.Status.RUNNING)
                dataTask.cancel(true);
            dataTask=null;
        }
    }

    private static class DataTask extends AsyncTask<ShowApiRequest,Void,String>{
        private WeakReference<HttpUtil> httpUtilWeakReference;

        DataTask(HttpUtil httpUtil){
            httpUtilWeakReference=new WeakReference<>(httpUtil);
        }
        @Override
        protected String doInBackground(ShowApiRequest... showApiRequests) {
            return showApiRequests[0].post();
        }

        @Override
        protected void onCancelled() {
            Log.d("DataTask","取消任务");
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("HttpUtil",s);
            if (httpUtilWeakReference.get().onReceiveDataListener!=null) {
                if (s != null&&!s.toLowerCase().contains("login"))
                    httpUtilWeakReference.get().onReceiveDataListener.onReceive(s);
                else
                    httpUtilWeakReference.get().onReceiveDataListener.onError();
            }
        }
    }


    public interface OnReceiveDataListener{
        void onReceive(String response);
        void onError();
    }
}
