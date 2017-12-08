package techbrain.libro_parlante;

/**
 * Created by andrea on 24/08/17.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.util.ArrayList;

import techbrain.libro_parlante.R;
import techbrain.libro_parlante.adapters.ChapterAdapter;
import techbrain.libro_parlante.adapters.PlayThread;
import techbrain.libro_parlante.bean.Book;
import techbrain.libro_parlante.bean.Chapter;
import techbrain.libro_parlante.task.SimpleDownloadTask;
import techbrain.libro_parlante.utils.ConfigUtils;
import techbrain.libro_parlante.utils.ImageUtils;
import techbrain.libro_parlante.utils.MyFileUtils;

public class ChapterShowList extends AppCompatActivity {

    public static String BOOK = "BOOK";
    public static String CHAPTERS = "CHAPTERS";
    public static String PLAYING_CHAPTER = "PLAYING_CHAPTER";

    public Book book;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_chapters, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Context me = this;

        switch (item.getItemId()) {
            case R.id.chapterShareElement:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");

                String shareBodyText = getResources().getString(R.string.share_message);

                if(book != null){
                    String appName = getResources().getString(R.string.app_name);
                    String appUrl = getResources().getString(R.string.app_url);
                    shareBodyText = "Ascolta " + book.getBookTitle() + " sull'app gratuita " + appName + " " + appUrl;
                }

                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Subject here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                startActivity(Intent.createChooser(sharingIntent, "Shearing Option"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapters);

        AdView mAdView = (AdView) findViewById(R.id.adViewChapters);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.chaptersToolbar);
        myToolbar.showOverflowMenu();
        setSupportActionBar(myToolbar);

        book = (Book) getIntent().getSerializableExtra(BOOK);

        if(book != null){
            // convert to linked book
            book = ConfigUtils.bookWithChapters.get(book.getBookDir());

            if(book != null){
                ArrayList<Chapter> chapters = (ArrayList<Chapter>) getIntent().getSerializableExtra(CHAPTERS);

                ArrayAdapter<Chapter> arrayAdapter = new ChapterAdapter(this.getBaseContext(), R.layout.single_chapter, chapters);
                ListView chaptersListView = (ListView) findViewById(R.id.chapters_listview);
                chaptersListView.setAdapter(arrayAdapter);
                /*chaptersListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
                        ChapterPlayStreamButton playStreamButton = (ChapterPlayStreamButton) view.findViewById(R.id.playButton);

                        Chapter currentChapter = playStreamButton.getChapter();

                        if(currentChapter != null && currentChapter.existsLocalFile()){
                            ChapterPlayer.startPlayer(currentContext, currentChapter);
                        }else{
                            Toast.makeText(currentContext, "Effettua prima il download del capitolo", Toast.LENGTH_LONG).show();
                        }
                    }
                });*/

                Integer realWidth = ImageUtils.getRealWidthSize(getWindowManager());

                Drawable bookImage = book.getLocalImageResource();
                String localFilePath = book.getLocalImageFilePath();

                if(localFilePath != null && new File(localFilePath).exists()){
                    bookImage = Drawable.createFromPath(localFilePath);
                }

                final Context me = this;

                ImageView bookImageView = (ImageView) findViewById(R.id.chapterListCurrentPlayingBookImage);
                bookImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    Chapter playingChapter = PlayThread.getPlayerState(me);

                    if(playingChapter != null && playingChapter.getBook().getBookTitle().equals(book.getBookTitle())){
                        Book currentBook = playingChapter.getBook();

                        //fetch chapters - not saved in player state
                        currentBook = ConfigUtils.bookWithChapters.get(currentBook.getBookDir());

                        playingChapter = playingChapter.getMatchingChapter(currentBook.getChapters());

                        if(MyFileUtils.exists(playingChapter.getLocalFilePath())){
                            //resume old playing chapter
                            ChapterPlayer.startPlayer(me, playingChapter);
                        }
                    }
                    }
                });

                if(bookImage != null){
                    bookImageView.setImageDrawable(ImageUtils.scaleImage(this, bookImage, realWidth, (int) realWidth*3/5));
                }

                Chapter playingChapter = (Chapter) getIntent().getSerializableExtra(PLAYING_CHAPTER);

                if(playingChapter != null){
                    //resume old playing chapter
                    ChapterPlayer.startPlayer(this, playingChapter);
                }

                checkCurrentPlayingState();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkCurrentPlayingState();
    }

    private void checkCurrentPlayingState() {
        Chapter oldPlayerState = PlayThread.getPlayerState(this);
        if(book != null && oldPlayerState != null && oldPlayerState.getBook().getBookTitle().equals(book.getBookTitle())){
            if(MyFileUtils.exists(oldPlayerState.getLocalFilePath())){
                //Toast.makeText(this, "Player was playing: " + oldPlayerState.getFileName() + " at duration: " + oldPlayerState.getCurrentDuration() + "/" + oldPlayerState.getTotalDuration(), Toast.LENGTH_LONG).show();
                TextView playingBookTitle = (TextView) this.findViewById(R.id.chapterListPlayBook_title);
                playingBookTitle.setText(oldPlayerState.getBook().getBookTitle());
                TextView playingChapterTitle = (TextView) this.findViewById(R.id.chapterListPlayChapter_title);
                playingChapterTitle.setText(oldPlayerState.getChapterTitle());

                double bookPercentage = (double) oldPlayerState.getPlayedPercentage();
                ProgressBar progressBar = (ProgressBar) this.findViewById(R.id.chapterListProgressBar);
                progressBar.setProgress((int) bookPercentage);
                progressBar.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                String localImageFileName = oldPlayerState.getBook().getLocalImageFileName();
                Drawable image = oldPlayerState.getBook().getLocalImageResource();

                if(localImageFileName != null){
                    String localFileImage = oldPlayerState.getBook().getLocalImageFilePath();
                    if(new File(localFileImage).exists()){
                        //set file image
                        //select current image
                        Drawable imageFromLocalFile = Drawable.createFromPath(localFileImage);

                        if(image != null) {
                            //select local drawable
                            image = imageFromLocalFile;
                        }else{
                            Book book = oldPlayerState.getBook();

                            //execute asynch download
                            SimpleDownloadTask sdt = new SimpleDownloadTask(book.getRemoteImageUrl(), book.getLocalImageFilePath(), null);
                            try{
                                sdt.execute();//OnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                            catch (Throwable t){
                                t.printStackTrace();
                            }
                        }

                    }else{
                        Book book = oldPlayerState.getBook();

                        //execute asynch download
                        SimpleDownloadTask sdt = new SimpleDownloadTask(book.getRemoteImageUrl(), book.getLocalImageFilePath(), null);
                        try{
                            sdt.execute();//OnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                        catch (Throwable t){
                            t.printStackTrace();
                        }
                    }
                }

                ImageView resumeBookImageView = (ImageView) this.findViewById(R.id.chapterListCurrentPlayingBookImage);
                resumeBookImageView.setAlpha(0.8f);

                Integer realWidth = ImageUtils.getRealWidthSize(getWindowManager());

                //select downloaded image
                resumeBookImageView.setImageDrawable(ImageUtils.scaleImage(this, image, realWidth, (int) realWidth*3/5));
            }
            else{
                //Toast.makeText(this, "Old playing chapter was deleted", Toast.LENGTH_LONG).show();
            }
        }
    }
}