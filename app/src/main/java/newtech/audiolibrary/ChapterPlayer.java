package newtech.audiolibrary;

/**
 * Created by andrea on 06/09/17.
 */

import android.app.Activity;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import newtech.audiolibrary.adapters.TestThread;
import newtech.audiolibrary.bean.Chapter;

public class ChapterPlayer extends Activity {

    public static String CHAPTER = "CHAPTER";

    static Button backwardButton10;
    static Button playPauseButton;
    static Button forwardButton10;

    static TestThread tt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_chapter);

        final Chapter currentChapter = (Chapter) getIntent().getSerializableExtra(CHAPTER);
        final Activity currentContext = this;

        tt = new TestThread(currentContext, currentChapter);

        playPauseButton = (Button) findViewById(R.id.playPauseButton);
        playPauseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v) {
                tt.toogle();
            }
        });

        backwardButton10 = (Button) findViewById(R.id.backwardButton10);
        backwardButton10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*int currPos = mediaPlayer.getCurrentPosition();
                // + 10 sec
                int newPos = currPos + 10*1000;
                if(newPos > mediaPlayer.getDuration()){
                    // last second
                    newPos = mediaPlayer.getDuration() - 1000;
                }
                mediaPlayer.pause();
                mediaPlayer.seekTo(newPos);
                updatePlayer();
                mediaPlayer.start();*/
            }
        });

        forwardButton10 = (Button) findViewById(R.id.backwardButton10);
        forwardButton10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*int currPos = mediaPlayer.getCurrentPosition();
                // - 10 sec
                int newPos = currPos - 10*1000;
                if(newPos < 0){
                    newPos = 0;
                }
                mediaPlayer.pause();
                mediaPlayer.seekTo(newPos);
                updatePlayer();
                mediaPlayer.start();*/
            }
        });

       tt.execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        tt.stop();
    }
}