package newtech.audiolibrary;

/**
 * Created by andrea on 06/09/17.
 */

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import newtech.audiolibrary.bean.Chapter;

public class ChapterPlayer extends Activity {

    public static String CHAPTER = "CHAPTER";

    static MediaPlayer mediaPlayer;

    static TextView currentDurationView;
    static TextView totalDurationView;

    static Button backwardButton10;
    static Button playPauseButton;
    static Button forwardButton10;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_chapter);

        Chapter currentChapter = (Chapter) getIntent().getSerializableExtra(CHAPTER);

        totalDurationView = (TextView) findViewById(R.id.playChapter_totalDuration);
        currentDurationView = (TextView) findViewById(R.id.playChapter_currentDuration);

        playPauseButton = (Button) findViewById(R.id.playPauseButton);
        playPauseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    playPauseButton.setText("|>");
                }else{
                    mediaPlayer.start();
                    playPauseButton.setText("||");
                    updatePlayer();
                }
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

        try {
            //play local resource
            String localFilePath = currentChapter.getLocalFilePath();
            mediaPlayer = MediaPlayer.create(this, Uri.parse(localFilePath));
            mediaPlayer.setLooping(false);
            mediaPlayer.start();

            //init title label
            final TextView title = (TextView) findViewById(R.id.playChapter_title);
            title.setText(currentChapter.getTitle());

            updatePlayer();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    public void updatePlayer(){
        totalDurationView.setText(formatDuration(mediaPlayer.getDuration()));
        currentDurationView.setText(formatDuration(mediaPlayer.getCurrentPosition()));
        currentDurationView.post(new Runnable() {
            public void run() {
                if (mediaPlayer.isPlaying()) {
                    updatePlayer();
                    currentDurationView.postDelayed(this, 1000);
                }else {
                    currentDurationView.removeCallbacks(this);
                }
            }
        });;
    }

    public static String formatDuration(int duration) {
        return String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        );
    }
}