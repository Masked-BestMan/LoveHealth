package com.zbm.lovehealth.knowledge;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zbm.lovehealth.EmptyViewAdapter;
import com.zbm.lovehealth.utils.ImageUtil;
import com.zbm.lovehealth.R;

import java.util.List;

public class KnowledgeListAdapter extends EmptyViewAdapter {
    private final static int ITEM_NO_IMAGE=0;
    private final static int ITEM_WITH_IMAGE=1;
    private OnItemClickListener onItemClickListener;


    private Context context;
    private List<KnowledgeListBean> data;
    public KnowledgeListAdapter(Context context, List<KnowledgeListBean> data){
        this.context=context;
        this.data=data;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==ITEM_WITH_IMAGE){
            View view= LayoutInflater.from(context).inflate(R.layout.knowledge_list_item_with_image,parent,false);
            return new ContentHolder(view);
        }else if (viewType==ITEM_NO_IMAGE){
            View view=LayoutInflater.from(context).inflate(R.layout.knowledge_list_item_no_image,parent,false);
            return new ContentHolder2(view);
        }else{
            View view=LayoutInflater.from(context).inflate(R.layout.list_empty_view_show,parent,false);
            return new EmptyHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (data.size()>0) {
            if (holder instanceof ContentHolder) {
                KnowledgeListBean bean = data.get(position);
                ContentHolder contentHolder = (ContentHolder) holder;
                contentHolder.imageView.setImageDrawable(context.getDrawable(R.mipmap.ic_launcher));
                ImageUtil.getImageByUrl(bean.getImg(), contentHolder.imageView);
                contentHolder.title.setText(bean.getTitle());
                contentHolder.sTitle.setText(bean.getsTitle());
            } else if (holder instanceof ContentHolder2) {
                KnowledgeListBean bean = data.get(position);
                ContentHolder2 contentHolder2 = (ContentHolder2) holder;
                contentHolder2.title.setText(bean.getTitle());
                contentHolder2.sTitle.setText(bean.getsTitle());
            }
            if (onItemClickListener != null)
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(data.get(holder.getLayoutPosition()), holder.getLayoutPosition());
                    }
                });
        }else {
            EmptyHolder emptyHolder= (EmptyHolder) holder;
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
            if (data.get(position).getImg().equals(""))
                return ITEM_NO_IMAGE;
            else
                return ITEM_WITH_IMAGE;
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

    public interface OnItemClickListener{
        void onItemClick(KnowledgeListBean bean,int position);
    }


    private static class ContentHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private TextView title,sTitle;
        ContentHolder(View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.knowledge_item_image);
            title=itemView.findViewById(R.id.knowledge_item_title);
            sTitle=itemView.findViewById(R.id.knowledge_item_s_title);
        }
    }

    private static class ContentHolder2 extends RecyclerView.ViewHolder{
        private TextView title,sTitle;
        ContentHolder2(View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.knowledge_item_title);
            sTitle=itemView.findViewById(R.id.knowledge_item_s_title);
        }
    }

}
