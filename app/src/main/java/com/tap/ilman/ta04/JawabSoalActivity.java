package com.tap.ilman.ta04;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tap.ilman.ta04.util.Analyze;
import com.tap.ilman.ta04.util.PExpr;
import com.tap.ilman.ta04.util.Param;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JawabSoalActivity extends AppCompatActivity implements DownloadAndRead, clickedPad, ParseProcess {

    private ProgressBar pb;
    private DetailSoal soal;
    private RecyclerView mrv;
    private PadAdapter padAdapter;
    private EditText answerBox;
    private TextView soalbox;
    private String fileLocation;
    private PExpr[] keys;


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

        answerBox = (EditText) findViewById(R.id.jawab);
        soalbox = (TextView) findViewById(R.id.soal);
        pb = (ProgressBar) findViewById(R.id.debug_check_JawabSoal);

        padAdapter = new PadAdapter(generatePad(4), this, getApplicationContext());

        mrv = (RecyclerView) findViewById(R.id.pads);
        mrv.setAdapter(padAdapter);
        mrv.setLayoutManager(new GridLayoutManager(this, 4));

        fileLocation = getApplicationContext().getFilesDir() + "/soal/" + getIntent().getStringExtra("key");
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        soalbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Analyze.checkSemantic(new Param().setInput(answerBox.getText().toString()));
            }
        });

        initialState();
        determineFiletoBeRead();
        //showUserSettings();
    }

    private void initialState() {

        soalbox.setText("");
        answerBox.setEnabled(false);
        pb.setMax(100);
        mrv.setEnabled(false);
        answerBox.setHint(getString(R.string.isi_jawabmu));

    }


    private void determineFiletoBeRead() {
        try {
            if (!AppUtils.checkSoalFile(getApplicationContext(), getIntent().getStringExtra("key"))) {
                tryToDownloadFile();
            } else doneDownoading();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tryToDownloadFile() {
        String server = new StringBuilder(
                PreferenceManager.getDefaultSharedPreferences(this)
                        .getString("prefServer", "http://192.168.43.50:8080/api")
        ).append("/soal/").append(getIntent().getStringExtra("key")).toString();
        Log.v("TEST", "download file if it isnt there: " + server);
        new DownloadFileFromURL(this, getApplicationContext(), pb, "/menu/main").execute(server);
    }


//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            mrv.setLayoutManager(new GridLayoutManager(this,6));
//            mrv.invalidate();
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//            mrv.setLayoutManager(new GridLayoutManager(this,4));
//            mrv.invalidate();
//        }
//    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        startActivity(new Intent(this, DaftarSoalActivity.class).putExtra("key", getIntent().getStringExtra("back")));
    }

    @Override
    public void doneDownoading() {
        DetailSoal[] a = new DetailSoal[0];
        try {
            Log.v("TEST", "JawabSoal: doneDownloading: try to construct DetailSoal POJO using jackson, file Location is " + fileLocation);
            a = AppUtils.jacksonObjectMapper.readValue(new File(fileLocation), DetailSoal[].class);
        } catch (IOException e) {
            Log.v("TEST", "CATCH BLOCK:  Detail is empty, seems like the download or file reading is failed ");
            e.printStackTrace();
        } finally {
            if (a[0].s.isEmpty() || a[0].k[0].isEmpty()) {
                Log.v("TEST", "FINAL BLOCK:  Detail (tak ada soal ataupun kunci) is empty, seems like the download or file writing is failed, reverting to initial state ");
                initialState();
            } else {
                soal = a[0];
                //Log.v("PARSE",soal.toString());
                soalbox.setText(soal.s);
                // System.out.println("JawabSoalActivity.doneDownoading: soal.k: "+Arrays.toString(soal.k));
                Param[] p = Param.getKeyAsParameterArray(soal.k);
                //System.out.println(Arrays.toString(p));
                new ParseKeys(keys, pb, this).execute(p);
            }
        }
    }


    @Override
    public void pushedPad(PadItem i) {
        switch (i) {

            case BACKSPACE:
                if (answerBox.getText().length() == 0) break;
                if (0 == answerBox.getSelectionEnd()) {
                    Log.v("TEST", "no op because selection at index 0 equals selectionEnd " + answerBox.getSelectionEnd());
                    break;
                } else {
                    Log.v("TEST", "will be deleting at: " + answerBox.getSelectionStart() + " to " + answerBox.getSelectionEnd());
                    answerBox.getText().delete(answerBox.getSelectionStart() - 1, answerBox.getSelectionStart());
                }
                break;
            case DEL:
                if (answerBox.getText().length() == 0) break;
                if (answerBox.getText().length() == answerBox.getSelectionEnd()) {
                    Log.v("TEST", "no op, because textlength "
                            + answerBox.getText().length() + " equals selectionEnd " + answerBox.getSelectionEnd());
                    break;
                } else {
                    Log.v("TEST", "will be deleting at: " + answerBox.getSelectionStart() + " to " + answerBox.getSelectionEnd());
                    answerBox.getText().delete(answerBox.getSelectionStart(), answerBox.getSelectionStart() + 1);
                }
                break;
            case TOLEFT:
                if (answerBox.getText().length() == 0) break;
                if (answerBox.getSelectionStart() == 0) break;
                if (answerBox.getSelectionStart() > 0)
                    answerBox.setSelection(answerBox.getSelectionStart() - 1);
                break;
            case TORIGHT:
                if (answerBox.getText().length() == 0) break;
                if (answerBox.getText().length() > answerBox.getSelectionEnd())
                    answerBox.setSelection(answerBox.getSelectionStart() + 1);
                break;
            case CLEAR:
                if (answerBox.getText().length() == 0) break;
                answerBox.setText("");
                break;
            default:
                answerBox.getText().insert(answerBox.getSelectionStart(), i.getPrintedVal());
                break;
        }
        Log.v("TEST", "called on ui, pushedpad parameter is: " + i);

    }

    static List<PadItem> generatePad(int columns) {
        List<PadItem> a = new ArrayList<>();
        if (columns == 6) {
            for (PadItem x : PadItem.values()) {
                a.add(x);
            }
        } else {
            for (PadItem x : PadItem.values()) {
                a.add(x);
            }
        }

        return a;
    }

    @Override
    public void doneParsing() {
        Log.v("TEST", "Parsing Seem Success , enabling necessary ui");
        answerBox.setEnabled(true);
        mrv.setEnabled(true);
    }
}


class padviewHolder extends RecyclerView.ViewHolder {
    Button btn;

    public padviewHolder(View itemView) {
        super(itemView);
        this.btn = (Button) itemView.findViewById(R.id.anumpad);
    }
}

class DetailSoal {
    //POJO
    String s;
    String[] c; //categories
    String[] k; //keys
    boolean spd = false; // special denumerator on factor term
    boolean igm = false; // ignore metrics / types in operand
    boolean rm = false; // lock result metric


    public DetailSoal() {
    }

    @Override
    public String toString() {
        return new StringBuilder("isi soal: ").append(s).append("\nkategori tags:").append(Arrays.toString(c))
                .append("\nkunci: ").append(Arrays.toString(k)).append(' ')
                .append(spd).append(' ').append(igm).append(' ').append(rm).toString();

    }
}

interface clickedPad {
    void pushedPad(PadItem i);

}

class PadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    float xy;
    RecyclerView rv;
    List<PadItem> padData;
    Context ctx;
    clickedPad caller;

    public PadAdapter(List<PadItem> padData, clickedPad caller, Context ctx) {
        this.ctx = ctx;
        this.padData = padData;
        this.caller = caller;
        this.xy = ctx.getResources().getDisplayMetrics().scaledDensity;
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.rv = recyclerView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        View v1 = inflater.inflate(R.layout.numpad, viewGroup, false);

        viewHolder = new padviewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        padviewHolder vh = (padviewHolder) holder;
        final float fc = vh.btn.getTextSize() / xy;
        final PadItem pd = padData.get(position);
        vh.btn.setText(pd.getShownVal());
        vh.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("TEST", "pad is sending signal: " + pd.name());
                caller.pushedPad(pd);
            }
        });
        switch (pd) {
            case BACKSPACE:
                System.out.println("resizing this BACKSPACE");
                vh.btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, fc * 0.4f);
            case DEL:
                System.out.println("resizing this DEL");
                vh.btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, fc * 0.65f);
            case TOLEFT:
            case TORIGHT:
            case CLEAR:
                System.out.println("coloring " + pd + " with primary very light");
                vh.btn.setBackgroundColor(Color.parseColor(ctx.getString(R.string.colorPrimaryVeryLight)));
                break;

            case BUKAKURUNG:
            case TUTUPKURUNG:
            case SAMADENGAN:
            case TAMBAH:
            case KURANG:
            case BAGI:
            case PER:
            case KALI:
            case TITIK:
            case KOMA:
            case AKAR:
            case DERAJATC:
            case DERAJATF:
            case DERAJATK:
            case DERAJATR:
                System.out.println("coloring " + pd + " with accent light");
                vh.btn.setBackgroundColor(Color.parseColor(ctx.getString(R.string.colorAccentLight)));
                break;
            case PANGKATSATU:
            case PANGKATDUA:
            case PANGKATTIGA:
            case PANGKATEMPAT:
            case PANGKATLIMA:
            case PANGKATENAM:
            case PANGKATTUJUH:
            case PANGKATDELAPAN:
            case PANGKATSEMBILAN:
            case PANGKATNOL:
                System.out.println("coloring " + pd + " with primary shade");
                vh.btn.setBackgroundColor(Color.parseColor(ctx.getString(R.string.colorPrimaryShade)));
                break;
            case SATU:
            case DUA:
            case TIGA:
            case EMPAT:
            case LIMA:
            case ENAM:
            case TUJUH:
            case DELAPAN:
            case SEMBILAN:
            case KOSONG:
                System.out.println("coloring " + pd + " with primary light");
                vh.btn.setBackgroundColor(Color.parseColor(ctx.getString(R.string.colorPrimaryLight)));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return padData.size();
    }
}


enum PadItem {
    TOLEFT("\u21e6", ""),
    TORIGHT("\u21e8", ""),
    BACKSPACE("backspace", ""),
    DEL("del", ""),

    SATU("1", "1"),
    DUA("2", "2"),
    TIGA("3", "3"),
    EMPAT("4", "4"),
    LIMA("5", "5"),
    ENAM("6", "6"),
    TUJUH("7", "7"),
    DELAPAN("8", "8"),
    SEMBILAN("9", "9"),
    KOSONG("0", "0"),

    BUKAKURUNG("(", "("),
    TUTUPKURUNG(")", ")"),
    SAMADENGAN("=", "="),
    TAMBAH("+", "+"),
    KURANG("-", "-"),
    BAGI("\u00f7", "÷"),
    PER("/", "/"),
    KALI("\u00d7", "\u00d7"),
    TITIK(".", "."),
    KOMA(",", ","),
    AKAR("\u221a", "\u221a"),

    DERAJATC("...\u00b0C", "\u00b0C"),
    DERAJATF("...\u00b0F", "\u00b0F"),
    DERAJATK("...\u00b0K", "\u00b0K"),
    DERAJATR("...\u00b0R", "\u00b0R"),

    PANGKATSATU("...\u00b9", "\u00b9"),
    PANGKATDUA("...\u00b2", "\u00b2"),
    PANGKATTIGA("...\u00b3", "\u00b3"),
    PANGKATEMPAT("...\u2074", "\u2074"),
    PANGKATLIMA("...\u2075", "\u2075"),
    PANGKATENAM("...\u2076", "\u2076"),
    PANGKATTUJUH("...\u2077", "\u2077"),
    PANGKATDELAPAN("...\u2078", "\u2078"),
    PANGKATSEMBILAN("...\u2079", "\u2079"),
    PANGKATNOL("...\u2070", "\u2070"),


    CLEAR("C", "");


//    static String specialpads[] = {
//            "\u21e6", "\u21e8", "\u3008", "\u3009", "C",
//            "1", "2", "3", "4", "5",
//            "6", "7", "8", "9", "0",
//            "+", "-", "\u00f7", "/", "\u00d7",
//            "(", ")", "="
//    };

    String shownVal;
    String printedVal;

    PadItem(String shownVal, String printedVal) {
        this.shownVal = shownVal;
        this.printedVal = printedVal;
    }

    public String getShownVal() {
        return shownVal;
    }

    public void setShownVal(String shownVal) {
        this.shownVal = shownVal;
    }

    public String getPrintedVal() {
        return printedVal;
    }

    public void setPrintedVal(String printedVal) {
        this.printedVal = printedVal;
    }

    @Override
    public String toString() {
        return new StringBuilder('[').append(shownVal).append(';')
                .append(printedVal).append(' ')
                .toString();
    }
}


interface ParseProcess {
    void doneParsing();
}


class ParseKeys extends AsyncTask<Param, Void, PExpr[]> {
    private PExpr[] keys;
    ProgressBar pb;
    ParseProcess caller;

    public ParseKeys(PExpr[] keys, ProgressBar pb, ParseProcess parseProcess) {
        this.keys = keys;
        this.pb = pb;
        this.caller = parseProcess;
    }

    @Override
    protected void onPreExecute() {
        pb.setIndeterminate(true);
        super.onPreExecute();
    }

    @Override
    protected PExpr[] doInBackground(Param... params) {
        boolean error = false;
        try {
            Analyze.getKeys(params);
        } catch (Exception e) {
            e.printStackTrace();
            error = true;
        } finally {
            if (error) {
                System.out.println("PARSING ERROR, Source code may be wrong");
                return null;
            } else
                return keys;
        }
    }

    @Override
    protected void onPostExecute(PExpr[] pExprs) {
        super.onPostExecute(pExprs);
        pb.setIndeterminate(false);
        caller.doneParsing();
    }
}