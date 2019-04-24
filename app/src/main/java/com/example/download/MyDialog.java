package com.example.download;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * @创建者 李国赫
 * @创建时间 2019/4/24 10:23
 * @描述
 * 自定义dialog
 */
public class MyDialog extends Dialog {

    private Button   confirmBtn;
    private Button   cancelBtn;
    private TextView titleTv;
    private TextView msgTv;
    //自定义提示文本
    private String   titleStr;
    private String   msgStr;
    //自定义按钮文本设置
    private String   confirmStr;
    private String   cancelStr;

    private ConfirmOnClickListener confirmOnClickListener;
    private CancelOnClickListener  cancelOnClickListener;

    public MyDialog(Context context) {
        super(context);
    }

    /**
     * 设置确定按钮文本和监听事件
     */
    public void setConfirmOnClickListener(String confirmStr, ConfirmOnClickListener confirmOnClickListener) {
        if (confirmStr != null) {
            this.confirmStr = confirmStr;
        }
        this.confirmOnClickListener = confirmOnClickListener;
    }

    /**
     * 设置确定按钮文本和监听事件
     */
    public void setCancelOnClickListener(String cancelStr, CancelOnClickListener cancelOnClickListener) {
        if (cancelStr != null) {
            this.cancelStr = cancelStr;
        }
        this.cancelOnClickListener = cancelOnClickListener;
    }

    /**
     * 初始化操作
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_dialog);
        //禁止点击空白取消
        setCanceledOnTouchOutside(false);
        //初始化界面
        initView();
        //初始化文本信息
        initData();
        //初始化点击事件
        initEvent();

    }

    /**
     * 确定取消按钮点击事件
     */
    private void initEvent() {
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmOnClickListener != null) {
                    confirmOnClickListener.onConfirmClick();
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelOnClickListener != null) {
                    cancelOnClickListener.onCancelClick();
                }
            }
        });
    }

    /**
     * 初始化显示文本
     */
    private void initData() {
        //提示信息
        if (titleStr != null) {
            titleTv.setText(titleStr);
        }
        if (msgStr != null) {
            msgTv.setText(msgStr);
        }
        //按钮文本
        if (confirmStr != null) {
            confirmBtn.setText(confirmStr);
        }
        if (cancelStr != null) {
            cancelBtn.setText(cancelStr);
        }
    }

    /**
     * 初始化界面
     */
    private void initView() {
        confirmBtn = findViewById(R.id.confirm_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
        titleTv = findViewById(R.id.title_tv);
        msgTv = findViewById(R.id.message_tv);
    }

    /**
     * 用户提示消息提醒
     */
    public void setTitle(String title) {
        titleStr = title;
    }

    /**
     * 外界内容设置
     */
    public void setMessage(String message) {
        msgStr = message;
    }

    /**
     * 确认监听
     */
    public interface ConfirmOnClickListener {
        void onConfirmClick();
    }

    /**
     * 取消监听
     */
    public interface CancelOnClickListener {
        void onCancelClick();
    }


}
