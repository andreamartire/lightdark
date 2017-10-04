package newtech.audiolibrary.task;

/**
 * Created by MartireAn on 24/09/2017.
 */

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import newtech.audiolibrary.utils.MyFileUtils;

public class SimpleDownloadTask extends AsyncTask<String, Integer, String> {

    URL remoteFileURL;
    String localFilePath;

    public SimpleDownloadTask(URL remoteFileURL, String localFilePath){
        super();
        this.remoteFileURL = remoteFileURL;
        this.localFilePath = localFilePath;
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

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
