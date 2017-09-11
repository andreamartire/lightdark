package newtech.audiolibrary.adapters;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import newtech.audiolibrary.R;
import newtech.audiolibrary.bean.Chapter;

/**
 * Created by MartireAn on 10/09/2017.
 */

public class TestThread extends AsyncTask<String, Integer, String> {

    static MediaPlayer mediaPlayer;
    Activity currentContext;
    Chapter currentChapter;

    public TestThread(Activity currentContext, Chapter currentChapter) {
        this.currentContext = currentContext;
        this.currentChapter = currentChapter;
    }

    public void updatePlayer(){

        TextView totalDurationView = (TextView) currentContext.findViewById(R.id.playChapter_totalDuration);
        totalDurationView.setText(formatDuration(mediaPlayer.getDuration()));

        final TextView currentDurationView = (TextView) currentContext.findViewById(R.id.playChapter_currentDuration);

        currentContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                currentDurationView.setText(formatDuration(mediaPlayer.getCurrentPosition()));
                currentDurationView.post(new Runnable() {
                    public void run() {
                        if (mediaPlayer.isPlaying()) {
                            currentDurationView.postDelayed(this, 1000);
                            currentDurationView.setText(formatDuration(mediaPlayer.getCurrentPosition()));
                        }else {
                            currentDurationView.removeCallbacks(this);
                        }
                    }
                });
            }
        });
    }

    public static String formatDuration(int duration) {
        return String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        );
    }

    public void toogle() {

        final TextView playPauseButton = (Button) currentContext.findViewById(R.id.playPauseButton);
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            playPauseButton.setText("|>");
        }else{
            mediaPlayer.start();
            playPauseButton.setText("||");
            updatePlayer();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        //play local resource
        String localFilePath = currentChapter.getLocalFilePath();
        mediaPlayer = new MediaPlayer();
        try {
            Log.d("myApp", "file size: " + new File(localFilePath).getTotalSpace());
            System.out.println("file size: " + new File(localFilePath).getTotalSpace());
            mediaPlayer.setDataSource(localFilePath);

            mediaPlayer.setLooping(false);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });

            //init title label
            final TextView title = (TextView) currentContext.findViewById(R.id.playChapter_title);
            currentContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    title.setText(currentChapter.getTitle());
                }
            });


            updatePlayer();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void stop() {
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}
