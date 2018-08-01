package com.zbm.lovehealth.search;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zbm.lovehealth.R;
import com.zbm.lovehealth.search.SearchItemBean;

import java.util.List;

public class SearchListAdapter extends RecyclerView.Adapter {

    public final static int TYPE_HEAD=0;
    public final static int TYPE_CONTENT=1;

    private Context context;
    private List<SearchItemBean> data;
    private OnSearchItemClickListener onSearchItemClickListener;

    public SearchListAdapter(Context context,List<SearchItemBean> data){
        this.context=context;
        this.data=data;
    }

    @Override
    public int getItemViewType(int position) {
        if (position==0)
            return TYPE_HEAD;
        else
            return TYPE_CONTENT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==TYPE_HEAD){
            View view=LayoutInflater.from(context).inflate(R.layout.search_list_item_head,parent,false);
            return new HeadHolder(view);
        }else{
            View view=LayoutInflater.from(context).inflate(R.layout.search_list_item_content,parent,false);
            return new ContentHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeadHolder){
            HeadHolder headHolder=(HeadHolder)holder;
            headHolder.clearHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSearchItemClickListener.onCleanHistory();
                }
            });
        }else {
            ContentHolder contentHolder=(ContentHolder)holder;
            contentHolder.itemName.setText(data.get(position-1).getKeyword());
            contentHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSearchItemClickListener.onItemClick(data.get(holder.getLayoutPosition()-1));
                }
            });
        }
    }


    public int getItemWidth(int position){
        return 0;
    }

    @Override
    public int getItemCount() {
        return data.size()+1;
    }

    private static class HeadHolder extends RecyclerView.ViewHolder{
        private Button clearHistory;
        HeadHolder(View itemView) {
            super(itemView);
            clearHistory=itemView.findViewById(R.id.clear_history);
        }
    }

    public static class ContentHolder extends RecyclerView.ViewHolder{
        private TextView itemName;
        ContentHolder(View itemView) {
            super(itemView);
            itemName=itemView.findViewById(R.id.item_name);
        }
    }

    public void setOnSearchItemClickListener(OnSearchItemClickListener onSearchItemClickListener) {
        this.onSearchItemClickListener = onSearchItemClickListener;
    }

    public interface OnSearchItemClickListener{
        void onItemClick(SearchItemBean bean);
        void onCleanHistory();
    }
}
