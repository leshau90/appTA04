package com.tap.ilman.ta04;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DaftarSoalActivity extends AppCompatActivity implements DownloadAndRead, viewQuickFile {

    private List<ItemDaftarSoal> items = new ArrayList<ItemDaftarSoal>();
    private RecyclerView mRecyclerView;
    private DaftarSoalMenuAdapter mAdapter;
    private String key;
    private String fileLocation;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_soal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_daftar_soal);
        setSupportActionBar(toolbar);
        AppUtils.checkAndCreateAppDirs(getApplicationContext());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_daftarSoal);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new EqualSpaceItemDecoration(4));


        items.add(new ItemDaftarSoal("", ""));


        mAdapter = new DaftarSoalMenuAdapter(items);
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_daftar_soal);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                determinetoDownloadOrReadFile();
            }
        });

        key = getIntent().getStringExtra("key").trim();
        fileLocation = this.getApplicationContext().getFilesDir().toString() + "/menu/" + key;
        pb = (ProgressBar) findViewById(R.id.debug_check_daftarSoal);

        determinetoDownloadOrReadFile();

    }

    private void determinetoDownloadOrReadFile() {
        try {
            if (!AppUtils.checkDaftarSoalFile(getApplicationContext(), key)) {
                tryToDownloadFile();
            } else doneDownoading();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void tryToDownloadFile() {
        String server = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("prefServer", "http://192.168.43.50:8080/api")
                + "/menu/" + key + "/"
                + PreferenceManager.getDefaultSharedPreferences(this)
                .getString("prefLimit", "20");
        new DownloadFileFromURL(this, getApplicationContext(), pb, "/menu/" + key).execute(server);
    }


    private void loadQuickView() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int maxQuickView = sharedPref.getInt("prefMaxQuickViewChar",50);
        String server = new StringBuilder(sharedPref
                .getString("prefServer", "http://192.168.43.50:8080/api")).toString();

        Log.v("TEST", "loading brief part of daftar soal..server url: " + server);
       new GetQuickViewOrDownloadFromURL(getApplicationContext(),pb
               , items
               , server, this,maxQuickView).execute();
        Log.v("TEST", "request go to separate thred..server url: " + server);
    }


    @Override
    public void doneDownoading() {
        items.clear();
        try {
            Log.v("mapping","hope this doesnt took long time");
            items = AppUtils.jacksonObjectMapper.readValue(new File(fileLocation)
                    , new TypeReference<List<ItemDaftarSoal>>() {
                    });
            Log.v("TEST", "there are " + items.size() + " elements in items  ");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (items.isEmpty()) {
                Log.v("TEST", "FINAL: DaftarSOal: and the item is empty, seems like the download or file writing is failed, or the server itself is down ");
                items.add(new ItemDaftarSoal("", "no data"));
            }
        }

        Log.v("TEST", "there are " + items.size() + " elements in items  ");
        if (!items.isEmpty()) {
            Log.v("TEST", "notify update");
            mAdapter = new DaftarSoalMenuAdapter(items);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

            Log.v("TEST","call load quickview");
            loadQuickView();
        }
    }

    @Override
    public void partiallyDone(int i) {
        //Log.v("TEST","on ui ..signal received updating number "+i+" item " +items.get(i));
        mAdapter.notifyItemChanged(i);
    }
}
