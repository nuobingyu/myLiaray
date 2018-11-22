package com.nby.popwindow_tuselect;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.os.Environment;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private File tempFile;
    private Uri contentUri ;
    private ImageView imageView;

    private static final int ALBUM_REQUEST_CODE = 1;

    private static final int CAMERA_REQUEST_CODE = 2;

    private static final int CROP_REQUEST_CODE = 3;

    private SelectTuPopWindow selectTuPopWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.iv);
        imageView.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                PopupWindow popupWindow = new PopupWindow();
                popupWindow.setHeight(500);
                popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT );
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.popwindow_select,null );
                popupWindow.setContentView(view);
                selectTuPopWindow = new SelectTuPopWindow(getBaseContext());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    SelectTuPopWindow.SelectTuPopWindowOnClickListener selectTuPopWindowOnClickListener
                            = new SelectTuPopWindow.SelectTuPopWindowOnClickListener( ) {
                        @Override
                        public void handleOnClick(int i) {
                            if(i== 1)
                            {
                                //注意这个错误在调用相机之前要先判断权限是否失效
                                if (Build.VERSION.SDK_INT >= 24)
                                {
                                    int checkCallPhonePermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
                                    if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED)
                                    {
                                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},CAMERA_REQUEST_CODE);
                                    }
                                    else
                                    {
                                        getPicFromCamera();
                                    }
                                }
                                else
                                {
                                    getPicFromCamera();
                                }
                            }
                            else if(i==2)
                            {
                                getPicFromAlbum();
                            }
                        }
                    };
                    selectTuPopWindow.setSelectTuPopWindowOnClickListener(selectTuPopWindowOnClickListener);
                    selectTuPopWindow.showAsDropDown(imageView,0,0, Gravity.CENTER);
                }
            }
        });
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getPicFromCamera();
        }else{
            Toast("需要存储权限");
        }
    }

    public void Toast(String s){
        Toast.makeText(this,s, Toast.LENGTH_SHORT ).show();
    }

    public void getPicFromCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        tempFile = new File(Environment.getExternalStorageDirectory().getPath()
                +"/"+System.currentTimeMillis()+".jpg");
        tempFile.getParentFile().mkdirs();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ){
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT ,contentUri );
        }else{
            intent.putExtra(MediaStore.EXTRA_OUTPUT ,Uri.fromFile(tempFile));
        }
        startActivityForResult(intent ,CROP_REQUEST_CODE);
    }

    private void getPicFromAlbum(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent , ALBUM_REQUEST_CODE);
    }

    /**
     *
     *剪裁图片
     * */
    private void cropPhoto(Uri uri){
        Intent intent = new Intent("com.android.camera.action.CROP");//调用剪裁
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION );
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri ,"image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY",300);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, CROP_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.e("onActivityResult","2");

        switch(requestCode){

            case CAMERA_REQUEST_CODE:
                break;

            case CROP_REQUEST_CODE:
                if(resultCode ==RESULT_OK)
                {
                    if(data !=null)
                    {
                        Bundle bundle = data.getExtras();
                        if(bundle != null)
                        {
                            Bitmap image = bundle.getParcelable("data");
                            imageView.setImageBitmap(image);
                            break;
                        }
                    }
                    Log.e("CROP_REQUEST_CODE","data为空");
                }
                break;

            case ALBUM_REQUEST_CODE:
                if(resultCode == RESULT_OK)
                {
                    Uri uri = data.getData();
                    Log.i("相册","Uri="+uri);
                    cropPhoto(uri);
                }
                break;
        }
    }
}
