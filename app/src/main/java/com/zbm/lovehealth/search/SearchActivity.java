package com.zbm.lovehealth.search;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.zbm.lovehealth.R;
import com.zbm.lovehealth.recognize.RecognizeActivity;
import com.zbm.lovehealth.utils.DBHelper;
import com.zbm.lovehealth.utils.MyUtil;

import java.util.ArrayList;
import java.util.List;


public class SearchActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editText;
    private TextView searchButton;
    private ImageView voiceRecognition;
    private ImageView clearButton;
    private SearchListView listView;
    private SearchListAdapter adapter;
    private List<SearchItemBean> data;

    private InputMethodManager mInputMethodManager;

    private static final int REQUEST_RECOGNIZE = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(null);
        //根据用户偏好设置夜间模式
        SharedPreferences preferences = getSharedPreferences("Love Health", MODE_PRIVATE);
        boolean isNight = preferences.getBoolean("in_night_mode", false);
        if (isNight)
            setTheme(R.style.AppTheme_night);
        setContentView(R.layout.activity_search);

        editText=findViewById(R.id.edit_text);
        searchButton=findViewById(R.id.search_button);
        voiceRecognition=findViewById(R.id.voice_recognition);
        clearButton=findViewById(R.id.clear_text);
        listView=findViewById(R.id.search_history_list);

        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        data = new ArrayList<>();


        init();
    }

    private void init() {
        adapter = new SearchListAdapter(this, data);
        adapter.setOnSearchItemClickListener(new SearchListAdapter.OnSearchItemClickListener() {
            @Override
            public void onItemClick(SearchItemBean bean) {
                editText.setText(bean.getKeyword());
            }

            @Override
            public void onCleanHistory() {
                if (!data.isEmpty())
                    showNormalDialog();

            }
        });
        FlowLayoutManager flowLayoutManager=new FlowLayoutManager();
        listView.setLayoutManager(flowLayoutManager);
        listView.addItemDecoration(new SearchListItemDecoration(MyUtil.dip2px(this,10)));
        listView.setAdapter(adapter);
        voiceRecognition.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(editable)) {
                    searchButton.setText(R.string.search_ensure);
                    voiceRecognition.setVisibility(View.INVISIBLE);
                    clearButton.setVisibility(View.VISIBLE);
                } else {
                   searchButton.setText(R.string.search_cancel);
                    voiceRecognition.setVisibility(View.VISIBLE);
                    clearButton.setVisibility(View.INVISIBLE);
                }
            }
        });
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER) {
                    executeSearch(searchButton);
                }
                return false;
            }
        });


        listView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE && mInputMethodManager.isActive())
                    mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                return false;
            }
        });
        DBHelper.getDBHelper(this).querySearchHistoryTable("select * from " + DBHelper.SEARCH_TABLE_NAME, new DBHelper.OnSearchHistoryTableListener() {
            @Override
            public void onExecuteResult(List<SearchItemBean> mHistoryData) {
                data.addAll(mHistoryData);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View view) {
        int object = view.getId();
        switch (object) {
            case R.id.voice_recognition:
                startActivityForResult(new Intent(this, RecognizeActivity.class), REQUEST_RECOGNIZE);
                break;
            case R.id.search_button:
                executeSearch(view);
                break;
            case R.id.clear_text:
                editText.setText("");
                break;
        }
    }

    private void executeSearch(View view) {
        if (mInputMethodManager.isActive())
            mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        if (((TextView) view).getText().equals("搜索")) {
            DBHelper.getDBHelper(this).updateSearchHistoryTable(String.valueOf(editText.getText()));
            Intent intent = new Intent();
            intent.putExtra("keyword", String.valueOf(editText.getText()));
            setResult(RESULT_OK, intent);
        }else
            setResult(RESULT_CANCELED);

        finish();
    }

    private void showNormalDialog() {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(this);
        normalDialog.setIcon(android.R.drawable.ic_menu_info_details)
                .setTitle("删除提示")
                .setMessage("确认清空输入记录？")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DBHelper.getDBHelper(SearchActivity.this).deleteTableItem(DBHelper.SEARCH_TABLE_NAME,null);
                                data.clear();
                                adapter.notifyDataSetChanged();
                            }
                        })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RECOGNIZE && resultCode == Activity.RESULT_OK) {
            String word = data.getStringExtra("result");
            editText.setText(word);
        }
    }
}
