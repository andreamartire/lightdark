package techbrain.libro_parlante.task;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import techbrain.libro_parlante.R;
import techbrain.libro_parlante.bean.Chapter;
import techbrain.libro_parlante.utils.MyFileUtils;

/**
 * Created by andrea on 17/05/17.
 */

// usually, subclasses of AsyncTask are declared inside the activity class.
// that way, you can easily modify the UI thread from here
public class DownloadTask extends AsyncTask<String, Integer, String> {

    private String _TMP = "_TMP";
    private int MAX_RETRY = 10;

    private Context context;
    private PowerManager.WakeLock mWakeLock;
    private ProgressBar progressBar;
    private View view;

    private Chapter chapter;

    public DownloadTask(Context context, ProgressBar progressBar, Chapter chapter, View view) {
        this.context = context;
        this.progressBar = progressBar;
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

        long total = 0;
        int fileLength = 0;

        boolean fileDownloaded = false;
        int retryNum = 0;
        Exception exception = null;

        providerDir = chapter.getProviderDir();
        bookDir = chapter.getBookDir();
        fileName = chapter.getFileName();

        //tmp file
        String filePathTmp = bookDir + File.separator + fileName + _TMP;

        //delete if exists old tmp file
        MyFileUtils.deleteFileIfExists(filePathTmp);

        while (!fileDownloaded && retryNum < MAX_RETRY){
            try {

                connection = (HttpURLConnection) new URL(chapter.getUrl()).openConnection();
                connection.setRequestProperty("User-Agent","Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36");
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();

                MyFileUtils.mkDir(providerDir);
                MyFileUtils.mkDir(bookDir);

                MyFileUtils.deleteFileIfExists(filePathTmp);
                MyFileUtils.touchFile(filePathTmp);//create it. avoid exception no such file

                Log.d("MyApp","Saving file to: " + filePathTmp);
                output = new FileOutputStream(filePathTmp);

                byte data[] = new byte[4096];

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

                progressBar.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });

            } catch (Exception e) {
                exception = e;
                e.printStackTrace();

                MyFileUtils.listDirFiles(bookDir);
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

            if(total > 0 && fileLength > 0){
                fileDownloaded = total >= fileLength;
            }

            if(!fileDownloaded){
                if(retryNum < MAX_RETRY) {
                    retryNum++;
                }else{
                    //delete file partially downloaded
                    MyFileUtils.deleteFileIfExists(filePathTmp);
                }
            }else{
                //downloaded
                exception = null;

                MyFileUtils.renameFile(filePathTmp, bookDir + File.separator + fileName);
            }
        }

        chapter.setDownloading(false);

        String errorMessage = exception != null ? exception.getMessage() : "";

        return fileDownloaded ? null : errorMessage;
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
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        progressBar.setProgress(progress[0]);

        Log.d("MyApp","Progress: " + progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        mWakeLock.release();

        progressBar.post(new Runnable() {
            @Override
            public void run() {
            progressBar.setVisibility(View.GONE);
            }
        });

        if (result != null){
            Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
        }

        //force repaint chapter list view
        ListView chapterListView = (ListView) view.getRootView().findViewById(R.id.chapters_listview);
        if(chapterListView != null){
            final ArrayAdapter adapter = ((ArrayAdapter) chapterListView.getAdapter());
            adapter.notifyDataSetChanged();
        }
    }
}