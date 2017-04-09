package com.example.rex_wang.navigationapplication.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Rex_Wang on 2017/3/29.
 */

public class Img {

    private static Bitmap bmp;

    public static Bitmap getImg() {
        return bmp;
    }

    public static void handleWebPic(final String url, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 執行下載後的圖片
                bmp = getUrlPic(url);
                // 將回傳訊號給Handle
                Message msg = new Message();
                msg.what = 3;
                // 發出訊號
                handler.sendMessage(msg);
            }
        }).start();
    }

    // 確保檔案執行 有順序執行完後 在run下一個 !
    public static synchronized Bitmap getUrlPic(String url) {
        Log.i("--- ---", " getUrlPic ");
        Bitmap webImg = null;
        InputStream inputStream = null;

        try {
            // 宣告網址
            URL imgUrl = new URL(url);
            // 宣告連結
            HttpURLConnection httpURLConnection = (HttpURLConnection) imgUrl.openConnection();
            // 連結
            httpURLConnection.connect();
            // 讀取 連接流
            inputStream = httpURLConnection.getInputStream();
            // 讀取 連接流大小


            /** 連結後，產生圖片方式
             *  (1) Stream → Bitmap
             *      Bitmap bmp = BitmapFactory.decodeStream( inputStream  is );
             *
             *  (2) Byte[] → Bitmap
             *      Bitmap bmp = BitmapFactory..decodeByteArray( 來源陣列 , 起始索引值 , 陣列大小 )
             */

            // Stream → Bitmap
            // webImg = BitmapFactory.decodeStream(inputStream);


            // Stream → Bitmap
            int tmpLength = 512;
            // -- 內容大小
            int length = (int) httpURLConnection.getContentLength();
            int readLen = 0;
            int desPos = 0;

            byte[] img = new byte[length]; // 內容大小
            byte[] tmp = new byte[tmpLength]; // 512

            // 拆解inputStream一次的讀取量 = tmp 寫入img
            while((readLen = inputStream.read(tmp))!=-1){
                // System.arraycopy( 來源陣列，起始索引值，目的陣列，起始索引值，複製長度);
                System.arraycopy(tmp, 0, img, desPos, readLen);
                desPos += readLen;
            }
            webImg = BitmapFactory.decodeByteArray(img, 0, img.length);


            // 當 讀取值累積大小 不等於 連接流大小
            if(desPos != length){
                throw new IOException("Only read" + desPos + "bytes. Fail");
            }

            // 斷取連結
            httpURLConnection.disconnect();

        } catch (IOException e) {
            Log.e("IOException", e.toString());
        }

        return webImg;
    }
}
