package com.example.rex_wang.navigationapplication.utility;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by Rex_Wang on 2017/3/22.
 */

public class BluetoothSetting {
    private static BluetoothManager bluetoothManager;
    public static BluetoothAdapter bluetoothAdapter;

    public static boolean isSupport(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public static boolean isConnected(Context context) {
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        return bluetoothAdapter.isEnabled();
    }


    public static void disable(Context context) {
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter.isEnabled()){
          bluetoothAdapter.disable();
        }
    }
}
