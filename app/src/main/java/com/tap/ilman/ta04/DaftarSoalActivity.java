package com.tap.ilman.ta04;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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


        mAdapter = new DaftarSoalMenuAdapter(items,key);
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
        setTitlem();

    }

    private void setTitlem(){
        StringBuilder sb = new StringBuilder();
        int i=getIntent().getIntExtra("type",0);
        String s = MainMenuAdapter.capitalize(key);
        switch(i){
            case 1:
                sb.append(getString(R.string.judul_kelas_daftar_soal)).append(" ").append(s);
                break;
            case 2:
                sb.append(getString(R.string.judul_kategori_daftar_soal)).append(" ").append(s);
                break;
            default:
                sb.append(getString(R.string.judul_unknown_daftar_soal));
                break;
        }
        setTitle(sb.toString());
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
        int maxQuickView = Integer.parseInt(sharedPref.getString("prefMaxQuickViewChar", "50"));
        String server = new StringBuilder(sharedPref
                .getString("prefServer", "http://192.168.43.50:8080/api")).toString();

        Log.v("TEST", "loading brief part of daftar soal..server url: " + server);
        new GetQuickViewOrDownloadFromURL(getApplicationContext(), pb
                , items
                , server, this, maxQuickView).execute();
        Log.v("TEST", "request go to separate thred..server url: " + server);
    }


    @Override
    public void doneDownoading() {
        items.clear();
        try {
            Log.v("mapping", "hope this doesnt took long time");
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
            mAdapter = new DaftarSoalMenuAdapter(items,key);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

            Log.v("TEST", "call load quickview");
            loadQuickView();
        }
    }

    @Override
    public void partiallyDone(int i) {
        //Log.v("TEST","on ui ..signal received updating number "+i+" item " +items.get(i));
        mAdapter.notifyItemChanged(i);
    }
}



class DaftarSoalMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    RecyclerView rv;
    List<ItemDaftarSoal> items;
    String nextActivityBack;


    public DaftarSoalMenuAdapter(List<ItemDaftarSoal> items, String key) {
        this.items = items;
        this.nextActivityBack = key;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        rv = recyclerView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        View v1 = inflater.inflate(R.layout.daftar_soal_item, viewGroup, false);
        v1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!items.get(rv.getChildAdapterPosition(v)).get_id().isEmpty()) {
                    Intent i = new Intent(rv.getContext(), JawabSoalActivity.class)
                            .putExtra("key", items.get(rv.getChildAdapterPosition(v)).get_id())
                            .putExtra("back",nextActivityBack);
                    rv.getContext().startActivity(i);
                }
            }
        });
        viewHolder = new ItemDaftarSoalVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemDaftarSoalVH vh = (ItemDaftarSoalVH) holder;
        Log.v("Adapter", "onbind...updating an item at index " + position + "  " + items.get(position));
        vh.QuickView.setText(items.get(position).content);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}

class ItemDaftarSoalVH extends RecyclerView.ViewHolder {


    public TextView QuickView;
    public LinearLayout ll;

    public ItemDaftarSoalVH(View itemView) {
        super(itemView);
        QuickView = (TextView) itemView.findViewById(R.id.quickview_daftar_soal);
        ll = (LinearLayout) itemView.findViewById(R.id.cat_icons_daftar_soal);

    }
}

class ItemDaftarSoal {
    String _id;

    public void setContent(String content) {
        this.content = content;
    }

    String content="";

    public ItemDaftarSoal() {
    }

    public ItemDaftarSoal(String _id, String content) {
        this._id = _id;
        this.content = content;    }

    @Override
    public String toString() {
        return new StringBuilder('[').append(_id).append(" , ").append(content).append(']').toString();
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getContent() {
        return this.content;
    }
}

