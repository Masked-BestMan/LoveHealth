package com.zbm.lovehealth;

import java.io.Serializable;

public abstract class AbstractDataBean implements Serializable{
    protected String id;

    public AbstractDataBean(String id){
        this.id=id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public abstract int getObjectSize();
}
