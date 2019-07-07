package com.ahmet.postphotos.Config;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;

import com.ahmet.postphotos.R;

/**
 * Created by ahmet on 9/8/2017.
 */

public class MyClipboardManager {

    public static boolean copyToClipboard(Context context, String text){

        try {
            int sdk = Build.VERSION.SDK_INT;
            if (sdk < Build.VERSION_CODES.CUPCAKE) {
                ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
                clipboardManager.setText(text);
            } else {
                ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText(context.getResources()
                        .getString(R.string.app_name), text);
                clipboardManager.setPrimaryClip(clipData);
            }
            return true;
        }catch (Exception ex){
            return false;
        }
    }
}
