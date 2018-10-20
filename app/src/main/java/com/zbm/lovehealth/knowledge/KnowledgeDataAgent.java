package com.zbm.lovehealth.knowledge;

import android.content.Context;
import android.content.SharedPreferences;

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

public class KnowledgeDataAgent extends AbstractDataAgent {
    public KnowledgeDataAgent(IDataRequestFeedback callback) {
        super(callback);

    }

    @Override
    public void requestData(final Map<String, String> params) {
        if (isNetworkAvailable(context)) {
            HttpUtil.getHttpUtil().retrieveDataFromServer("http://route.showapi.com/96-109", params, new HttpUtil.OnReceiveDataListener() {
                @Override
                public void onReceive(String response) {
                    if (checkResult(response)) {
                        List<AbstractDataBean> dataBeans = parseResult(response);
                        if (dataBeans!=null) {
                            int flag = dataBeans.size();
                            SharedPreferences.Editor editor = context.getSharedPreferences("Love Health", Context.MODE_PRIVATE).edit();
                            editor.putInt("knowledge_list_count", flag);
                            editor.apply();
                            for (int i = 0; i < flag; i++)
                                CacheUtil.getCacheUtilInstance().addObjectToCache(params.get("tid")+"_knowledge_" + i, dataBeans.get(i));
                            callback.onReceiveData(dataBeans);
                        }else
                            callback.onReceiveError(TRANSMISSION_ERROR);
                    } else
                        requestData(params);   //返回结果错误则重新请求
                }

                @Override
                public void onError() {
                    callback.onReceiveError(CONNECTION_ERROR);
                }
            });
        } else {

            SharedPreferences preferences = context.getSharedPreferences("Love Health", Context.MODE_PRIVATE);
            int flag = preferences.getInt("knowledge_list_count", 0);

            if (flag == 0) {
                callback.onReceiveError(CONNECTION_ERROR);   //如果flag为0，说明knowledge模块还未访问过网络
                return;
            }

            List<AbstractDataBean> dataBeans = new ArrayList<>();


            for (int i = 0; i < flag; i++) {
                AbstractDataBean dataBean = CacheUtil.getCacheUtilInstance().getObjectFromMemory(params.get("tid")+"_knowledge_" + i);
                if (dataBean == null) {
                    dataBean = CacheUtil.getCacheUtilInstance().getObjectFromDisk(params.get("tid")+"_knowledge_" + i);
                    if (dataBean != null)
                        dataBeans.add(dataBean);
                } else {
                    dataBeans.add(dataBean);
                }
            }

            //由于缓存空间有限，此时dataBeans的size大小不一定等于flag
            callback.onReceiveData(dataBeans);
        }

    }

    @Override
    protected List<AbstractDataBean> parseResult(String response) {
        List<AbstractDataBean> beans = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(response);
            JSONObject body = object.getJSONObject("showapi_res_body");
            JSONObject pageBean = body.getJSONObject("pagebean");
            JSONArray list = pageBean.getJSONArray("contentlist");

            for (int i = 0; i < list.length(); i++) {
                JSONObject jsonObject = list.getJSONObject(i);
                String id = jsonObject.getString("id");
                String title = jsonObject.getString("title");
                String sTitle = jsonObject.getString("time");
                String img="";
                if (jsonObject.has("img"))
                    img = jsonObject.getString("img");
                beans.add(new KnowledgeListBean(id, title, sTitle, img));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return beans;
    }

    //检查返回的结果是否正确
    private boolean checkResult(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String code = jsonObject.getString("showapi_res_code");
            if (!code.equals("0"))
                return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void stopDataRequest() {
        HttpUtil.getHttpUtil().stopDataRequest();
    }
}
