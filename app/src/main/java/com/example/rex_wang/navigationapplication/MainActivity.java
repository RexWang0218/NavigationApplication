package com.example.rex_wang.navigationapplication;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.rex_wang.navigationapplication.utility.BluetoothSetting;
import com.example.rex_wang.navigationapplication.utility.DL;
import com.example.rex_wang.navigationapplication.utility.Img;
import com.example.rex_wang.navigationapplication.utility.NetworkSetting;
import com.facebook.FacebookSdk;
import com.facebook.share.widget.LikeView;


/**
 * MainActivity
 * - 確認裝置是否有支援藍芽
 * - 確認手機已開啟藍芽以及網路
 * - 切換至 ScanActivity
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "--- MainActivity ---";
    private Toolbar tb_main;
    private Button btn_location_information, btn_activity_list;
    private ImageView img_main;
    private SoundPool mSoundPool;
    private LikeView lv_facebook;
    private int welcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, " onCreate ");

        FacebookSdk.sdkInitialize(getApplicationContext());
        init();
    }

    private void init() {
        Log.d(TAG, " init ");
        if (BluetoothSetting.isSupport(this)) {
            // 標題列
            tb_main = (Toolbar) findViewById(R.id.toolbar_main);
            tb_main.setTitle("創夢工廠導覽");
            // 活動列表 btn
            btn_activity_list = (Button) findViewById(R.id.btn_activity_list);
            btn_activity_list.setOnClickListener(this);
            // 導覽 btn
            btn_location_information = (Button) findViewById(R.id.btn_location_information);
            btn_location_information.setOnClickListener(this);
            // 首頁圖片
            img_main = (ImageView) findViewById(R.id.img_main);
            img_main.setImageDrawable(getResources().getDrawable(R.drawable.home));
            // 音效
            mSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
            welcome = mSoundPool.load(this, R.raw.welcome, 1);
            // 按讚
            lv_facebook = (LikeView) findViewById(R.id.lv_facebook);
            lv_facebook.setObjectIdAndType("https://www.facebook.com/AndroidOfficial/?ref=br_rs", LikeView.ObjectType.DEFAULT);

        } else {
            Toast.makeText(this, "此設備不支援低號頻藍芽", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, " onResume ");
        if (!NetworkSetting.isConnected(this)) {
            ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setComponent(cn);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

//            Intent it = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
//            startActivityForResult(it, RESULT);
        } else {
//            downloadImg();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, " onPause ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, " onDestroy ");
        BluetoothSetting.disable(this);
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, " onClick ");
        if (view.getId() == R.id.btn_location_information) {
            Intent it = new Intent(this, ScanActivity.class);
            startActivity(it);

        } else if (view.getId() == R.id.btn_activity_list) {
            Toast.makeText(MainActivity.this, "尚未連接功能", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, " onActivityResult ");
    }

    private void downloadImg() {
        final ProgressDialog dialog = DL.createProgressDialog(this, "載入中...");
        dialog.show();
        String url = "http://163.18.42.27:1414/share/home.jpg";
        Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 3:
                        dialog.dismiss();
                        img_main.setImageBitmap(Img.getImg());
                        mSoundPool.play(welcome, 1, 1, 0, 0, 1);
                        break;
                }
                super.handleMessage(msg);
            }
        };
        Img.handleWebPic(url, mHandler);
    }

}
