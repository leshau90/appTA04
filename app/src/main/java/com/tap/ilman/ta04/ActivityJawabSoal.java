package com.tap.ilman.ta04;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

public class ActivityJawabSoal extends AppCompatActivity implements DownloadAndRead{

    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_jawab_soal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        pb = (ProgressBar) findViewById(R.id.debug_check_JawabSoal);

       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        startActivity(new Intent(this,DaftarSoalActivity.class).putExtra("key",getIntent().getStringExtra("back")));
    }

    private void tryToDownloadFile() {

        String server = PreferenceManager.getDefaultSharedPreferences(this).getString("prefServer", "http://192.168.43.50:8080/api") + "/main";
        Log.v("TEST", "from perference setting the value is: " + server);
        new DownloadFileFromURL(this, getApplicationContext(), pb, "/menu/main").execute(server);

    }

    @Override
    public void doneDownoading() {

    }
}
