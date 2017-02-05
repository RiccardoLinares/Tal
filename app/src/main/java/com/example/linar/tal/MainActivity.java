package com.example.linar.tal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

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
    private TableLayout tableLayout;
    private TextView console;
    private InternetConnection internet = new InternetConnection(this);
    private String urlWebView = "https://www.facebook.com";
    private CallbackManager mFacebookCallbackManager;
    private AccessTokenTracker mFacebookAccessTokenTracker;
    private Button logoutButton;

    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        mFacebookCallbackManager = CallbackManager.Factory.create();

        // Monitora il cambiamento del token di accesso
        mFacebookAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                if (newAccessToken == null) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        };

        // Carica il contenuto
        setContentView(R.layout.activity_main);

        //Istanza dei Widget
        mWebView = (WebView) findViewById(R.id.checkLogin);
        startRicerca = (Button) findViewById(R.id.startRicerca);
        nuovaRicerca = (Button) findViewById(R.id.nuovaRicerca);
        logoutButton = (Button) findViewById(R.id.logoutButton);

        searchBar = (EditText) findViewById(R.id.searchBar);
        tableLayout = (TableLayout) findViewById(R.id.tableLayout);
        console = (TextView) findViewById(R.id.textConsole);

        progress = ProgressDialog.show(this, "", "Loading", true);

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
        final MyJavaScriptInterface interfaccia = new MyJavaScriptInterface(this, progress);
        mWebView.addJavascriptInterface(interfaccia, "INTERFACE");

        // Force links and redirects to open in the WebView instead of in a browser
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon){
                progress.show();
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                // Inject file js into webpage
                InjectScriptFile jquery = new InjectScriptFile(getApplicationContext(), view, "js/jquery.js");
                InjectScriptFile actions = new InjectScriptFile(getApplicationContext(), view, "js/actions.js");
                progress.dismiss();
            }
        });

        //La funzione JS parte quando premo il bottone
        startRicerca.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (searchBar.getText().toString().length() > 0) {
                    effettuaRicerca(mWebView, searchBar.getText().toString());
                } else {
                    console.setText("Inserisci un nome!");
                }
            }
        });

        //La funzione JS si ferma quando premo il bottone
        nuovaRicerca.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ricominciaRicerca(mWebView);
            }
        });

        // Facebook Logout
        logoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FacebookSdk.sdkInitialize(getApplicationContext());
                LoginManager.getInstance().logOut();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Controlla se l'utente è loggato, in caso contrario apre l'activity del login
        /*String token = AccessToken.getCurrentAccessToken().getToken();
        if (token == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }*/

    }

    private void effettuaRicerca(WebView webView, String text) {
        if (internet.isAvailable()) {
            console.setText("Ricerca in corso...");
            webView.loadUrl("javascript:startServer()");
            webView.loadUrl("javascript:nomeRicerca('" + text + "')");
        } else {
            internet.errMessage();
        }

    }

    private void ricominciaRicerca(WebView webView) {
        if (internet.isAvailable()) {
            console.setText("Reset della ricerca...");
            webView.loadUrl("javascript:stopServer()");
            webView.loadUrl(urlWebView);
            tableLayout.removeAllViews();

        } else {
            internet.errMessage();
        }
    }

}
