package com.zbm.lovehealth.search;

import com.zbm.lovehealth.AbstractDataBean;

public class SearchItemBean extends AbstractDataBean {
    private String id,keyword,time;

    public SearchItemBean(String id,String keyword,String time){
        this.id=id;
        this.keyword=keyword;
        this.time=time;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getTime() {
        return time;
    }

    @Override
    public int getObjectSize() {
        return 0;
    }
}
