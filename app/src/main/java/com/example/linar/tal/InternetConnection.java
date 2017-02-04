package com.example.linar.tal;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * Created by linar on 03/02/2017.
 */

public class InternetConnection {

    private WebView webView;
    private Context context;

    public InternetConnection(WebView webView){
        this.webView = webView;
    }

    public InternetConnection(Context context) {
        this.context = context;
    }

    public boolean isAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public void errMessage(){
        Toast.makeText(this.context, "Controlla la connessione internet",
                Toast.LENGTH_LONG).show();
    }
}
