package com.zbm.lovehealth.knowledge;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.zbm.lovehealth.AbstractDataAgent;
import com.zbm.lovehealth.AbstractDataBean;
import com.zbm.lovehealth.detail.DetailActivity;
import com.zbm.lovehealth.IDataRequestFeedback;
import com.zbm.lovehealth.utils.ImageUtil;
import com.zbm.lovehealth.utils.MyUtil;
import com.zbm.lovehealth.R;
import com.zbm.lovehealth.search.SearchActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

public class KnowledgeActivity extends AppCompatActivity implements BGARefreshLayout.BGARefreshLayoutDelegate, IDataRequestFeedback {
    private FloatingActionButton searchKnowledgeButton;
    private KnowledgeDataAgent knowledgeDataAgent;
    private List<KnowledgeListBean> list;
    private KnowledgeListAdapter knowledgeListAdapter;
    private BGARefreshLayout mRefreshLayout;
    private RecyclerView knowledgeList;
    private String knowledge_name;
    private Map<String, String> params;    //请求的页号最大为5
    private boolean isRefreshing;   //该标志决定了list是清空还是继续添加,下拉刷新时为true，下拉加载更多为false
    private int currentPage = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isNightMode = getSharedPreferences("Love Health", MODE_PRIVATE).getBoolean("in_night_mode", false);
        if (isNightMode)
            setTheme(R.style.AppTheme_night);
        setContentView(R.layout.activity_knowledge);
        Intent intent = getIntent();
        String tid = intent.getStringExtra("tid");
        knowledge_name = intent.getStringExtra("name");
        searchKnowledgeButton = findViewById(R.id.search_knowledge_button);
        mRefreshLayout = findViewById(R.id.knowledge_refresh_layout);
        knowledgeList = findViewById(R.id.knowledge_list);
        Toolbar toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        init();

        knowledgeDataAgent = new KnowledgeDataAgent(this);
        params = new HashMap<>();
        params.put("tid", tid);
        params.put("page", currentPage + "");
        knowledgeDataAgent.requestData(params);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(knowledge_name);
        }


        mRefreshLayout.setDelegate(this);
        mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(this, true));

        list = new ArrayList<>();
        knowledgeListAdapter = new KnowledgeListAdapter(this, list);
        knowledgeList.setLayoutManager(new LinearLayoutManager(this));
        knowledgeList.setAdapter(knowledgeListAdapter);
        knowledgeList.addItemDecoration(new KnowledgeListItemDecoration(MyUtil.dip2px(this, 10)));
        knowledgeListAdapter.setOnItemClickListener(new KnowledgeListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(KnowledgeListBean bean, int position) {
                Intent intent = new Intent(KnowledgeActivity.this, DetailActivity.class);
                intent.putExtra("id", bean.getId());
                intent.putExtra("title", bean.getTitle());
                startActivity(intent);
            }
        });

        searchKnowledgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KnowledgeActivity.this.startActivityForResult(new Intent(KnowledgeActivity.this, SearchActivity.class), 0);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (knowledgeDataAgent != null)
            knowledgeDataAgent.stopDataRequest();
        ImageUtil.emptyImageTask();
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        isRefreshing = true;
        currentPage = (currentPage + 1) % 5 + 1;
        params.put("page", currentPage + "");
        knowledgeDataAgent.requestData(params);
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        isRefreshing = false;

        if (knowledgeDataAgent.isNetworkAvailable(this)) {
            currentPage = (currentPage + 1) % 5;
            params.put("page", currentPage + "");
            knowledgeDataAgent.requestData(params);
            return true;
        } else
            return false;
    }

    @Override
    public void onReceiveData(@NonNull List<AbstractDataBean> beans) {
        if (isRefreshing) {
            list.clear();
            isRefreshing = false;
        }
        for (AbstractDataBean knowledgeListBean : beans) {
            list.add((KnowledgeListBean) knowledgeListBean);
        }
        knowledgeListAdapter.notifyDataSetChanged(true);
        mRefreshLayout.endLoadingMore();
        mRefreshLayout.endRefreshing();
    }

    @Override
    public void onReceiveError(int errorCode) {
        if (errorCode == AbstractDataAgent.CONNECTION_ERROR)
            Toast.makeText(this, "请检查网络连通性!", Toast.LENGTH_SHORT).show();
        else {
            Toast.makeText(this, "传输出错，请稍后刷新重试!", Toast.LENGTH_SHORT).show();
        }
        knowledgeListAdapter.notifyDataSetChanged(true);
        mRefreshLayout.endLoadingMore();
        mRefreshLayout.endRefreshing();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            params.put("key", data.getStringExtra("keyword"));
            Log.d("Know", "" + params);
            knowledgeDataAgent.requestData(params);
            isRefreshing = true;
        }
    }
}
