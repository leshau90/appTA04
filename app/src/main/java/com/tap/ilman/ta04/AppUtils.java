package com.tap.ilman.ta04;

import android.content.Context;
import android.os.AsyncTask;
//import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ProgressBar;


//import java.io.BufferedInputStream;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
//import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
//import java.io.IOException;
import java.io.IOException;
import java.io.InputStream;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;


/**
 * Created by samh on 6/13/2016.
 */
public class AppUtils {


    static ObjectMapper jacksonObjectMapper = new ObjectMapper();

    static {
        jacksonObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jacksonObjectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    }

//    static boolean isValidJSON(final File f) {
//        boolean valid = false;
//        try {
//            final JsonParser parser = jacksonObjectMapper.getFactory().createParser(f);
//            while (parser.nextToken() != null) {
//
//            }
//            valid = true;
//        } catch (Exceptione){
//            e
//        }
//        return valid;
//    }

    static void deleteAllFile(Context ctx) {
        File folder = new File(ctx.getFilesDir() + "/soal");

        if (folder.exists() && folder.isDirectory()) {
            Log.v("TEST", "APPUTIL: deleteallfile, all file in  " + folder.getAbsolutePath() + " will be deleted");

            for (File f : folder.listFiles()) {
                Log.v("TEST", "APPUTIL: deleteallfile, delete:  " + f.getName());
                f.delete();
            }
        }
        folder = new File(ctx.getFilesDir() + "/menu");
        if (folder.exists() && folder.isDirectory()) {
            Log.v("TEST", "APPUTIL: deleteallfile, all file in  " + folder.getAbsolutePath() + " will be deleted");
            for (File f : folder.listFiles()) {
                Log.v("TEST", "APPUTIL: deleteallfile, delete:  " + f.getName());
                f.delete();
            }
        }
    }

    static void checkAndCreateAppDirs(Context ctx) {
        File folder = new File(ctx.getFilesDir() + "/soal");
        boolean success = true;
        if (!folder.exists()) {
            System.out.println("no such dirs.. creating");
            success = folder.mkdir();
        } else
            Log.v("TEST", folder.getName() + " created before: files here  " + Arrays.toString(folder.listFiles()));
        if (success) {
            System.out.println(folder.getAbsolutePath() + " successfully created ...");
        } else {
            System.out.println(folder.getAbsolutePath() + " cant be created..., this is bad");// Do something else on failure
        }
        folder = new File(ctx.getFilesDir() + "/menu");

        if (!folder.exists()) {
            System.out.println("no such dirs.. creating");
            success = folder.mkdir();
        } else
            Log.v("TEST", folder.getName() + " created before: files here  " + Arrays.toString(folder.listFiles()));
        if (success) {
            System.out.println(folder.getAbsolutePath() + " successfully created ...");
        } else {
            System.out.println(folder.getAbsolutePath() + " cant be created..., this is bad");// Do something else on failure
        }
    }


    static boolean checkMenuFile(Context ctx) throws Exception {
        File file = new File(ctx.getFilesDir() + "/menu/main");
        if (file.exists()) {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            Log.v("TEST", "APPUTIL: checkMenuFile, file " + file.getAbsolutePath().toString() + " is there");
            return true;
        } else {
            Log.v("TEST", "APPUTIL: checkMenuFile, no such file " + file.getAbsolutePath().toString());
            return false;
        }
    }

    static boolean checkFile(String s) throws Exception {
        File file = new File(s);
        if (file.exists()) {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            Log.v("TEST", "APPUTIL: checkMenuFile, file " + file.getAbsolutePath().toString() + " is there");
            return true;
        } else {
            Log.v("TEST", "APPUTIL: checkMenuFile, no such file " + file.getAbsolutePath().toString());
            return false;
        }
    }

    static boolean checkDaftarSoalFile(Context ctx, String key) throws Exception {
        File file = new File(ctx.getFilesDir() + "/menu/" + key);
        if (file.exists()) {
            Log.v("TEST", "APPUTIL: checkDaftarSoalFile, file " + file.getAbsolutePath().toString() + " is there");
            return true;
        } else {
            Log.v("TEST", "APPUTIL: checkDaftarSoalFile, no such file " + file.getAbsolutePath().toString());
            return false;
        }
    }

    static boolean checkSoalFile(Context ctx, String sid) throws Exception {
        File file = new File(ctx.getFilesDir() + "/soal/" + sid);
        if (file.exists()) {
            Log.v("TEST", "APPUTIL: checkSoalFile, file " + file.getAbsolutePath().toString() + " is there");
            return true;
        } else {
            Log.v("TEST", "APPUTIL: checkSoalFile, no such file " + file.getAbsolutePath().toString());
            return false;
        }
    }

    public static float pixelsToSp(Context context, float px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px/scaledDensity;
    }
}

interface DownloadAndRead {

    void doneDownoading();

}


class DownloadFileFromURL extends AsyncTask<String, Integer, String> {
    Context ctx;
    ProgressBar status;
    String fileName;
    DownloadAndRead caller;

    public DownloadFileFromURL(DownloadAndRead caller, Context ctx, ProgressBar tv, String fileName) {
        this.ctx = ctx;
        this.status = tv;
        this.fileName = fileName;
        this.caller = caller;

        //new File(ctx.getFilesDir(),fileName);
    }

    /**
     * Before starting background thread
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.v("downstream", "begin download.. " + ctx.getFilesDir().toString() + fileName);
        status.setProgress(0);

    }


    @Override
    protected String doInBackground(String... f_url) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(f_url[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();


            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.v("downstream", "HTTP server DOWN");
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }


            Log.v("downstream", "HTTP IS OK server is " + url.toString());

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            //deleting old one first
            new File(ctx.getFilesDir().toString() + fileName).delete();
            // download the file

            input = connection.getInputStream();
            output = new FileOutputStream(ctx.getFilesDir().toString() + fileName);
            Log.v("downstream", "opening stream to file : " + ctx.getFilesDir().toString() + fileName);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (fileLength / total) * 100);
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {

            }
            if (connection != null)
                connection.disconnect();
        }
        return null;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        status.setProgress(values[0]);
        Log.v("downstream", "onProgressUpdate...." + values[0] + "bytes");
    }

    /**
     * After completing background task
     **/
    @Override
    protected void onPostExecute(String file_url) {
        Log.v("downstream", "onPostExecute....Downloaded");
        status.setProgress(100);
        caller.doneDownoading();
    }
}

interface viewQuickFile {
    void partiallyDone(int i);
}

class GetQuickViewOrDownloadFromURL extends AsyncTask<Void, Integer, Void> {
    Context ctx;
    int maxQuickViewLength;
    ProgressBar status;
    StringBuilder fileName;
    String res = "";
    List<ItemDaftarSoal> dsma;
    int ids;
    viewQuickFile caller;
    int strl, strsl;
    StringBuilder server;
    StringBuilder tempKey = new StringBuilder();
    InputStream input = null;
    OutputStream output = null;
    HttpURLConnection connection = null;
    int currentI;

    public GetQuickViewOrDownloadFromURL(Context ctx, ProgressBar Status, List<ItemDaftarSoal> dsma, String server, viewQuickFile caller, int maxQuickViewLength) {
        this.maxQuickViewLength = maxQuickViewLength;
        this.ctx = ctx;
        this.status = Status;
        this.dsma = dsma;
        this.caller = caller;
        this.server = new StringBuilder(server).append("/soal/");
        fileName = new StringBuilder(ctx.getFilesDir().toString()).append("/soal/");
        strl = fileName.length();
        strsl = this.server.length();
        Log.v("downstream", "consturctor of getquickview server is: " + server.toString() + "file is " + fileName.toString());
    }


    /**
     * Before starting background thread
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.v("downstream", "begin download.. " + ctx.getFilesDir().toString() + fileName);
        status.setProgress(0);
    }

    private void downloadOneIfNone() throws Exception {

        if (!AppUtils.checkFile(fileName.toString())) {
            URL url = new URL(server.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return;
            } else {
                int fileLength = connection.getContentLength();
                //deleting old one first
                new File(fileName.toString()).delete();
                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(fileName.toString());
                Log.v("downstream", "opening stream to file : " + fileName);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total / fileLength) * 100, 0);
                    output.write(data, 0, count);
                }
            }
        }
    }

    private void readOne(int ids) {
        StringBuilder sb = new StringBuilder();
        sb.setLength(0);
        try {
            if (AppUtils.checkFile(fileName.toString())) {
                Log.v("downstream", " parsing json, the key is 's' only, file: " + fileName.toString());

                sb.append(AppUtils.jacksonObjectMapper.readValue(new File(fileName.toString()), JsonNode.class)
                        .get(0).get("s").textValue());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Log.v("downstream", " Final : READDATA..jackson.. readOne " + sb.toString() + " length " + sb.length());
            if (sb.length() == 0) {
                Log.v("downstream", " Final : READDATA..set content to NoData");
                dsma.get(ids).setContent("No Preview Data");
            } else if (sb.length() <= maxQuickViewLength) {
                Log.v("downstream", " Final : READDATA..set content to " + sb.toString());
                dsma.get(ids).setContent(sb.toString());
            } else {
                Log.v("downstream", " Final : READDATA..set content to " + sb.toString());
                sb.setLength(maxQuickViewLength);
                sb.append("...");
                dsma.get(ids).setContent(sb.toString());
            }
        }
    }

    private void downloadAll() {

        for (int i = 0; i < dsma.size(); i++) {
            ItemDaftarSoal a = dsma.get(i);
            currentI = i;
            fileName.setLength(strl);
            fileName.append(a._id);
            server.setLength(strsl);
            server.append(a._id);
            Log.v("downstream", "downloadAll(): where i " + i + " server: " + server + " file to be written: " + fileName);

            try {
                downloadOneIfNone();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                    ignored.printStackTrace();
                }
                if (connection != null)
                    connection.disconnect();
            }
            readOne(i);
            publishProgress(i, 1);
        }
    }


    @Override
    protected Void doInBackground(Void... params) {
        Log.v("downstream", "do in background ");
        downloadAll();
        return null;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        System.out.println("onProgressUpdate... " + Arrays.toString(values));
        super.onProgressUpdate(values);
        switch (values[1]) {
            case 0:
                status.setProgress(values[0]);

                Log.v("downstream", "onProgressUpdate... setProggresbar" + values[0]);
                break;
            case 1:
                caller.partiallyDone(values[0]);
                Log.v("downstream", "onProgressUpdate... notifyitemchanged " + values[0] + " content is " + dsma.get(values[0]));
                break;
            default:
                Log.v("downstream", "onProgressUpdate... this shouldn't happen... no integer code");
                break;
        }


    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //dsma.notifyItemChanged(ids);
        status.setProgress(100);
        Log.v("downstream", "onPostExecute....the content should be ...:\n" + dsma.get(ids).content);
        Log.v("downstream", "res should be " + res);
    }
}

