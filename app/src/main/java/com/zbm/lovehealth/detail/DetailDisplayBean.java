package com.zbm.lovehealth.detail;

import com.zbm.lovehealth.AbstractDataBean;

public class DetailDisplayBean extends AbstractDataBean {
    private String categoryName,mediaName,time,content,img,keyWords,wapUrl;
    public DetailDisplayBean(String id,String categoryName,String mediaName,String time,String content,String img,String keyWords,String wapUrl) {
        super(id);
        this.categoryName=categoryName;
        this.mediaName=mediaName;
        this.time=time;
        this.content=content;
        this.img=img;
        this.keyWords=keyWords;
        this.wapUrl=wapUrl;
    }

    public String getMediaName() {
        return mediaName;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }

    public String getWapUrl() {
        return wapUrl;
    }

    public void setWapUrl(String wapUrl) {
        this.wapUrl = wapUrl;
    }

    @Override
    public int getObjectSize() {
        String s=getId()+
                getCategoryName()+
                getMediaName()+
                getTime()+
                getContent()+
                getImg()+
                getKeyWords()+
                getWapUrl();
        return s.getBytes().length;
    }
}
