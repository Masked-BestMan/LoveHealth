package com.zbm.lovehealth.detail;


import com.zbm.lovehealth.AbstractDataAgent;
import com.zbm.lovehealth.AbstractDataBean;
import com.zbm.lovehealth.utils.CacheUtil;
import com.zbm.lovehealth.utils.HttpUtil;
import com.zbm.lovehealth.IDataRequestFeedback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DetailDataAgent extends AbstractDataAgent {

    public DetailDataAgent(IDataRequestFeedback callback) {
        super(callback);
    }

    @Override
    public void requestData(final Map<String, String> params) {
        if (isNetworkAvailable(context)) {
            HttpUtil.getHttpUtil().retrieveDataFromServer("http://route.showapi.com/90-88", params, new HttpUtil.OnReceiveDataListener() {
                @Override
                public void onReceive(String response) {
                    List<AbstractDataBean> dataBeans = parseResult(response);
                    if (dataBeans!=null){
                        CacheUtil.getCacheUtilInstance().addObjectToCache(params.get("id"), dataBeans.get(0));
                        callback.onReceiveData(dataBeans);
                    }else
                        callback.onReceiveError(TRANSMISSION_ERROR);
                }

                @Override
                public void onError() {
                    callback.onReceiveError(CONNECTION_ERROR);
                }
            });
        } else {

            AbstractDataBean dataBean = CacheUtil.getCacheUtilInstance().getObjectFromMemory(params.get("id"));
            if (dataBean == null){
                dataBean = CacheUtil.getCacheUtilInstance().getObjectFromDisk(params.get("id"));
            }
            if (dataBean == null) {
                callback.onReceiveError(CONNECTION_ERROR);  //无数据，说明detail模块还未访问过网络
                return;
            }
            List<AbstractDataBean> dataBeans = new ArrayList<>();
            dataBeans.add(dataBean);
            callback.onReceiveData(dataBeans);
        }

    }


    @Override
    protected List<AbstractDataBean> parseResult(String response) {
        List<AbstractDataBean> beans = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(response);
            JSONObject body = object.getJSONObject("showapi_res_body");
            JSONObject item = body.getJSONObject("item");
            String img = item.getString("img");
            String categoryName = item.getString("tname");
            String time = item.getString("ctime");
            String content = item.getString("content");
            String id = item.getString("id");
            String mediaName;
            if (item.has("media_name"))
                mediaName = item.getString("media_name");
            else
                mediaName = "不详";
            String wapUrl=item.getString("wapurl");
            String keyWords=item.getString("keywords");
            if (content.equals("")&&item.has("images")) {
                List<String> imagesList=new ArrayList<>();
                JSONArray array=item.getJSONArray("images");
                for (int i=0;i<array.length();i++){
                    JSONObject imageObject=array.getJSONObject(i);
                    imagesList.add(imageObject.getString("u"));
                }
                content = imagesToHtml(imagesList);
            }
            beans.add(new DetailDisplayBean(id, categoryName,mediaName, time, content, img,keyWords,wapUrl));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return beans;
    }

    private String imagesToHtml(List<String> list){
        StringBuilder sb=new StringBuilder();
        sb.append("<center>图集</center> \n ");
        for (String s:list)
            sb.append("\n<img src=\"").append(s).append("\"> \n");
        return sb.toString();
    }

    @Override
    public void stopDataRequest() {
        HttpUtil.getHttpUtil().stopDataRequest();
    }
}
