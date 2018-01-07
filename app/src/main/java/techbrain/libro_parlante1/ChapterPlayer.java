package techbrain.libro_parlante1;

/**
 * Created by andrea on 06/09/17.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;

import techbrain.libro_parlante1.R;
import techbrain.libro_parlante1.adapters.PlayThread;
import techbrain.libro_parlante1.bean.Book;
import techbrain.libro_parlante1.bean.Chapter;
import techbrain.libro_parlante1.utils.ConfigUtils;
import techbrain.libro_parlante1.utils.ImageUtils;

public class ChapterPlayer extends AppCompatActivity {

    public static String CHAPTER = "CHAPTER";

    static PlayThread playThread;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Context me = this;

        switch (item.getItemId()) {
            case R.id.playerShareElement:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");

                String shareBodyText = getResources().getString(R.string.share_message);

                if(playThread.getCurrentChapter() != null && playThread.getCurrentChapter().getBook() != null){
                    String appName = getResources().getString(R.string.app_name);
                    String appUrl = getResources().getString(R.string.app_url);
                    shareBodyText = "Ascolta \"" + playThread.getCurrentChapter().getBook().getBookTitle() + "\" sull'app gratuita " + appName + " " + appUrl;
                }

                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                startActivity(Intent.createChooser(sharingIntent, "Share"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_chapter);

        AdView mAdView = (AdView) findViewById(R.id.adViewPlayer);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.playerToolbar);
        myToolbar.showOverflowMenu();
        setSupportActionBar(myToolbar);

        Chapter currentChapter = (Chapter) getIntent().getSerializableExtra(CHAPTER);

        if(currentChapter != null){
            Book linkedBook = ConfigUtils.bookWithChapters.get(currentChapter.getBook().getBookDir());
            // convert to linked chapter. avoid to spread this logic
            if(linkedBook != null && currentChapter != null){
                currentChapter = currentChapter.getMatchingChapter(linkedBook.getChapters());

                ImageView bookImage = (ImageView) findViewById(R.id.bookImage);

                Point size = new Point();
                getWindowManager().getDefaultDisplay().getSize(size);

                if(currentChapter.getBook().getLocalImageResource() != null){
                    Drawable image = currentChapter.getBook().getLocalImageResource();
                    bookImage.setImageDrawable(ImageUtils.scaleImage(this, image, size.x, (int) size.x*3/5));
                }

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

                boolean queued = false;

                while (!queued){
                    try{
                        playThread.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                        queued = true;
                    } catch (Throwable t){
                        t.printStackTrace();
                        queued = false;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(playThread != null){
            playThread.stop();
        }
    }

    public static void startPlayer(Context context, Chapter currentChapter){
        Intent intent = new Intent(context, ChapterPlayer.class);

        //pass data thought intent to another activity
        intent.putExtra(ChapterPlayer.CHAPTER, currentChapter);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }
}