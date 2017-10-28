package newtech.audiolibrary.task;

/**
 * Created by MartireAn on 24/09/2017.
 */

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;

import newtech.audiolibrary.utils.MyFileUtils;

public class SimpleDownloadTask extends AsyncTask<String, Integer, String> {

    URL remoteFileURL;
    String localFilePath;
    Callable<Integer> callback;

    public SimpleDownloadTask(URL remoteFileURL, String localFilePath, Callable<Integer> calback){
        super();
        this.remoteFileURL = remoteFileURL;
        this.localFilePath = localFilePath;
        this.callback = calback;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            File file = new File(localFilePath);

            String meta = file.getParent();
            //make parent folder if not exists
            MyFileUtils.mkDir(meta);

            long startTime = System.currentTimeMillis();
            Log.d("ImageManager", "download beginning");
            Log.d("ImageManager", "download url:" + remoteFileURL);
            Log.d("ImageManager", "downloaded file path:" + localFilePath);

            /* Open a connection to that URL. */
            URLConnection ucon = remoteFileURL.openConnection();

            /*
             * Define InputStreams to read from the URLConnection.
             */
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            /*
             * Read bytes to the Buffer until there is nothing more to read(-1).
             */
            ByteArrayOutputStream baf = new ByteArrayOutputStream();
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.write((byte) current);
            }

            /* Convert the Bytes read to a String. */
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baf.toByteArray());
            fos.close();

            if(callback != null){
                Handler mHandler = new Handler(Looper.getMainLooper());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                    //arrayAdapter.notifyDataSetChanged();
                    try {
                        callback.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    }
                });
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }
}
