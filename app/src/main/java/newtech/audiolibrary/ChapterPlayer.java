package newtech.audiolibrary;

/**
 * Created by andrea on 06/09/17.
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import newtech.audiolibrary.adapters.PlayThread;
import newtech.audiolibrary.bean.Chapter;

public class ChapterPlayer extends Activity {

    public static String CHAPTER = "CHAPTER";

    static PlayThread tt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_chapter);

        Chapter currentChapter = (Chapter) getIntent().getSerializableExtra(CHAPTER);

        tt = new PlayThread(this, currentChapter);

        ImageButton playPauseButton = (ImageButton) findViewById(R.id.playPauseButton);
        playPauseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v) {
                tt.toogle();
            }
        });

        ImageButton backwardButton10 = (ImageButton) findViewById(R.id.backwardButton10);
        backwardButton10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tt.backwardPlay(10);
            }
        });

        ImageButton backwardButton30 = (ImageButton) findViewById(R.id.backwardButton30);
        backwardButton30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tt.backwardPlay(30);
            }
        });

        ImageButton forwardButton10 = (ImageButton) findViewById(R.id.forwardButton10);
        forwardButton10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tt.forwardPlay(10);
            }
        });

        ImageButton forwardButton30 = (ImageButton) findViewById(R.id.forwardButton30);
        forwardButton30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tt.forwardPlay(30);
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