package com.example.rex_wang.navigationapplication.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Rex_Wang on 2017/3/22.
 */

public class NetworkSetting {
    private static ConnectivityManager connectivityManager;
    private static NetworkInfo networkInfo;

    public static boolean isConnected(Context context) {
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo==null){
            return false;
        }else if(networkInfo.isConnected()){
            return true;
        }else {
            return false;
        }
    }
}
