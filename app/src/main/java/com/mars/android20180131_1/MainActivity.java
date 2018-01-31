package com.mars.android20180131_1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    ImageView img;
    private static final String CAMERA_DIR = "/dcim/";
    private static final String albumName ="CameraSample";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = findViewById(R.id.imageView);
    }

    public void click1(View v)
    {
        //启动相机的Intent构造 MediaSotre.ACTION_IMAGE_CAPTURE
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(it, 123);
    }

    //獲得完整尺寸图片,存到手機內部資料夾
    public void click2(View v)
    {
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(getExternalFilesDir("PHOTO"), "myphoto.jpg");
        it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        startActivityForResult(it, 456);
    }

   //載入存在手機內的圖片
    public void click3(View v)
    {

    }

    @Override
    //需要获得相机拍摄的照片，所以startActivity(intent,requestCode);之后在onActivityResult()中接收返回的数据
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //方法的返回的intent的extras中存储在对应data下，一张缩略图
        if (requestCode == 123)
        {
            if (resultCode == RESULT_OK)
            {
                Bundle pBundle = data.getExtras();
                Bitmap bmp = (Bitmap) pBundle.get("data");
                img.setImageBitmap(bmp);
            }
        }
        /*
        if (requestCode == 456)
        {

            if (resultCode == RESULT_OK)
            {
                //起file物件
                File f = new File(getExternalFilesDir("PHOTO"), "myphoto.jpg");
                //Bitmap會到這個路徑裡面解出圖片,但是很吃手機的記憶體,相片解析度太高可以能當機
                Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
                img.setImageBitmap(bmp);
            }
        }
        */

        if (requestCode == 456)
        {
            if (resultCode == RESULT_OK)
            {
                File f = new File(getExternalFilesDir("PHOTO"), "myphoto.jpg");
                try {
                    InputStream is = new FileInputStream(f);
                    Log.d("BMP", "Can READ:" + is.available());
                    Bitmap bmp = getFitImage(is);
                    img.setImageBitmap(bmp);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }




    }
    public static Bitmap getFitImage(InputStream is)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        byte[] bytes = new byte[0];
        try {
            bytes = readStream(is);
            //BitmapFactory.decodeStream(inputStream, null, options);
            Log.d("BMP", "byte length:" + bytes.length);
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
            System.gc();
            // Log.d("BMP", "Size:" + bmp.getByteCount());
            return bmp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }












    //網路抄下來的還沒試
    private File getPhotoDir(){
        File storDirPrivate = null;
        File storDirPublic = null;

        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){

            //private,只有本应用可访问
            storDirPrivate = new File (
                    Environment.getExternalStorageDirectory()
                            + CAMERA_DIR
                            + albumName
            );

            //public 所有应用均可访问
            storDirPublic = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    albumName);

            if (storDirPublic != null) {
                if (! storDirPublic.mkdirs()) {
                    if (! storDirPublic.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }
        }else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storDirPublic;//或者return storDirPrivate;

    }


}
