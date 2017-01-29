package com.example.linar.tal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    private WebView mWebView;
    private String oldest, fb_dtsg, xhpc_targetid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = (WebView) findViewById(R.id.checkLogin);

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.loadUrl("http://www.facebook.com");

        // Forza la versione desktop del sito
        //TODO: capire quale browser usare
        mWebView.getSettings().setUserAgentString("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0");

       //Permette l'esecuzione di setVariabili
        mWebView.addJavascriptInterface(new MyJavaScriptInterface(), "INTERFACE");

        // Force links and redirects to open in the WebView instead of in a browser
        mWebView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {
                // Inject file js into webpage
                injectScriptFile jquery = new injectScriptFile(getApplicationContext(), view, "js/jquery.js");
                injectScriptFile actions = new injectScriptFile(getApplicationContext(), view, "js/actions.js");

                // Get response from webpage function to set oldest and the other variable
                view.loadUrl("javascript:window.INTERFACE.setVariabili(document.getElementsByClassName('pam uiBoxLightblue tickerMoreLink uiMorePagerPrimary')[0].getAttribute('ajaxify'), document.getElementsByName('fb_dtsg')[0].value, document.getElementsByName('xhpc_targetid')[0].value)");
                //view.loadUrl("javascript:test();");
            }
        });

    }
}
