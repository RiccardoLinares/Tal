package com.example.linar.tal;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private WebView mWebView;
    private Button startRicerca;
    private Button stopRicerca;
    private Button nuovaRicerca;
    private EditText searchBar;
    private String oldest, fb_dtsg, xhpc_targetid;
    public TableLayout tableLayout;
    private ArrayList<String> listaRisultati;
    private View progressContainer;
    private String urlWebView = "http://www.facebook.com";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Istanza dei Widget
        mWebView = (WebView) findViewById(R.id.checkLogin);
        startRicerca = (Button) findViewById(R.id.startRicerca);
        stopRicerca = (Button) findViewById(R.id.stopRicerca);
        nuovaRicerca = (Button) findViewById(R.id.nuovaRicerca);

        searchBar = (EditText) findViewById(R.id.searchBar);
        tableLayout = (TableLayout) findViewById(R.id.tableLayout);

        //Posiziona il pannello di loading in cima
        findViewById(R.id.loadingPanel).bringToFront();

        //Disabilita l'apertura della keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.loadUrl(urlWebView);

        // Forza la versione desktop del sito
        //TODO: capire quale browser usare
        mWebView.getSettings().setUserAgentString("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0");

        //Permette l'esecuzione di setVariabili
        final MyJavaScriptInterface interfaccia = new MyJavaScriptInterface(this);
        mWebView.addJavascriptInterface(interfaccia, "INTERFACE");

        // Force links and redirects to open in the WebView instead of in a browser
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                // Inject file js into webpage
                injectScriptFile jquery = new injectScriptFile(getApplicationContext(), view, "js/jquery.js");
                injectScriptFile actions = new injectScriptFile(getApplicationContext(), view, "js/actions.js");

                // Nasconde il pannello di loading
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            }
        });

        //La funzione JS parte quando premo il bottone
        startRicerca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO controllo su searchbar vuota
                effettuaRicerca(mWebView, searchBar.getText().toString());
            }
        });

        //La funzione JS si ferma quando premo il bottone
        stopRicerca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fermaRicerca(mWebView);
            }
        });

        //La funzione JS si ferma quando premo il bottone
        nuovaRicerca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ricominciaRicerca(mWebView);
            }
        });

    }

    private void effettuaRicerca(WebView webView, String text) {
        webView.loadUrl("javascript:flagServer(true)");
        webView.loadUrl("javascript:nomeRicerca('" + text + "')");
    }

    private void fermaRicerca(WebView webView){
        webView.loadUrl("javascript:flagServer(false)");
    }

    private void ricominciaRicerca(WebView webView){
        fermaRicerca(webView);
        webView.loadUrl("javascript:flagServer(true)");
        webView.loadUrl(urlWebView);
        tableLayout.removeAllViews();
    }

    public TableLayout getTableLayout() {
        return tableLayout;
    }

    //TODO
    /*
    -Stop ricerca
    -Nuova ricerca
    -Tasto invio
     */
}
