package com.tap.ilman.ta04;

import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

//import org.apache.commons.lang3.text.WordUtils;

import java.util.List;


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


class ItemDaftarSoalVH extends RecyclerView.ViewHolder {


    public TextView QuickView;
    public LinearLayout ll;

    public ItemDaftarSoalVH(View itemView) {
        super(itemView);
        QuickView = (TextView) itemView.findViewById(R.id.quickview_daftar_soal);
        ll = (LinearLayout) itemView.findViewById(R.id.cat_icons_daftar_soal);

    }



}


/**
 * Created by samh on 5/23/2016.
 */

class DaftarSoalMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    RecyclerView rv;
    List<ItemDaftarSoal> items;



    public DaftarSoalMenuAdapter(List<ItemDaftarSoal> items) {
        this.items = items;
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
                    Intent i = new Intent(rv.getContext(), DaftarSoalActivity.class)
                            .putExtra("key", items.get(rv.getChildAdapterPosition(v)).get_id());
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
        Log.v("Adapter","onbind...updating an item at index "+position+ "  " +items.get(position));
        vh.QuickView.setText(items.get(position).content);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


}


public class MainMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
                                .putExtra("key", items.get(mRecyclerView.getChildAdapterPosition(v)).getType().name().toLowerCase());

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
                                .putExtra("key", items.get(mRecyclerView.getChildAdapterPosition(v)).getType().name().toLowerCase());

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


