package newtech.audiolibrary;

/**
 * Created by andrea on 06/09/17.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import newtech.audiolibrary.adapters.PlayThread;
import newtech.audiolibrary.bean.Book;
import newtech.audiolibrary.bean.Chapter;
import newtech.audiolibrary.utils.ConfigUtils;

public class ChapterPlayer extends Activity {

    public static String CHAPTER = "CHAPTER";

    static PlayThread playThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_chapter);

        Chapter currentChapter = (Chapter) getIntent().getSerializableExtra(CHAPTER);

        Book linkedBook = ConfigUtils.bookWithChapters.get(currentChapter.getBook().getBookDir());
        // convert to linked chapter. avoid to spread this logic
        currentChapter = currentChapter.getMatchingChapter(linkedBook.getChapters());

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