package com.zbm.lovehealth.category;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;

import com.zbm.lovehealth.AbstractDataBean;
import com.zbm.lovehealth.R;
import com.zbm.lovehealth.utils.CacheUtil;
import com.zbm.lovehealth.utils.HttpUtil;
import com.zbm.lovehealth.utils.MyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategoryPresenter implements CategoryContract.Presenter {
    private CategoryContract.View categoryView;
    private Context context;
    public CategoryPresenter(CategoryContract.View categoryView){
        this.categoryView=categoryView;
        context=((Fragment)categoryView).getContext();
        categoryView.setPresenter(this);
    }
    @Override
    public void subscribe() {
        requestData(null);
    }

    @Override
    public void unsubscribe() {

    }

   private void requestData(final Map<String,String> params) {
        if (MyUtil.isNetworkAvailable(context)) {
            HttpUtil.getHttpUtil().retrieveDataFromServer("http://route.showapi.com/96-108", params, new HttpUtil.OnReceiveDataListener() {
                @Override
                public void onReceive(String response) {
                    if (checkResult(response)) {
                        List<CategoryListBean> dataBeans = parseResult(response);
                        if (dataBeans!=null) {
                            int flag = dataBeans.size();
                            SharedPreferences.Editor editor = context.getSharedPreferences("Love Health", Context.MODE_PRIVATE).edit();
                            editor.putInt("category_list_count", flag);
                            editor.apply();
                            for (int i = 0; i < flag; i++)
                                CacheUtil.getCacheUtilInstance().addObjectToCache("category_" + i, dataBeans.get(i));
                            //callback.onReceiveData(dataBeans);
                            categoryView.showCategoryList(dataBeans);
                        }else{
                            //callback.onReceiveError(MyUtil.TRANSMISSION_ERROR);
                            categoryView.showLoadingCategoryError(MyUtil.TRANSMISSION_ERROR);
                        }
                    }else
                        requestData(params);    //返回结果错误则重新请求
                }

                @Override
                public void onError() {
                    //callback.onReceiveError(MyUtil.CONNECTION_ERROR);
                    categoryView.showLoadingCategoryError(MyUtil.CONNECTION_ERROR);
                }
            });
        }else{

            SharedPreferences preferences=context.getSharedPreferences("Love Health", Context.MODE_PRIVATE);
            int flag=preferences.getInt("category_list_count",0);

            if (flag==0){
                //callback.onReceiveError(CONNECTION_ERROR);   //如果flag为0，说明category模块还未访问过网络
                categoryView.showLoadingCategoryError(MyUtil.CONNECTION_ERROR);
                return;
            }

            List<CategoryListBean> dataBeans = new ArrayList<>();


            for (int i=0;i<flag;i++){
                AbstractDataBean dataBean = CacheUtil.getCacheUtilInstance().getObjectFromMemory("category_"+i);
                if (dataBean == null){
                    dataBean = CacheUtil.getCacheUtilInstance().getObjectFromDisk("category_"+i);
                    if (dataBean!=null)
                        dataBeans.add((CategoryListBean) dataBean);
                }else {
                    dataBeans.add((CategoryListBean) dataBean);
                }
            }

            //由于缓存空间有限，此时dataBeans的size大小不一定等于flag
            //callback.onReceiveData(dataBeans);
            categoryView.showCategoryList(dataBeans);
        }
    }

    private List<CategoryListBean> parseResult(String response) {
        List<CategoryListBean> beans=new ArrayList<>();
        try {
            JSONObject object=new JSONObject(response);
            JSONObject body=object.getJSONObject("showapi_res_body");
            JSONArray list=body.getJSONArray("list");

            for (int i=0;i<list.length();i++){
                JSONObject jsonObject=list.getJSONObject(i);
                String id= jsonObject.getString("id");
                String name=jsonObject.getString("name");
                beans.add(new CategoryListBean(id,name, R.drawable.temple));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return beans;
    }

    //检查返回的结果是否正确
    private boolean checkResult(String response){
        try {
            JSONObject jsonObject=new JSONObject(response);
            String code=jsonObject.getString("showapi_res_code");
            if (!code.equals("0"))
                return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
