package com.zbm.lovehealth.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;


import com.zbm.lovehealth.search.SearchItemBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class DBHelper extends SQLiteOpenHelper {
    private static DBHelper helper = null;
    /*
    queryTYPE为搜索类型：url网址；word内容
     */
    public static final String SEARCH_TABLE_NAME = "SearchHistoryTB";

    private static final String CREATE_SEARCH_HISTORY_TABLE = "create table " + SEARCH_TABLE_NAME + "(" +
            "id integer primary key autoincrement," +
            "keyword text unique," +
            "s_time date)";


    private OnSearchHistoryTableListener hl;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    hl.onExecuteResult((List<SearchItemBean>) msg.obj);
                    break;
            }
        }
    };

    public void removeMessage() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler=null;
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SEARCH_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public static DBHelper getDBHelper(Context context) {
        if (helper == null)
            helper = new DBHelper(context.getApplicationContext(), "LoveHealthDatabase", null, 1);
        return helper;
    }


    public void querySearchHistoryTable(final String sql, OnSearchHistoryTableListener hl) {
        this.hl = hl;
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<SearchItemBean> searchResult = new ArrayList<>();
                SQLiteDatabase db = helper.getWritableDatabase();
                Cursor mCursor = db.rawQuery(sql, null);
                while (mCursor.moveToNext()) {
                    int id = mCursor.getInt(mCursor.getColumnIndex("id"));
                    String name = mCursor.getString(mCursor.getColumnIndex("keyword"));
                    String time = mCursor.getString(mCursor.getColumnIndex("s_time"));
                    searchResult.add(new SearchItemBean(id+"" , name, time));
                }
                mCursor.close();
                mHandler.sendMessage(mHandler.obtainMessage(1, searchResult));
            }
        }).start();
    }

    public void updateSearchHistoryTable(final String keyword) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String searchTime = format.format(new Date(System.currentTimeMillis()));
                SQLiteDatabase db=helper.getWritableDatabase();
                String sql = "replace into " + SEARCH_TABLE_NAME + "(keyword,s_time) values('" + keyword + "','" + searchTime + "')";
                try {
                    db.execSQL(sql);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

    public void deleteTableItem(final String table, final String where) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = helper.getWritableDatabase();
                if (where != null)
                    db.execSQL("delete from " + table + " " + where);
                else
                    db.execSQL("delete from " + table);
            }
        }).start();
    }

    public interface OnSearchHistoryTableListener {
        void onExecuteResult(List<SearchItemBean> mHistoryData);
    }

}
