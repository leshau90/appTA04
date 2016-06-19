package com.tap.ilman.ta04;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.ProgressBar;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DownloadAndRead {
    private List<MenuItem> items = new ArrayList<>();
    private MainMenuAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private String fileLocation;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AppUtils.checkAndCreateAppDirs(getApplicationContext());

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        // mRecyclerView.setHasFixedSize(true);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        //span list item 2 ,if its last item
        ((GridLayoutManager) mLayoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (position == items.size() - 1) ? 2 : 1;
            }
        });

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new EqualSpaceItemDecoration(4));

        items.add(new MenuItem(MenuCategory.UNKNOWN, 0));

        mAdapter = new MainMenuAdapter(items);
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                determineFiletoBeRead();
            }
        });
        fileLocation = this.getApplicationContext().getFilesDir().toString() + "/menu/main";

        pb = (ProgressBar) findViewById(R.id.debug_check);
        pb.setMax(100);

        determineFiletoBeRead();
        //showUserSettings();
    }

    private void determineFiletoBeRead() {
        try {
            if (!AppUtils.checkMenuFile(getApplicationContext())) {
                tryToDownloadFile();
            } else doneDownoading();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        //showUserSettings();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if cat is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, Pengaturan.class));
            return true;
        }

        if (id == R.id.action_flush) {
            AppUtils.deleteAllFile(getApplicationContext());
            tryToDownloadFile();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void doneDownoading() {
        items.clear();
        try {
            items = AppUtils.jacksonObjectMapper.readValue(new File(fileLocation), new TypeReference<List<MenuItem>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (items.isEmpty()) {
                Log.v("TEST", "FINAL BLOCK:  and the item is empty, seems like the download or file writing is failed ");
                items.add(new MenuItem(MenuCategory.UNKNOWN, 0));
            }

        }
        Log.v("TEST", "there are " + items.size() + " elements in items  ");
        //MainMenuAdapter.logMenuITEM(items);

        if (!items.isEmpty()) {
            //sorter(items);
            Collections.sort(items);
            Log.v("TEST", "after sorting");
            //MainMenuAdapter.logMenuITEM(items);

            Log.v("TEST", "notify update");

            mAdapter = new MainMenuAdapter(items);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void tryToDownloadFile() {

        String server = PreferenceManager.getDefaultSharedPreferences(this).getString("prefServer", "http://192.168.43.50:8080/api") + "/main";
        Log.v("TEST", "from perference setting the value is: " + server);
        new DownloadFileFromURL(this, getApplicationContext(), pb, "/menu/main").execute(server);

    }


    private void showUserSettings() {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        StringBuilder builder = new StringBuilder();

        builder.append("\n Username: "
                + sharedPrefs.getString("prefUsername", "NULL"));

        builder.append("\n Send report:"
                + sharedPrefs.getBoolean("prefSendReport", false));

        builder.append("\n Sync Frequency: "
                + sharedPrefs.getString("prefSyncFrequency", "NULL"));

        builder.append("\n Api Server: "
                + sharedPrefs.getString("prefServer", "NULL"));
        builder.append("\n \ufffd \u2558");
        ProgressBar settingsTextView = (ProgressBar) findViewById(R.id.debug_check);

        //settingsTextView.set(builder.toString());
    }


}


