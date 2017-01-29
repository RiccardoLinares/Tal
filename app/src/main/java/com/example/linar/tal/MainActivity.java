package com.example.linar.tal;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.lang.reflect.Array;

public class MainActivity extends AppCompatActivity {

    private WebView mWebView;
    private Button ricerca;
    private EditText searchBar;
    private String oldest, fb_dtsg, xhpc_targetid;
    private TableLayout tableLayout;
    private TextView[] textArray;
    private TableRow[] tableRow;
    private int num_aggiornamenti = 10; // TODO cambiare con il numero di aggiornamenti effettivamente ricevuti da javascritp

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Istanza dei Widget
        mWebView = (WebView) findViewById(R.id.checkLogin);
        ricerca = (Button) findViewById(R.id.button);
        searchBar = (EditText) findViewById(R.id.searchBar);
        tableLayout = (TableLayout) findViewById(R.id.tableLayout);
        textArray = new TextView[num_aggiornamenti];
        tableRow = new TableRow[num_aggiornamenti];

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
                view.loadUrl("javascript:window.INTERFACE.getRisultati(risultatiTrovati())");

            }
        });

         //La funzione JS parte quando premo il bottone
        ricerca.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                //TODO controllo su searchbar vuota
                effettuaRicerca(mWebView, searchBar.getText().toString());
            }

        });


    }

    private void effettuaRicerca(WebView webView, String text) {
        webView.loadUrl("javascript:nomeRicerca('"+text+"')");
    }

    //Input: array con i risultati e la scrollview
    private void riempiScrollView(WebView webView, ScrollView view, Array risultati){
        // Crea dinamicamente i riquadri degli aggiornamenti
        for (int i = 0; i < num_aggiornamenti; i++) {
            //Create the tablerows
            tableRow[i] = new TableRow(this);
            tableRow[i].setBackgroundColor(Color.GRAY);
            // Here create the TextView dynamically
            textArray[i] = new TextView(this);
            textArray[i].setText("testo");
            textArray[i].setTextColor(Color.WHITE);
            textArray[i].setPadding(5, 5, 5, 5);
            tableRow[i].addView(textArray[i]);
            tableLayout.addView(tableRow[i]);

        }
    }
}
