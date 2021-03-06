package com.example.park.management;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        final WebView web = (WebView)findViewById(R.id.instaView);
        final String instaURL="https://www.instagram.com/explore/tags/alpa1104/?hl=nl";
        web.loadUrl(instaURL);
        web.getSettings().setJavaScriptEnabled(true);
        web.setWebViewClient(new WebViewClient());
        web.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction()!=KeyEvent.ACTION_DOWN)
                    return true;


                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (web.getUrl().equals(instaURL)){}
                    else if (web.canGoBack()) {
                        web.goBack();
                    }

                    return true;
                }

                return false;
            }
        });
    }
    public void imageClick(View v){
        Intent info = getIntent();
        String userID = info.getStringExtra("userID");
        String userPassword = info.getStringExtra("userPassword");
        String userName = info.getStringExtra("userName");
        String userStudentNumber = info.getStringExtra("userStudentNumber");
        String userImage = info.getStringExtra("userImage");

        Intent intent = new Intent(GalleryActivity.this, MainActivity.class);

        intent.putExtra("userPassword", userPassword);
        intent.putExtra("userName",userName);
        intent.putExtra("userStudentNumber",userStudentNumber);
        intent.putExtra("userID", userID);
        intent.putExtra("userImage", userImage);
        intent.putExtra("Frag","alpa");
        GalleryActivity.this.startActivity(intent);

    }



}

