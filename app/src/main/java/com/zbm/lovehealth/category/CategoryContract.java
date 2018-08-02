package com.zbm.lovehealth.category;

import com.zbm.lovehealth.BasePresenter;
import com.zbm.lovehealth.BaseView;

import java.util.List;

public interface CategoryContract {
    interface View extends BaseView {
        void showCategoryList(List<CategoryListBean.ShowapiResBodyBean.ListBean> beans);

        void showLoadingCategoryError(int errorCode);
    }

    interface Presenter extends BasePresenter {

    }
}
