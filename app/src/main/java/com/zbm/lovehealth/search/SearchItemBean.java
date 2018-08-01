package com.zbm.lovehealth.search;

import com.zbm.lovehealth.AbstractDataBean;

public class SearchItemBean extends AbstractDataBean {
    private String keyword,time;

    public SearchItemBean(String id,String keyword,String time){
        super(id);
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
