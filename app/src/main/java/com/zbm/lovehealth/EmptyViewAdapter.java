package com.zbm.lovehealth;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class EmptyViewAdapter extends RecyclerView.Adapter {
    protected final static int ITEM_NO_DATA=-1;
    /**
     * 标识适配器绑定的数据是否加载完毕(即决定空视图显示内容)
     */
    protected boolean isFinishedLoad=false;
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public final void notifyDataSetChanged(boolean isFinishedLoad){
        this.isFinishedLoad=isFinishedLoad;
        notifyDataSetChanged();
    }
    public static class EmptyHolder extends RecyclerView.ViewHolder{
        public TextView emptyTitle;
        public Button setting;
        public EmptyHolder(View itemView) {
            super(itemView);
            emptyTitle = itemView.findViewById(R.id.empty_title);
            setting=itemView.findViewById(R.id.network_setting);
        }
    }
}
