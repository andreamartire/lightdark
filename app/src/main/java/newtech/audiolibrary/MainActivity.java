package newtech.audiolibrary;

import android.app.Activity;
import android.app.ProgressDialog;
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

import java.io.IOException;

public class MainActivity extends Activity {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {

            playFromResource(R.raw.test);

            playFromUrl("http://users.skynet.be/fa046054/home/P22/track37.mp3");

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

