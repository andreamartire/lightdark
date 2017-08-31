package newtech.audiolibrary.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import newtech.audiolibrary.bean.Chapter;
import newtech.audiolibrary.utils.MyFileUtils;

/**
 * Created by andrea on 17/05/17.
 */

// usually, subclasses of AsyncTask are declared inside the activity class.
// that way, you can easily modify the UI thread from here
public class DownloadTask extends AsyncTask<String, Integer, String> {

    private Context context;
    private PowerManager.WakeLock mWakeLock;
    private ProgressDialog mProgressDialog;
    private View view;

    private Chapter chapter;

    public DownloadTask(Context context, ProgressDialog mProgressDialog, Chapter chapter, View view) {
        this.context = context;
        this.mProgressDialog = mProgressDialog;
        this.chapter = chapter;
        this.view = view;
    }

    @Override
    protected String doInBackground(String ... sUrl) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;

        String providerDir = null;
        String bookDir = null;
        String fileName = null;

        try {
            URL url = new URL(chapter.getUrl());

            providerDir = chapter.getProviderDir();
            bookDir = chapter.getBookDir();
            fileName = chapter.getFileName();

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();

            MyFileUtils.mkDir(providerDir);
            MyFileUtils.mkDir(bookDir);

            //create it. avoid exception no such file
            MyFileUtils.touchFile(bookDir + File.separator + fileName);

            Log.d("MyApp","Saving file to: " + bookDir + File.separator + fileName);
            output = new FileOutputStream(bookDir + File.separator + fileName);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {

                Log.d("MyApp","GeT Data");

                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }

            mProgressDialog.dismiss();
            
        } catch (Exception e) {

            e.printStackTrace();

            // TODO
            File[] filesList = new File(bookDir).listFiles();
            if(filesList != null){
                for(File f : filesList){
                    long size = f.length();
                    System.out.println("File: " + f.getAbsolutePath());
                    System.out.println("Size: " + size);
                }
            }

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
    protected void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
        mProgressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        mWakeLock.release();
        mProgressDialog.dismiss();
        if (result != null)
            Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
        //view.getParent().refreshDrawableState();
        //view.refreshDrawableState();

    }
}