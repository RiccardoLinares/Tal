package com.example.linar.tal;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;


import java.io.UnsupportedEncodingException;

import static com.example.linar.tal.R.id.tableLayout;

/**
 * Created by linar on 29/01/2017.
 */

public class MyJavaScriptInterface {

    // VARIABILI
    private String oldest, fb_dtsg, xhpc_targetid;
    public Context mContext;


    // COSTRUTTORI
    public MyJavaScriptInterface() {
    }

    public MyJavaScriptInterface(Context c) {
        mContext = c;
    }


    //METODI
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

    @JavascriptInterface
    public void riceviDati(String[] input) {
        //input è l'array di dati che mi serve
        //è mediante questi che devo riempire la scrollView
        //il problema è che non so come accedere alla scrollview del main da qui
        Log.d("ARRAY", String.valueOf(input.length));
        //riempiScrollView(input);
        if (input.length > 0) {
            setBooleanRicevuti(true);
        } else {
            setBooleanRicevuti(false);
        }
    }

    @JavascriptInterface
    public void riceviDati_test(String nome, String link) {
        //input è l'array di dati che mi serve
        //è mediante questi che devo riempire la scrollView
        //il problema è che non so come accedere alla scrollview del main da qui
        Log.d("RISULTATI", nome + " " + link);
        riempiScrollView(nome, link);

    }


    boolean datiRicevuti = false;

    public void setBooleanRicevuti(boolean b) {
        datiRicevuti = b;
    }

    public boolean getBooleanRicevuti() {
        return datiRicevuti;
    }


    private void riempiScrollView(final String nome, final String link) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TableLayout table = (TableLayout) ((Activity) mContext).findViewById(R.id.tableLayout);
                TextView[] textArray;
                TableRow[] tableRow;
                textArray = new TextView[1];
                tableRow = new TableRow[1];

                // Crea dinamicamente i riquadri degli aggiornamenti
                //Create the tablerows
                tableRow[0] = new TableRow(mContext);
                tableRow[0].setBackgroundColor(Color.GRAY);
                // Here create the TextView dynamically
                textArray[0] = new TextView(mContext);

                //TODO
                textArray[0].setText(nome + " " + link);
                //textArray[i].setText("testo di prova");

                textArray[0].setTextColor(Color.WHITE);
                textArray[0].setPadding(5, 5, 5, 5);
                tableRow[0].addView(textArray[0]);

                table.addView(tableRow[0]);

            }
        });


    }

}
