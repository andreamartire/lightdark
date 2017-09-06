package newtech.audiolibrary;

/**
 * Created by andrea on 06/09/17.
 */

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import newtech.audiolibrary.bean.Chapter;

public class ChapterPlayer extends Activity {

    public static String CHAPTER = "CHAPTER";

    public static MediaPlayer mediaPlayer;
    public static TextView currentDurationView;

    ScheduledExecutorService mScheduledExecutorService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_chapter);

        Chapter currentChapter = (Chapter) getIntent().getSerializableExtra(CHAPTER);

        TextView title = (TextView) findViewById(R.id.playChapter_title);
        TextView totalDuration = (TextView) findViewById(R.id.playChapter_totalDuration);

        currentDurationView = (TextView) findViewById(R.id.playChapter_currentDuration);

        try {
            //play local resource
            String localFilePath = currentChapter.getLocalFilePath();
            mediaPlayer = MediaPlayer.create(this, Uri.parse(localFilePath));
            mediaPlayer.setLooping(false);
            mediaPlayer.start();

            //init elements
            title.setText(currentChapter.getTitle());
            totalDuration.setText(formatDuration(mediaPlayer.getDuration()));

            currentDurationView.setText(formatDuration(mediaPlayer.getCurrentPosition()));
            currentDurationView.post(updatePlayerCurrentDuration);

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

    public static Runnable updatePlayerCurrentDuration = new Runnable() {
        public void run() {
            int currentDuration;
            if (mediaPlayer.isPlaying()) {
                currentDuration = mediaPlayer.getCurrentPosition();
                updatePlayer(currentDuration);
                currentDurationView.postDelayed(this, 1000);
            }else {
                currentDurationView.removeCallbacks(this);
            }
        }
    };

    public static void updatePlayer(int currentDuration){
        currentDurationView.setText(formatDuration(currentDuration));
    }

    public static String formatDuration(int duration) {
        return String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        );
    }
}