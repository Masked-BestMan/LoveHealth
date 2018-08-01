package com.zbm.lovehealth;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;

import java.util.List;
import java.util.Map;

public abstract class AbstractDataAgent {
    public final static int TRANSMISSION_ERROR=0;
    public final static int CONNECTION_ERROR=1;

    protected Context context;
    protected IDataRequestFeedback callback;
    public AbstractDataAgent(IDataRequestFeedback callback){
        this.callback=callback;
        if (callback instanceof Fragment)
            context=((Fragment)callback).getActivity().getApplicationContext();
        else
            context= ((Context) callback).getApplicationContext();
    }

    /**
     * Activity在onCreate中调用
     * Fragment在onActivityCreated()中调用
     * @param params 请求参数
     */
    public abstract void requestData(Map<String,String> params);

    /**
     * 如果网络传输出错，该方法会返回null
     * @param response json数据
     * @return 解析实体列表，列表数量不定
     */
    protected abstract List<AbstractDataBean> parseResult(String response);

    /**
     * Activity在onDestroy()中调用
     * Fragment在onDestroyView()中调用
     */
    public abstract void stopDataRequest();

    /**
     * 验证网络是否连接
     * @param context .
     * @return .
     */
    public boolean isNetworkAvailable(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);

        if (manager == null) {
            return false;
        }

        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        return networkinfo != null && networkinfo.isAvailable();
    }
}
