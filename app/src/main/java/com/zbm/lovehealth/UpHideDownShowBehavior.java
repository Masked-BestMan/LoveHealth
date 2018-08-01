package com.zbm.lovehealth;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

public class UpHideDownShowBehavior extends FloatingActionButton.Behavior {
    public UpHideDownShowBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // 返回true时表示传递滑动参数，同时执行后面的滑动监听，返回false的话后面的onNestedScroll等方法就不会调用了
    // super是直接返回的false
    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        // 判断如果是垂直滚动则返回true
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        // 可以直接删掉super 进去之后发现super里面啥也没做
        //super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        //api 25以上CoordinatorLayout中onNestedScroll()方法多了if (view.getVisibility() == GONE)判断，
        // 所以用hide()方法或setVisibility(GONE)会导致show()方法或setVisibility(VISIBLE);
        if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
            child.setVisibility(View.INVISIBLE);
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            child.setVisibility(View.VISIBLE);
        }
    }
}
