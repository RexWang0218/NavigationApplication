package com.example.rex_wang.navigationapplication;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rex_wang.navigationapplication.beacon.BeaconFormat;
import com.example.rex_wang.navigationapplication.beacon.BeaconList;
import com.example.rex_wang.navigationapplication.sql.SQLiteDB;
import com.example.rex_wang.navigationapplication.utility.BluetoothSetting;
import com.example.rex_wang.navigationapplication.utility.DL;
import com.example.rex_wang.navigationapplication.utility.Img;
import com.example.rex_wang.navigationapplication.utility.NetworkSetting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ScanActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, BluetoothAdapter.LeScanCallback {
    private final String TAG = " --- ScanActivity --- ";

    private BluetoothAdapter mBluetoothAdapter;
    private int mCounter = 60;
    private Timer mTimer;
    private boolean Scanning, Counting,isCounting;
    private SQLiteOpenHelper DB = new SQLiteDB(this);

    // 物件宣告
    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout mLinearLayout;
    private Button btn_share;
    private String firstBeacon;
    private ImageView mImageView;
    private String url_video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        Log.d(TAG, " onCreate ");
        init();
    }

    // 物件宣告
    private void init() {
        Log.d(TAG, " init ");
        // 下拉更新元件
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_scan);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        // 內容顯示設定
        mLinearLayout = (LinearLayout) findViewById(R.id.ll_scan);
        mLinearLayout.setVisibility(View.GONE);
        // share按鈕
        btn_share = (Button) findViewById(R.id.btn_scan_share);
        btn_share.setOnClickListener(this);
        // 標題
        mToolbar = (Toolbar) findViewById(R.id.toolbar_scan);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Demo-導覽系統");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // 檢查網路狀態與藍芽，進行搜尋iBeacon
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
        } else if (!BluetoothSetting.isConnected(this)) {
            Intent it = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(it);
        } else {
            startScan();
        }
    }

    // 關閉搜尋與計數器
    @Override
    protected void onPause() {
        super.onResume();
        Log.d(TAG, " onPause ");
        stopScan();
        stopCount();
    }

    // 關藍芽
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, " onDestroy ");
        BluetoothSetting.disable(this);
        DB.close();
    }


    private void startScan() {
        Log.d(TAG, " startScan ");
        if(isCounting){
            stopCount();
        }
        mBluetoothAdapter = BluetoothSetting.bluetoothAdapter;
        mBluetoothAdapter.startLeScan(this);
        Scanning = true;
        mLinearLayout.setVisibility(View.GONE);
        BeaconList.cleanList();
    }

    private void stopScan() {
        Log.d(TAG, " stopScan ");
        if (Scanning) {
            mBluetoothAdapter.stopLeScan(this);
            Scanning = false;
        }
    }


    // 下拉進行搜尋
    @Override
    public void onRefresh() {
        if (!Scanning) {
            startScan();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_scan_share) {
            Toast.makeText(this, "未設置功能", Toast.LENGTH_SHORT).show();
        }
        if(view.getId() == R.id.btn_scan_video){
            Intent it = new Intent(this,VideoActivity.class);
            it.putExtra("url",url_video);
            startActivity(it);
        }
    }

    public class timerTask extends TimerTask {
        @Override
        public void run() {
            Log.d(TAG, " task/run ");
            Message message = new Message();
            if (mCounter != 0) {
                mCounter--;
                message.what = 1;
                handler.sendMessage(message);
            } else {
                message.what = 2;
                handler.sendMessage(message);
            }
        }
    }

    private void startCount() {
        Log.d(TAG, " startCount ");
        isCounting = true;
        mTimer = new Timer();
        mCounter = 60;
        mTimer.schedule(new ScanActivity.timerTask(), 0, 1000);// 0 秒開始,間格1秒
    }

    private void stopCount() {
        Log.d(TAG, " stopCount ");
        if (isCounting) {
            mTimer.cancel();
//            mTimer.purge();
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, " handle ");
            switch (msg.what) {
                case 1:
                    Log.i(TAG, " 1 " + mCounter);
                    String ttt = String.valueOf(mCounter) + " 秒";
                    break;
                case 2:
                    Log.i(TAG, " 2 " + mCounter);
                    stopCount();
                    startScan();
                    break;
                case 3:
                    Log.i(TAG, " 3 " + mCounter);
                    mImageView.setImageBitmap(Img.getImg());
            }
        }
    };


    //-----------------------------------------------------------------------------------//
    @Override
    public void onLeScan(final BluetoothDevice bluetoothDevice, final int rssi, final byte[] scanBytes) {
        Log.d(TAG, " onLeScan ");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String ScanResult = BeaconList.decode(bluetoothDevice, rssi, scanBytes);
                switch (ScanResult) {
                    case "success":
                        stopScan();
                        SQLiteDatabase dw = DB.getWritableDatabase();
                        SQLiteDatabase dr = DB.getReadableDatabase();
                        Cursor cursor = dr.rawQuery("SELECT * FROM BeaconTable", null);

                        if (cursor.getCount() != 0) {
                            Log.i(TAG, String.valueOf(cursor.getCount()));
                            dw.execSQL("DROP TABLE IF EXISTS BeaconTable");
                            DB.onCreate(dw);
                        }

                        Boolean contain = true;
                        List<BeaconFormat> BL = BeaconList.mList;
                        for (BeaconFormat ble : BL) {
                            if (ble.getDistance().equals("0.00")) {
                                contain = false;
                                break;
                            } else {
                                ContentValues cv = new ContentValues();
                                cv.put("Address", ble.getAddress());
                                cv.put("Name", ble.getName());
                                cv.put("Rssi", ble.getRssi());
                                cv.put("Distance", ble.getDistance());
                                Log.i(TAG, ble.getAddress());
                                Log.i(TAG, ble.getName());
                                Log.i(TAG, String.valueOf(ble.getRssi()));
                                Log.i(TAG, ble.getDistance());
                                dw.insert("BeaconTable", null, cv);
                            }
                        }
                        if (contain) {
                            setInfo();
                        } else {
                            Log.i(TAG, " Scan有誤 ");
                            BeaconList.cleanList();
                            startScan();
                        }
                        break;
                    case "搜尋不到適合的裝置...":
                        stopScan();
                        setInfoAlertDialog("系統告示", "無搜尋到任何資訊");
                        break;
                }
            }
        });
    }

    private void setInfo() {
        SQLiteDatabase dw = DB.getWritableDatabase();
        Cursor cursor = dw.rawQuery("SELECT * FROM BeaconTable", null);
        cursor.moveToFirst();
        firstBeacon = cursor.getString(1);

        Selectdata selectdata = new Selectdata();
        selectdata.execute("http://163.18.42.22/beaconweb/wproject/sqltest.aspx");

    }

    private void setInfoAlertDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("重新搜尋", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startScan();
                    }
                }).setNegativeButton("返回首頁", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        }).show();
    }

    private class Selectdata extends AsyncTask<String, Integer, String> {
        private ProgressDialog dialog;
        private String txt_scan_introduction, txt_scan_name, url_img;

        @Override
        protected String doInBackground(String... params) {
            Log.i(TAG, params[0]);
            InputStream is = DL.getURL(params[0]);
            String JsonString = DL.streamToString(is, "UTF-8");
            return JsonString;
        }

        @Override
        protected void onPreExecute() {
            //背景執行續進度對話框
            dialog = DL.createProgressDialog(ScanActivity.this, "獲取資料中...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            Log.i("資料測試", s);
            Boolean Received = false;
            try {
                JSONArray jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    if (firstBeacon.equals(jsonObject.getString("address"))) {
                        Log.i(TAG, "--------------------");

                        txt_scan_name = jsonObject.getString("name");
                        txt_scan_introduction = jsonObject.getString("introduction");
                        url_img = jsonObject.getString("img");
                        url_video = jsonObject.getString("video");
                        Received = true;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (Received) {
                Log.i(TAG, "--------------------");
                TextView txt_name = (TextView) findViewById(R.id.txt_scan_name);
                TextView txt_introduction = (TextView) findViewById(R.id.txt_scan_introduction);

                txt_name.setText(txt_scan_name);
                txt_introduction.setText(txt_scan_introduction);

                mImageView = (ImageView) findViewById(R.id.img_scan);
                if (url_img.equals("") || url_img.equals("null")) {
                    Log.i(TAG, "+++++++++");
                    mImageView.setVisibility(View.GONE);
                } else {
                    Log.i(TAG, "--------------------");

                    mImageView.setVisibility(View.VISIBLE);
                    Img.handleWebPic(url_img, handler);
                }

                Button btn_video = (Button) findViewById(R.id.btn_scan_video);
                if (url_video.equals("") || url_video.equals("null")) {
                    btn_video.setVisibility(View.GONE);
                } else {
                    btn_video.setVisibility(View.VISIBLE);
                    btn_video.setOnClickListener(ScanActivity.this);
                }

                mLinearLayout.setVisibility(View.VISIBLE);
                if (Counting) {
                    startCount();
                }
            }
        }
    }

    /**
     * 建立 Menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        Log.i(TAG, " onCreateOptionsMenu ");
        return true;
    }

    /**
     * 目錄準備事件
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.i(TAG, "onPrepareOptionsMenu");

        if (Counting) {
            menu.findItem(R.id.action_scan).setTitle("停止搜尋");
        } else {
            menu.findItem(R.id.action_scan).setTitle("開始循序");
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionItemSelected");
        String SelectItem = (String) item.getTitle();
        Log.i("--- 目錄系統 ---", " 選擇 " + SelectItem);

        if (Counting) {
            stopCount();
            Counting = false;
        } else {
            Counting = true;
            startScan();
        }
        return super.onOptionsItemSelected(item);
    }
}
