package com.tap.ilman.ta04;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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
    private void tryToDownloadFile() {

        String server = PreferenceManager.getDefaultSharedPreferences(this).getString("prefServer", "http://192.168.43.50:8080/api") + "/main";
        Log.v("TEST", "from perference setting the value is: " + server);
        new DownloadFileFromURL(this, getApplicationContext(), pb, "/menu/main").execute(server);

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


//    private void showUserSettings() {
//        SharedPreferences sharedPrefs = PreferenceManager
//                .getDefaultSharedPreferences(this);
//
//        StringBuilder builder = new StringBuilder();
//
//        builder.append("\n Username: "
//                + sharedPrefs.getString("prefUsername", "NULL"));
//
//        builder.append("\n Send report:"
//                + sharedPrefs.getBoolean("prefSendReport", false));
//
//        builder.append("\n Sync Frequency: "
//                + sharedPrefs.getString("prefSyncFrequency", "NULL"));
//
//        builder.append("\n Api Server: "
//                + sharedPrefs.getString("prefServer", "NULL"));
//        builder.append("\n \ufffd \u2558");
//        ProgressBar settingsTextView = (ProgressBar) findViewById(R.id.debug_check);
//
//        //settingsTextView.set(builder.toString());
//    }
}





class ClassViewHolder extends RecyclerView.ViewHolder {
    // each data item is just a string in this case
    public TextView txtSmall;
    public TextView txtBig;
    public LinearLayout smallicons;

    public ClassViewHolder(View v) {
        super(v);
        txtSmall = (TextView) v.findViewById(R.id.smallCap);
        txtBig = (TextView) v.findViewById(R.id.bigCap);
        smallicons = (LinearLayout) v.findViewById(R.id.class_icons);
    }
}

enum MenuCategory {
    SATU, DUA, TIGA,
    EMPAT, LIMA, ENAM, SATUAN, GEOMETRI,
    LUAS, BUAH, CAMPURAN, HARD, SIMPLE, VOLUME, WAKTU,
    NEGATIVE, UANG, BERAT, PUKUL,
    PANJANG, PANGKAT, KELILING, PERSEN, JMD, TEMPERATURE, SPEED, MAINUNKNOWN, UNKNOWN;
}

class MenuItem implements Comparable<MenuItem> {

    MenuCategory cat;
    public int amount;


    public MenuItem() {
    }

    public MenuItem(MenuCategory cat, int amount) {
        this.cat = cat;
        this.amount = amount;

    }

    public MenuCategory getType() {
        return cat;
    }

    public void setType(MenuCategory it) {
        this.cat = it;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return new StringBuilder("[").append(this.getType().name()).append(" ").append(this.getAmount()).append(" soal").append("]").toString();
    }

    @Override
    public int compareTo(MenuItem another) {
        return this.getType().compareTo(another.getType());
    }
}

class CatViewHolder extends RecyclerView.ViewHolder {
    // each data item is just a string in this case
    public TextView bigIcon;
    public TextView txtSmall;
    public TextView txtBig;


    public CatViewHolder(View v) {
        super(v);
        bigIcon = (TextView) v.findViewById(R.id.bigIcon);
        txtSmall = (TextView) v.findViewById(R.id.cat_descr2);
        txtBig = (TextView) v.findViewById(R.id.cat_descr);
    }
}

class MainMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // The items to display in your RecyclerView
    private List<MenuItem> items;

    private final int USER = 0, IMAGE = 1;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MainMenuAdapter(List<MenuItem> items) {
        this.items = items;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.items.size();
    }

    RecyclerView mRecyclerView;


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;

    }


    @Override
    public int getItemViewType(int position) {
        //this gives item view Type
        MenuCategory a = items.get(position).getType();

        switch (a) {
            case SATU:
            case DUA:
            case TIGA:
            case EMPAT:
            case LIMA:
            case ENAM:
                return 1;
            case GEOMETRI:
            case SATUAN:
            case LUAS:
            case BUAH:
            case CAMPURAN:
            case HARD:
            case SIMPLE:
            case VOLUME:
            case WAKTU:
            case NEGATIVE:
            case UANG:
            case BERAT:
            case PUKUL:
            case PANJANG:
            case PANGKAT:
            case KELILING:
            case PERSEN:
            case JMD:
            case TEMPERATURE:
            case SPEED:
                return 2;
            default:
                return -1;
        }


    }

    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case 1:
                View v1 = inflater.inflate(R.layout.class_item, viewGroup, false);
                v1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(mRecyclerView.getContext(), DaftarSoalActivity.class)
                                .putExtra("key", items.get(mRecyclerView.getChildAdapterPosition(v)).getType().name().toLowerCase())
                                .putExtra("type", 1);
                        mRecyclerView.getContext().startActivity(i);
                    }
                });
                viewHolder = new ClassViewHolder(v1);
                break;
            case 2:
                View v2 = inflater.inflate(R.layout.category_item, viewGroup, false);
                v2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(mRecyclerView.getContext(), DaftarSoalActivity.class)
                                .putExtra("key", items.get(mRecyclerView.getChildAdapterPosition(v)).getType().name().toLowerCase())
                                .putExtra("type", 2);

                        mRecyclerView.getContext().startActivity(i);

                    }
                });
                viewHolder = new CatViewHolder(v2);
                break;
            default:
                View v3 = inflater.inflate(R.layout.class_item, viewGroup, false);
                viewHolder = new ClassViewHolder(v3);
                break;
        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case 1:
                ClassViewHolder vh1 = (ClassViewHolder) viewHolder;

                configureClssView(vh1, position);
                break;
            case 2:
                CatViewHolder vh2 = (CatViewHolder) viewHolder;
                configureCatView(vh2, position);
                break;
            default:
                ClassViewHolder vh3 = (ClassViewHolder) viewHolder;
                configureUnknownClssView(vh3, position);
                break;
        }
    }

    private void configureUnknownClssView(ClassViewHolder vh3, int position) {
        vh3.txtBig.setText("?");
        vh3.txtSmall.setText((items.size() <= 1 ? "please download new data" : "data error"));
    }

    private void configureCatView(CatViewHolder vh2, int position) {
        MenuItem x = items.get(position);
        vh2.bigIcon.setText(giveUnicodeIcon(x.getType()));
        vh2.txtBig.setText(capitalize(x.getType().name()));
        vh2.txtSmall.setText(x.getAmount() + " soal");
    }

    private void configureClssView(ClassViewHolder vh1, int position) {
        MenuItem x = items.get(position);
        vh1.txtBig.setText(giveUnicodeIcon(x.getType()));
        vh1.txtSmall.setText(x.getAmount() + " soal");
    }

    static String capitalize(String sx) {
        StringBuilder s = new StringBuilder(sx.toLowerCase());
        s.setCharAt(0, Character.toUpperCase(s.charAt(0)));
        return s.toString();
    }


    static String giveUnicodeIcon(MenuCategory x) {
        switch (x) {
            case SATU:
                return "1";
            case DUA:
                return "2";
            case TIGA:
                return "3";
            case EMPAT:
                return "4";
            case LIMA:
                return "5";
            case ENAM:
                return "6";
            case GEOMETRI:
                return "\u25a6";
            case SATUAN:
                return "kg";
            case LUAS:
                return "\u25ef";
            case BUAH:
                return "\u2696";
            case CAMPURAN:
                return "(1 \u00d7 2 : 4)";
            case HARD:
                return "(x)";
            case SIMPLE:
                return "1+2=3";
            case VOLUME:
                return "\ud83d";
            case WAKTU:
            case JMD:
                return "\udd50";
            case PANJANG:
                return "\uD83D\uDCCF";
            case PANGKAT:
                return "X\u00b2";
            case KELILING:
                return "\u25cb";
            case PERSEN:
                return "%";
            case TEMPERATURE:
                return "\u00b0C";
            case SPEED:
                return "km/h";
            default:
                return "\ufffd";
        }
    }

    static void logMenuITEM(List<MenuItem> a) {
        for (MenuItem c : a) {
            Log.v("TEST", "[" + c.getType().name() + " " + c.getAmount() + "]");
        }
    }
}

class EqualSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int mSpaceHeight;

    public EqualSpaceItemDecoration(int mSpaceHeight) {
        this.mSpaceHeight = mSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.bottom = mSpaceHeight;
        outRect.top = mSpaceHeight;
        outRect.left = mSpaceHeight + 2;
        outRect.right = mSpaceHeight + 2;
    }
}