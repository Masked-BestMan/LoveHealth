package com.zbm.lovehealth.retrofit;

import com.zbm.lovehealth.category.CategoryListBean;
import com.zbm.lovehealth.detail.DetailDisplayBean;
import com.zbm.lovehealth.knowledge.KnowledgeListBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface ApiService {
    @GET("96-108")
    Flowable<List<CategoryListBean>>  getCategories(@QueryMap Map<String,String> params);

    @GET("96-109")
    Flowable<List<KnowledgeListBean>> getKnowledge(@QueryMap Map<String,String> params);

    @GET("96-36")
    Flowable<DetailDisplayBean> getDetails(@QueryMap Map<String,String> params);
}
