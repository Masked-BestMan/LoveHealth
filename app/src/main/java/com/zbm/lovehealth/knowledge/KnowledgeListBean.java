package com.zbm.lovehealth.knowledge;

import com.zbm.lovehealth.AbstractDataBean;

public class KnowledgeListBean extends AbstractDataBean {
    private String id,title,sTitle,img;
    public KnowledgeListBean(String id,String title,String sTitle,String img){
        this.id=id;
        this.title=title;
        this.sTitle=sTitle;
        this.img=img;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getsTitle() {
        return sTitle;
    }

    public void setsTitle(String sTitle) {
        this.sTitle = sTitle;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public int getObjectSize() {
        String s = getId() +
                getTitle() +
                getsTitle() +
                getImg();
        return s.getBytes().length;
    }
}
