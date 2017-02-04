package com.example.linar.tal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by linar on 04/02/2017.
 */


public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
