package techbrain.libro_parlante1.task;

/**
 * Created by MartireAn on 24/09/2017.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import techbrain.libro_parlante1.utils.ImageUtils;
import techbrain.libro_parlante1.utils.MyFileUtils;

public class SimpleDownloadTask extends AsyncTask<String, Integer, String> {

    Context context;
    URL remoteFileURL;
    String localFilePath;
    Callable<Integer> callback;

    public SimpleDownloadTask(Context context, URL remoteFileURL, String localFilePath, Callable<Integer> callback){
        super();
        this.context = context;
        this.remoteFileURL = remoteFileURL;
        this.localFilePath = localFilePath;
        this.callback = callback;
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

            //scale image for save space
            byte[] b = baf.toByteArray();
            Drawable image = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(b, 0, b.length));
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Integer realWidth = ImageUtils.getRealWidthSize(wm);

            final int xSize = realWidth;
            final int hSize = xSize*3/5;

            image = ImageUtils.scaleImage(context, image, xSize, hSize);

            Bitmap bitmap = ((BitmapDrawable)image).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bitmapdata = stream.toByteArray();

            /* Convert the Bytes read to a String. */
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
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
