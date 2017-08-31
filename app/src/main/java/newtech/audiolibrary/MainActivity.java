package newtech.audiolibrary;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.media.session.MediaButtonReceiver;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import newtech.audiolibrary.task.DownloadTask;

public class MainActivity extends Activity {

    private MediaPlayer mediaPlayer;

    // declare the dialog as a member field of your activity
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //playTest();
    }

    private void playTest() {
        String myUrl = "http://users.skynet.be/fa046054/home/P22/track37.mp3";
        //String myUrl = "http://mediapolisvod.rai.it/relinker/relinkerServlet.htm?cont=rGKIDnvU46geeqqEEqual";

        // instantiate it within the onCreate method
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setMessage("A message");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

        mProgressDialog.show(this, "dialog title",  "dialog message", true);

        // execute this when the downloader must be fired
        //final DownloadTask downloadTask = new DownloadTask(this, mProgressDialog, null);
        //downloadTask.execute(myUrl, "/sdcard/donwloaded_file.mp3");

        Log.d("MyApp","Start Thread Download");

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //downloadTask.cancel(true);
            }
        });

        try {

            //playFromResource(R.raw.test);

            //playFromUrl(myUrl);

        }catch (Exception e){
            e.printStackTrace();
        }catch (Error e){
            e.printStackTrace();
        }
    }

    public void playFromResource(int res){
        mediaPlayer = MediaPlayer.create(this.getApplicationContext(), R.raw.test);
        mediaPlayer.start();
    }

    public void playFromUrl(String url) throws IOException{
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource(url);
        mediaPlayer.prepare(); // might take long! (for buffering, etc)
        mediaPlayer.start();
    }

    public void onDestroy() {

        mediaPlayer.stop();
        super.onDestroy();

    }
}

