package com.zbm.lovehealth.category;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class CategoryListItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public CategoryListItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        //不是第一个的格子都设一个左边和底部的间距
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;
        outRect.top=space;
    }
}
