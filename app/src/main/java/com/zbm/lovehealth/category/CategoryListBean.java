package com.zbm.lovehealth.category;

import com.zbm.lovehealth.AbstractDataBean;

public class CategoryListBean extends AbstractDataBean {
    private int image;
    private String name;

    public CategoryListBean(String id,String name,int image){
        super(id);
        this.name=name;
        this.image=image;
    }


    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getObjectSize() {
        String s = getId() +
                getImage() +
                getName();
        return s.getBytes().length;
    }
}
