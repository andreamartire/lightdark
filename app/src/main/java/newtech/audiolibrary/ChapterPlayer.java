package newtech.audiolibrary;

/**
 * Created by andrea on 06/09/17.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;

import newtech.audiolibrary.adapters.PlayThread;
import newtech.audiolibrary.bean.Book;
import newtech.audiolibrary.bean.Chapter;
import newtech.audiolibrary.task.SimpleDownloadTask;
import newtech.audiolibrary.utils.ConfigUtils;
import newtech.audiolibrary.utils.ImageUtils;
import newtech.audiolibrary.utils.MyFileUtils;

public class ChapterPlayer extends Activity {

    public static String CHAPTER = "CHAPTER";

    static PlayThread playThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_chapter);

        AdView mAdView = (AdView) findViewById(R.id.adViewPlayer);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Chapter currentChapter = (Chapter) getIntent().getSerializableExtra(CHAPTER);
        Book linkedBook = ConfigUtils.bookWithChapters.get(currentChapter.getBook().getBookDir());
        // convert to linked chapter. avoid to spread this logic
        currentChapter = currentChapter.getMatchingChapter(linkedBook.getChapters());

        ImageView bookImage = (ImageView) findViewById(R.id.bookImage);

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);

        if(currentChapter.getBook().getLocalImageFilePath() != null && new File(currentChapter.getBook().getLocalImageFilePath()).exists()){
            Drawable drableBookImage = Drawable.createFromPath(currentChapter.getBook().getLocalImageFilePath());
            bookImage.setImageDrawable(ImageUtils.scaleImage(this, drableBookImage, size.x, (int) size.x*3/5));
        }

        playThread = new PlayThread(this, currentChapter);

        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                progressChanged = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                playThread.seekToPercentage(progressChanged);
            }
        });

        ImageButton playPauseButton = (ImageButton) findViewById(R.id.playPauseButton);
        playPauseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v) {
                playThread.toggle();
            }
        });

        ImageButton backwardButton10 = (ImageButton) findViewById(R.id.backwardButton10);
        backwardButton10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playThread.backwardPlay(10);
            }
        });

        ImageButton backwardButton30 = (ImageButton) findViewById(R.id.backwardButton30);
        backwardButton30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playThread.backwardPlay(30);
            }
        });

        ImageButton forwardButton10 = (ImageButton) findViewById(R.id.forwardButton10);
        forwardButton10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playThread.forwardPlay(10);
            }
        });

        ImageButton forwardButton30 = (ImageButton) findViewById(R.id.forwardButton30);
        forwardButton30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playThread.forwardPlay(30);
            }
        });

        playThread.execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        playThread.stop();
    }

    public static void startPlayer(Context context, Chapter currentChapter){
        Intent intent = new Intent(context, ChapterPlayer.class);

        //pass data thought intent to another activity
        intent.putExtra(ChapterPlayer.CHAPTER, currentChapter);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }
}