package com.example.linar.tal;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.ArrayList;


import java.io.UnsupportedEncodingException;


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
    public void riceviDati(String nome, String img, String link) {
        //input è l'array di dati che mi serve
        //è mediante questi che devo riempire la scrollView
        //il problema è che non so come accedere alla scrollview del main da qui
        Log.d("RISULTATI", nome + " " + img + " " + link);
        riempiScrollView(nome, img, link);

    }


    boolean datiRicevuti = false;

    public void setBooleanRicevuti(boolean b) {
        datiRicevuti = b;
    }

    public boolean getBooleanRicevuti() {
        return datiRicevuti;
    }


    private void riempiScrollView(final String textRisultato, final String imgUrl, final String link) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TableLayout table = (TableLayout) ((Activity) mContext).findViewById(R.id.tableLayout);
                TextView textArray = new TextView(mContext);
                TableRow tableRow = new TableRow(mContext);
                ImageView imgView = new ImageView(mContext);

                Picasso.with(mContext).load(imgUrl).into(imgView);

                //textArray.setText(textRisultato + " " + link);
                textArray.setText(textRisultato);

                tableRow.addView(imgView);
                tableRow.addView(textArray);

                table.addView(tableRow);

            }
        });
    }

}
