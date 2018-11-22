package com.nby.popwindow_tuselect;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.os.Build;
import android.os.Looper;
import android.os.Handler;
import android.os.Message;

public class SelectTuPopWindow extends PopupWindow {

    private View mMenuView;
    private final static int PICK_PHOTO_CODE = 1;
    private final static int TAKE_PHOTO_CODE = 2;
    private final static int CROP_PIC_CODE = 3;
    private Context mContext;
    private Handler handler;
    private SelectTuPopWindowOnClickListener selectTuPopWindowOnClickListener;

    public SelectTuPopWindow(final Context context) {
        super(context);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        mMenuView = inflater.inflate(R.layout.popwindow_select, null);
        Button btn_take_photo = mMenuView.findViewById(R.id.btn_take_photo);
        Button btn_pick_photo = mMenuView.findViewById(R.id.btn_from_xc);
        Button btn_cancel = mMenuView.findViewById(R.id.btn_cancel);
        init( );
        handler = new Handler(Looper.getMainLooper());
        btn_cancel.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "你点击了cancel按钮", Toast.LENGTH_SHORT).show( );
                dismiss();
            }
        });
        btn_take_photo.setOnClickListener(new View.OnClickListener( ) {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                selectTuPopWindowOnClickListener.handleOnClick(1);
                Toast.makeText(context, "你点击了拍照按钮", Toast.LENGTH_SHORT).show( );
                dismiss();
            }
        });
        btn_pick_photo.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                selectTuPopWindowOnClickListener.handleOnClick(2);
                Toast.makeText(context, "你点击了从相册选择", Toast.LENGTH_SHORT).show( );
                dismiss();
            }
        });

    }

    public Handler getCurrHandler(){
        return handler;
    }


    private void init() {
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(0x50000000));
        setAnimationStyle(R.style.PopupAnimation);
        setInputMethodMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mMenuView.setOnTouchListener(new View.OnTouchListener( ) {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction( ) == MotionEvent.ACTION_UP
                        && event.getY( ) < mMenuView.findViewById(R.id.rl_popwindow_camerapic).getTop( )) {
                    dismiss( );
                }
                return true;
            }
        });
        setContentView(mMenuView);
    }

    public void Toast(String s){
        Toast.makeText(mContext ,s,Toast.LENGTH_SHORT).show();
    }

    public interface SelectTuPopWindowOnClickListener{
        public abstract void handleOnClick(int i);
    }

    public SelectTuPopWindowOnClickListener getSelectTuPopWindowOnClickListener() {
        return selectTuPopWindowOnClickListener;
    }

    public void setSelectTuPopWindowOnClickListener(SelectTuPopWindowOnClickListener selectTuPopWindowOnClickListener) {
        this.selectTuPopWindowOnClickListener = selectTuPopWindowOnClickListener;
    }
}
