package com.zbm.lovehealth.category;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zbm.lovehealth.EmptyViewAdapter;
import com.zbm.lovehealth.R;

import java.util.List;

public class CategoryListAdapter extends EmptyViewAdapter {
    private final static int ITEM_CONTENT=0;

    private Context context;
    private List<CategoryListBean.ShowapiResBodyBean.ListBean> data;
    private OnItemClickListener onItemClickListener;

    CategoryListAdapter(Context context, List<CategoryListBean.ShowapiResBodyBean.ListBean> data) {
        this.data=data;
        this.context = context;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==ITEM_CONTENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.category_list_item, parent, false);
            return new ContentHolder(view);
        }else {
            View view=LayoutInflater.from(context).inflate(R.layout.list_empty_view_show,parent,false);
            return new EmptyHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (data.size()>0) {
            CategoryListBean.ShowapiResBodyBean.ListBean bean=data.get(position);
            ((ContentHolder) holder).imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.temple));
            ((ContentHolder) holder).textView.setText(bean.getName());
            if (onItemClickListener != null)
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(data.get(holder.getLayoutPosition()), holder.getLayoutPosition());
                    }
                });
        }else {
            Log.d("Cate","bind");
            EmptyHolder emptyHolder= (EmptyHolder) holder;
            emptyHolder.emptyTitle.setTextColor(0xFF888888);
            if (isFinishedLoad) {
                emptyHolder.emptyTitle.setText(context.getResources().getString(R.string.list_empty_view_title_no_web));
                emptyHolder.setting.setVisibility(View.VISIBLE);
                emptyHolder.setting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                });
            }else {
                emptyHolder.setting.setVisibility(View.INVISIBLE);
                emptyHolder.emptyTitle.setText(context.getResources().getString(R.string.list_empty_view_title_load));
            }
        }

    }


    @Override
    public int getItemViewType(int position) {
        if (data.size()>0) {
            return ITEM_CONTENT;
        }else
            return  ITEM_NO_DATA;
    }

    @Override
    public int getItemCount() {
        return data.size()>0?data.size():1;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(CategoryListBean.ShowapiResBodyBean.ListBean bean, int position);
    }

    private static class ContentHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;

        ContentHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.category_item_background);
            textView = itemView.findViewById(R.id.category_item_name);
        }
    }

}
