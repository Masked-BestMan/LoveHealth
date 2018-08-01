package com.zbm.lovehealth.category;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.zbm.lovehealth.AbstractDataAgent;
import com.zbm.lovehealth.AbstractDataBean;
import com.zbm.lovehealth.IDataRequestFeedback;
import com.zbm.lovehealth.utils.MyUtil;
import com.zbm.lovehealth.R;
import com.zbm.lovehealth.ThemeMessageEvent;
import com.zbm.lovehealth.knowledge.KnowledgeActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment implements CategoryContract.View {

    private boolean isNightMode;
    private Toolbar toolbar;
    private ImageView publicityBoard;
    private RecyclerView categoryList;
    private CategoryContract.Presenter mPresenter;
    private List<CategoryListBean> list;
    private CategoryListAdapter categoryListAdapter;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new CategoryPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);    //必须设置true，Fragment的onCreateOptionsMenu()才会被调用
        toolbar = view.findViewById(R.id.toolbar);
        publicityBoard = view.findViewById(R.id.publicity_board);
        categoryList = view.findViewById(R.id.category_list);
        collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar_layout);
        init();
        return view;
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        categoryDataAgent=new CategoryDataAgent(this);
//        categoryDataAgent.requestData(null);
//
//    }


    private void init() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        isNightMode = getActivity().getSharedPreferences("Love Health", MODE_PRIVATE).getBoolean("in_night_mode", false);
        if (isNightMode) {
            collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.theme_color_deep_night));
            categoryList.setBackgroundColor(getResources().getColor(R.color.theme_color_simple_night));

        } else {
            collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.theme_color_deep_day));
            categoryList.setBackgroundColor(getResources().getColor(R.color.theme_color_simple_day_background));
        }

        publicityBoard.setImageDrawable(getResources().getDrawable(R.drawable.sport));
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.text_alpha_white));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.text_full_white));
        list = new ArrayList<>();
        categoryListAdapter = new CategoryListAdapter(getActivity(), list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (list.size() > 0)
                    return 1;
                else
                    return 2;
            }
        });
        categoryList.setLayoutManager(gridLayoutManager);
        categoryList.setAdapter(categoryListAdapter);
        categoryListAdapter.setOnItemClickListener(new CategoryListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CategoryListBean bean, int position) {
                Intent intent = new Intent(getActivity(), KnowledgeActivity.class);
                intent.putExtra("tid", bean.getId());
                intent.putExtra("name", bean.getName());
                startActivity(intent);
            }
        });
        categoryList.addItemDecoration(new CategoryListItemDecoration(MyUtil.dip2px(getActivity(), 20)));

    }

    //    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (categoryDataAgent!=null)
//            categoryDataAgent.stopDataRequest();
//    }
    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

//    @Override
//    public void onReceiveData(@NonNull List<AbstractDataBean> beans) {
//        list.clear();
//        for (AbstractDataBean categoryListBean : beans) {
//            list.add((CategoryListBean) categoryListBean);
//        }
//        categoryListAdapter.notifyDataSetChanged(true);
//    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();    //Activity的onCreateOptionsMenu()会在之前调用到，导致menu重叠，要先清除
        inflater.inflate(R.menu.category_menu_tool_bar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_day_night_mode:
                if (isNightMode) {
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("Love Health", MODE_PRIVATE).edit();
                    editor.putBoolean("in_night_mode", false);
                    editor.apply();
                    categoryList.setBackgroundColor(getResources().getColor(R.color.theme_color_simple_day_background));
                    collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.theme_color_deep_day));
                    EventBus.getDefault().post(new ThemeMessageEvent(false));
                    isNightMode = false;

                } else {
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("Love Health", MODE_PRIVATE).edit();
                    editor.putBoolean("in_night_mode", true);
                    editor.apply();
                    collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.theme_color_deep_night));
                    categoryList.setBackgroundColor(getResources().getColor(R.color.theme_color_simple_night));
                    EventBus.getDefault().post(new ThemeMessageEvent(true));
                    isNightMode = true;
                }
                getActivity().invalidateOptionsMenu();
                break;
            case R.id.action_refresh:
                //categoryDataAgent.requestData(null);
                mPresenter.subscribe();
        }
        return true;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem dayNightModeItem = menu.findItem(R.id.action_day_night_mode);
        if (isNightMode) {
            dayNightModeItem.setIcon(getResources().getDrawable(R.drawable.day_mode));
            dayNightModeItem.setTitle(getResources().getString(R.string.tool_bar_menu_action_day));
        } else {
            dayNightModeItem.setIcon(getResources().getDrawable(R.drawable.night_mode));
            dayNightModeItem.setTitle(getResources().getString(R.string.tool_bar_menu_action_night));
        }
        super.onPrepareOptionsMenu(menu);
    }

//    @Override
//    public void onReceiveError(int errorCode) {
//        if (errorCode == AbstractDataAgent.CONNECTION_ERROR)
//            Toast.makeText(getActivity(), "请检查网络连通性!", Toast.LENGTH_SHORT).show();
//        else {
//            Toast.makeText(getActivity(), "传输出错，请稍后刷新重试!", Toast.LENGTH_SHORT).show();
//        }
//        categoryListAdapter.notifyDataSetChanged(true);
//    }

    @Override
    public void showCategoryList(List<CategoryListBean> beans) {
        list.clear();
        for (AbstractDataBean categoryListBean : beans) {
            list.add((CategoryListBean) categoryListBean);
        }
        categoryListAdapter.notifyDataSetChanged(true);
    }

    @Override
    public void showLoadingCategoryError(int errorCode) {
        if (errorCode == MyUtil.CONNECTION_ERROR)
            Toast.makeText(getActivity(), "请检查网络连通性!", Toast.LENGTH_SHORT).show();
        else {
            Toast.makeText(getActivity(), "传输出错，请稍后刷新重试!", Toast.LENGTH_SHORT).show();
        }
        categoryListAdapter.notifyDataSetChanged(true);
    }

    @Override
    public void setPresenter(CategoryContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
