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
        riempiScrollView(input);
    }


    private void riempiScrollView(String[] risultati) {
        TableLayout table = (TableLayout) ((Activity)mContext).findViewById(R.id.tableLayout);
        TextView[] textArray;
        TableRow[] tableRow;
        textArray = new TextView[risultati.length];
        tableRow = new TableRow[risultati.length];

        // Crea dinamicamente i riquadri degli aggiornamenti
        for (int i = 0; i < risultati.length; i++) {
            //Create the tablerows
            tableRow[i] = new TableRow(mContext);
            tableRow[i].setBackgroundColor(Color.GRAY);
            // Here create the TextView dynamically
            textArray[i] = new TextView(mContext);
            textArray[i].setText(risultati[i]);
            textArray[i].setTextColor(Color.WHITE);
            textArray[i].setPadding(5, 5, 5, 5);
            tableRow[i].addView(textArray[i]);

            table.addView(tableRow[i]);
        }
    }

}
