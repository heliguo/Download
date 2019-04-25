package com.example.download;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = findViewById(R.id.check_updatge);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //按需更改url
                String url = "http://172.24.252.1:8080/app/qq.apk";
                String path = Environment.getExternalStorageDirectory()+"";
                UpdateUtil util = new UpdateUtil(MainActivity.this);
                util.requestVersionCode(2,path,"qq.apk",url);

            }
        });

    }
}
