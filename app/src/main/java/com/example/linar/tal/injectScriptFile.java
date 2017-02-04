package com.example.linar.tal;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Base64;
import android.webkit.WebView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by linar on 29/01/2017.
 */


public class InjectScriptFile {
    // Funzione che inietta il file Js nella webView
    InjectScriptFile(Context myContext, WebView view, String scriptFile) {
        AssetManager mngr = myContext.getAssets();

        InputStream input;
        try {
            input = mngr.open(scriptFile);
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            input.close();

            // String-ify the script byte-array using BASE64 encoding !!!
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            view.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var script = document.createElement('script');" +
                    "script.type = 'text/javascript';" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "script.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(script)" +
                    "})()");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

