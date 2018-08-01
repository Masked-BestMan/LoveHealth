package com.zbm.lovehealth.detail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zbm.lovehealth.AboutArticleDialog;
import com.zbm.lovehealth.AbstractDataAgent;
import com.zbm.lovehealth.AbstractDataBean;
import com.zbm.lovehealth.IDataRequestFeedback;
import com.zbm.lovehealth.utils.MyUtil;
import com.zbm.lovehealth.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DetailActivity extends AppCompatActivity implements IDataRequestFeedback {
    private DetailDataAgent detailDataAgent;
    private WebView contentView;
    private NestedScrollView nestedScrollView;
    private String keywords,categoryName,mediaName,time,wapUrl;
    private Map<String, String> params;
    private boolean isNight;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(null);
        //根据用户偏好设置夜间模式
        SharedPreferences preferences = getSharedPreferences("Love Health", MODE_PRIVATE);
        isNight = preferences.getBoolean("in_night_mode",false);
        if (isNight)
            setTheme(R.style.AppTheme_night);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String title = intent.getStringExtra("title");

        nestedScrollView = findViewById(R.id.nested_scroll_view);
        contentView = new WebView(getApplicationContext());
        contentView.setBackground(null);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (isNight)
            toolbar.setPopupTheme(R.style.OverflowButtonStyle_Night);
        TextView titleView = toolbar.findViewById(R.id.title_view);
        titleView.setText(title);
        titleView.setSelected(true);
        setSupportActionBar(toolbar);

        init();

        detailDataAgent = new DetailDataAgent(this);
        params = new HashMap<>();
        params.put("id", id);
        detailDataAgent.requestData(params);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        nestedScrollView.addView(contentView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        contentView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        contentView.getSettings().setJavaScriptEnabled(true);
        contentView.addJavascriptInterface(new NetworkSettingJS(this), "network");
        //contentView.getSettings().setTextZoom(200);

        contentView.setWebViewClient(new WebViewClient() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                if (isNight) {
                    InputStream mIs;
                    String wholeJS = null;
                    try {
                        mIs = getResources().getAssets().open("night.js");
                        if (mIs != null) {
                            byte buff[] = new byte[1024];
                            ByteArrayOutputStream fromFile = new ByteArrayOutputStream();
                            do {
                                int numRead;
                                numRead = mIs.read(buff);
                                if (numRead <= 0) {
                                    break;
                                }
                                fromFile.write(buff, 0, numRead);
                            } while (true);
                            wholeJS = fromFile.toString();
                        } else {
                            Toast.makeText(DetailActivity.this, "js加载失败", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // 加载JS代码
                    // 格式规定为:file:///android_asset/文件名.html
                    //contentView.loadUrl("file:///android_asset/javascript.html");
                    //WebView添加读取的js
                    contentView.loadUrl(wholeJS);
                }
                super.onPageStarted(view, url, favicon);

            }
        });
        if (getSupportActionBar() != null)
            getSupportActionBar().setHomeButtonEnabled(true);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        detailDataAgent.stopDataRequest();
        contentView.removeAllViews();
        contentView.destroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_tool_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_share:
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
                intent.putExtra(Intent.EXTRA_TEXT, "I have successfully share my message through my app");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, "分享到"));
                break;
            case R.id.action_refresh:
                detailDataAgent.requestData(params);
                break;
            case R.id.action_link:
                Intent intent1 = new Intent();
                intent1.setAction("android.intent.action.VIEW");
                intent1.setData(Uri.parse(wapUrl));
                startActivity(intent1);
                break;
            case R.id.action_details:
                Intent intent2=new Intent(this,AboutArticleDialog.class);
                intent2.putExtra("keywords",keywords);
                intent2.putExtra("categoryName",categoryName);
                intent2.putExtra("mediaName",mediaName);
                intent2.putExtra("time",time);
                startActivity(intent2);
                break;
        }
        return true;
    }


    @Override
    public void onReceiveData(@NonNull List<AbstractDataBean> beans) {
        for (AbstractDataBean abstractDataBean : beans) {
            DetailDisplayBean detailDisplayBean = (DetailDisplayBean) abstractDataBean;
            contentView.loadDataWithBaseURL(null, fixHtmlData(detailDisplayBean.getContent()), "text/html", "UTF-8", null);
            keywords=detailDisplayBean.getKeyWords();
            time=detailDisplayBean.getTime();
            mediaName=detailDisplayBean.getMediaName();
            categoryName=detailDisplayBean.getCategoryName();
            wapUrl=detailDisplayBean.getWapUrl();
        }

    }

    @Override
    public void onReceiveError(int errorCode) {
        if (errorCode== AbstractDataAgent.CONNECTION_ERROR) {
            contentView.loadUrl("file:///android_asset/network_setting_js.html");
        } else{
            Toast.makeText(this,"传输出错，请稍后刷新重试!",Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 该方法用于修正html文本
     */
    private String fixHtmlData(String bodyHTML) {
        return "<html>" +
                "<head><style>.div-css5-a{ line-height:"+ MyUtil.sp2px(this,11)+"px}img{max-width: 100%; width:auto; height: auto;}</style></head>" +
                "<body><div class=\"div-css5-a\">" + bodyHTML.replaceAll("\\n", "<br>") +
                "</div></body></html>";
    }

    private static class NetworkSettingJS{
        private Context context;

        NetworkSettingJS(Context context){
            this.context=context;
        }

        @JavascriptInterface
        public void showSettingFromJS(){
            context.startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }
}
