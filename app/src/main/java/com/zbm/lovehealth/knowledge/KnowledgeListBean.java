package com.zbm.lovehealth.knowledge;

import com.zbm.lovehealth.AbstractDataBean;

public class KnowledgeListBean extends AbstractDataBean {
    private String title,sTitle,img;
    public KnowledgeListBean(String id,String title,String sTitle,String img){
        super(id);
        this.title=title;
        this.sTitle=sTitle;
        this.img=img;
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
