package com.zbm.lovehealth;

import android.support.annotation.NonNull;

import java.util.List;

public interface IDataRequestFeedback {
    void onReceiveData(@NonNull List<AbstractDataBean> beans);  //保证该参数不为空
    void onReceiveError(int errorCode);  //获取不到数据或数据有错时会回调该方法
}
