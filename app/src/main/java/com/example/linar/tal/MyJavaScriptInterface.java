package com.example.linar.tal;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
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
    boolean flagwWebLoaded = false;


    // COSTRUTTORI
    public MyJavaScriptInterface() {
    }

    public MyJavaScriptInterface(Context c) {
        mContext = c;
    }


    //METODI

    //ricevi i dati da js e riempie la scrollView
    @JavascriptInterface
    public void riceviDati(final String textRisultato, final String imgUrl, String link) {
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

    //ricevi i dati da js ed elimina la schermata di loading
    @JavascriptInterface
    public void nascondiLoading() {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((Activity) mContext).findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            }
        });
    }
}
