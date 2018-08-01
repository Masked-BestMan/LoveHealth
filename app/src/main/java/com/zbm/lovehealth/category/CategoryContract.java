package com.zbm.lovehealth.category;

import com.zbm.lovehealth.BasePresenter;
import com.zbm.lovehealth.BaseView;

import java.util.List;

public interface CategoryContract {
    interface View extends BaseView<Presenter> {
        void showCategoryList(List<CategoryListBean> beans);

        void showLoadingCategoryError(int errorCode);
    }

    interface Presenter extends BasePresenter {

    }
}
