package com.example.linar.tal;

import android.app.Fragment;
import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.TextView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.ArrayList;


import java.io.UnsupportedEncodingException;

/**
 * Created by linar on 29/01/2017.
 */

public class MyJavaScriptInterface {

    private String oldest, fb_dtsg, xhpc_targetid;

    private TextView contentView;
    public Context mContext;
    public MainActivity mainActivity;

    // COSTRUTTORI
    public MyJavaScriptInterface() {
    }
    public MyJavaScriptInterface(Context c) {
        mContext = c;
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

    @JavascriptInterface
    public void riceviDati (String[] input) {
         //input è l'array di dati che mi serve
         //è mediante questi che devo riempire la scrollView
         //il problema è che non so come accedere alla scrollview del main da qui


    }

    /*
    private void riempiScrollView(ArrayList<String> risultati) {
        TextView[] textArray;
        TableRow[] tableRow;
        textArray = new TextView[risultati.size()];
        tableRow = new TableRow[risultati.size()];

        // Crea dinamicamente i riquadri degli aggiornamenti
        for (int i = 0; i < risultati.size(); i++) {
            //Create the tablerows
            tableRow[i] = new TableRow(this);
            tableRow[i].setBackgroundColor(Color.GRAY);
            // Here create the TextView dynamically
            textArray[i] = new TextView(this);
            textArray[i].setText(risultati.get(i));
            textArray[i].setTextColor(Color.WHITE);
            textArray[i].setPadding(5, 5, 5, 5);
            tableRow[i].addView(textArray[i]);
            tableLayout.addView(tableRow[i]);
        }
    }
    */
}
