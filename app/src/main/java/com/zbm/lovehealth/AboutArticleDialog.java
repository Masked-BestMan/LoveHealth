package com.zbm.lovehealth;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AboutArticleDialog extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(null);
        setContentView(R.layout.dialog_about_article);
        setFinishOnTouchOutside(false);

        Intent intent =getIntent();
        final String keywords=intent.getStringExtra("keywords");
        final String categoryName=intent.getStringExtra("categoryName");
        final String mediaName=intent.getStringExtra("mediaName");
        final String time=intent.getStringExtra("time");

        TextView contentView=findViewById(R.id.content_view);
        contentView.setText(transferString(keywords,categoryName,mediaName,time));
        TextView titleView=findViewById(R.id.title_view);
        View dialogBackground=findViewById(R.id.main_background);
        Button ensure=findViewById(R.id.ensure);
        ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button copy=findViewById(R.id.copy);
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取剪贴板管理器：
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText("Label", "关键词："+keywords+",所属分类："+categoryName+",发布媒体："+mediaName+",发布时间："+time);
                // 将ClipData内容放到系统剪贴板里。
                if (cm != null) {
                    cm.setPrimaryClip(mClipData);
                    Toast.makeText(AboutArticleDialog.this,"已复制到剪贴板!",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(AboutArticleDialog.this,"复制失败!",Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
        boolean isNightMode=getSharedPreferences("Love Health",MODE_PRIVATE).getBoolean("in_night_mode",false);
        if (isNightMode){
            titleView.setTextColor(getResources().getColor(R.color.theme_color_simple_day));
            contentView.setTextColor(getResources().getColor(R.color.theme_color_simple_day));
            dialogBackground.setBackgroundColor(getResources().getColor(R.color.theme_color_deep_night));
        }
    }

    public CharSequence transferString(String...strings){
        String html="<html><head><head/><body>关键词："+
                strings[0]+"<br>所属分类："+strings[1]+"<br>发布媒体："+strings[2]+"<br>发布时间："+
                strings[3]+"<body/><html/>";
        return Html.fromHtml(html);
    }
}
