package com.myeotra.user.ui.activity.privacy_police;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.myeotra.user.BuildConfig;
import com.myeotra.user.R;

public class PrivacyPolicyActivity extends AppCompatActivity {

    WebView webview;

    ImageView menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        webview = findViewById(R.id.webview);
        menu = findViewById(R.id.menu);

        webview.loadUrl(BuildConfig.TERMS_CONDITIONS);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });


        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
