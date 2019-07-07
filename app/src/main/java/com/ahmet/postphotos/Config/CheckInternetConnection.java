package com.ahmet.postphotos.Config;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by ahmet on 4/14/2018.
 */

public class CheckInternetConnection {

    private Context context;

    public CheckInternetConnection(Context context) {
        this.context = context;
    }

    public boolean isConnectingToInternet(){

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null){

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()){
                return true;
            }

        }

        return false;

    }
}
