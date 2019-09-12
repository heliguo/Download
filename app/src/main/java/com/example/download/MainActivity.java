package com.example.download;


import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ketr.bsdiff.DiffUtils;

public class MainActivity extends AppCompatActivity {

    private Button mButton;
    private Button patchBtn;
    private Button diffBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = findViewById(R.id.check_updatge);
        patchBtn = findViewById(R.id.patch);
        diffBtn = findViewById(R.id.diff);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //按需更改url
                String url = "http://192.168.22.68:8080/app/app-debug_new1.apk";
//                String url = "https://192.168.22.68:8443/app/app-debug_new1.apk";
                String path = "" + Environment.getExternalStorageDirectory();
                UpdateUtil util = new UpdateUtil(MainActivity.this);
                util.requestVersionCode(2, path, "app-debug_new1.apk", url);
            }
        });
        patchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPatch();
            }
        });
        diffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDiff();
            }
        });

    }

    //合并成功
    public void onPatch() {

        String oldfile = Environment.getExternalStorageDirectory() + "/app-debug_old1.apk";
        String newfile = Environment.getExternalStorageDirectory() + "/app-debug_new1.apk";
        String patchfile = Environment.getExternalStorageDirectory() + "/app-debug_patch1.patch";
        DiffUtils diffUtils = new DiffUtils();
        int restlt = diffUtils.mergeDiffApk(oldfile, newfile, patchfile);
        if (restlt == 0) {
            Log.d("123456", "合并成功");
            Toast.makeText(this, "合并成功", Toast.LENGTH_LONG).show();
        } else {
            Log.d("123456", "合并失败");
            Toast.makeText(this, "合并失败", Toast.LENGTH_LONG).show();
        }
    }

    //生成差量包
    public void onDiff() {

        String oldfile = Environment.getExternalStorageDirectory() + "/app-debug_old1.apk";
        String newfile = Environment.getExternalStorageDirectory() + "/app-debug_new1.apk";
        String patchfile = Environment.getExternalStorageDirectory() + "/app-debug_patch1.patch";
        DiffUtils diffUtils = new DiffUtils();
        int result = diffUtils.generateDiffApk(oldfile, newfile, patchfile);
        if (result == 0) {
            Log.d("123456", "拆分成功");
            Toast.makeText(this, "拆分成功", Toast.LENGTH_LONG).show();
        } else {
            Log.d("123456", "拆分失败");
            Toast.makeText(this, "拆分失败", Toast.LENGTH_LONG).show();
        }
    }
}
