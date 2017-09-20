package newtech.audiolibrary.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import newtech.audiolibrary.R;
import newtech.audiolibrary.bean.Chapter;
import newtech.audiolibrary.utils.MyFileUtils;

/**
 * Created by MartireAn on 10/09/2017.
 */

public class PlayThread extends AsyncTask<String, Integer, String> {

    public static String PLAYER_STATE_FILE = "player_state.json";
    public static String METADATA = ".metadata";

    static MediaPlayer mediaPlayer;
    Activity currentContext;
    Chapter currentChapter;

    public PlayThread(Activity currentContext, Chapter currentChapter) {
        this.currentContext = currentContext;
        this.currentChapter = currentChapter;
    }

    public void updatePlayer(){

        int currentDuration = mediaPlayer.getCurrentPosition();
        currentChapter.setCurrentDuration(currentDuration);
        int totalDuration = mediaPlayer.getDuration();
        currentChapter.setTotalDuration(totalDuration);

        TextView totalDurationView = (TextView) currentContext.findViewById(R.id.playChapter_totalDuration);
        totalDurationView.setText(formatDuration(currentDuration));

        final TextView currentDurationView = (TextView) currentContext.findViewById(R.id.playChapter_currentDuration);

        currentContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                currentDurationView.setText(formatDuration(mediaPlayer.getCurrentPosition()));
                currentDurationView.post(new Runnable() {
                    public void run() {
                        try {
                            if (mediaPlayer.isPlaying()) {
                                currentDurationView.postDelayed(this, 1000);
                                currentDurationView.setText(formatDuration(mediaPlayer.getCurrentPosition()));
                                savePlayerState(currentChapter);
                            } else {
                                currentDurationView.removeCallbacks(this);
                            }
                        } catch (java.lang.IllegalStateException|java.io.IOException e){
                            //nothing
                            e.printStackTrace();
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

    public void toggle() {
        final ImageButton playPauseButton = (ImageButton) currentContext.findViewById(R.id.playPauseButton);
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            playPauseButton.setImageResource(R.drawable.ic_play_arrow_black_72dp);
        }else{
            mediaPlayer.start();
            playPauseButton.setImageResource(R.drawable.ic_pause_black_72dp);
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
                    updatePlayer();
                    mediaPlayer.start();
                }
            });

            //init title label
            final TextView title = (TextView) currentContext.findViewById(R.id.playChapter_title);
            currentContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    title.setText(currentChapter.getChapterTitle());
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void stop() {
        try{
            if(mediaPlayer != null && mediaPlayer.isPlaying()){
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }catch (Throwable t){
            t.printStackTrace();
        }
    }

    public void forwardPlay(int seconds) {
        int currPos = mediaPlayer.getCurrentPosition();
        // + 10 sec
        int newPos = currPos + seconds*1000;
        if(newPos > mediaPlayer.getDuration()){
            // last second
            newPos = mediaPlayer.getDuration() - 1000;
        }else if(newPos < 0){
            // reset to start
            newPos = 0;
        }

        boolean wasPlaying = mediaPlayer.isPlaying();

        mediaPlayer.pause();
        mediaPlayer.seekTo(newPos);
        updatePlayer();

        if(wasPlaying){
            mediaPlayer.start();
        }
    }

    public void backwardPlay(int seconds) {
        forwardPlay(-seconds);
    }


    public static void savePlayerState(Chapter currentChapter) throws IOException {
        String metadataFilePath = currentChapter.getBook().getAppDir() + File.separator + METADATA + File.separator;
        MyFileUtils.mkDir(metadataFilePath);

        String playerStateFilePath = metadataFilePath + File.separator + PLAYER_STATE_FILE;

        MyFileUtils.deleteFileIfExists(playerStateFilePath);

        String jsonStr = new Gson().toJson(currentChapter);

        FileWriter fw = new FileWriter(playerStateFilePath);
        fw.write(jsonStr);
        fw.close();
    }

    public static Chapter getPlayerState(Context context){
        String metadataFilePath = context.getFilesDir().getAbsolutePath() + File.separator + METADATA + File.separator;
        String playerStateFilePath = metadataFilePath + File.separator + PLAYER_STATE_FILE;

        Chapter loadedChapter = null;
        try{
            loadedChapter = new Gson().fromJson(new FileReader(playerStateFilePath), Chapter.class);
            System.out.println("Loaded json: " + loadedChapter);
        }catch (FileNotFoundException e){
            //nothing
        }

        return loadedChapter;
    }

    public static void deletePlayerState(Context context){
        String metadataFilePath = context.getFilesDir().getAbsolutePath() + File.separator + METADATA + File.separator;
        String playerStateFilePath = metadataFilePath + File.separator + PLAYER_STATE_FILE;
        MyFileUtils.deleteFileIfExists(playerStateFilePath);
    }
}