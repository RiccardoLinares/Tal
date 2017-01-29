package com.example.linar.tal;

import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

/**
 * Created by linar on 29/01/2017.
 */

public class MyJavaScriptInterface {

    private String oldest, fb_dtsg, xhpc_targetid;

    private TextView contentView;

        // Istanzia una TextView e la attribuisce alla variabile privata contentView
        public MyJavaScriptInterface() {
        }

        // Istanzia le variabili oldest, fb_dtsg, xhpc_targetid
        @JavascriptInterface
        public void setVariabili(String old, String dtsg, String xhpc) throws UnsupportedEncodingException {
            oldest = old.substring(old.indexOf("oldest=") + 7, old.indexOf("&source"));
            fb_dtsg = dtsg;
            xhpc_targetid = xhpc;

            Log.d("oldest", oldest);
            Log.d("fb_dtsg", fb_dtsg);
            Log.d("xhpc_targetid", xhpc_targetid);
        }
}
