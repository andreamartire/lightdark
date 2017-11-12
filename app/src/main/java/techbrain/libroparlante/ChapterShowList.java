package techbrain.libroparlante;

/**
 * Created by andrea on 24/08/17.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import newtech.audiolibrary.R;
import techbrain.libroparlante.adapters.ChapterAdapter;
import techbrain.libroparlante.adapters.PlayThread;
import techbrain.libroparlante.bean.Book;
import techbrain.libroparlante.bean.Chapter;
import techbrain.libroparlante.task.SimpleDownloadTask;
import techbrain.libroparlante.utils.ConfigUtils;
import techbrain.libroparlante.utils.ImageUtils;
import techbrain.libroparlante.utils.MyFileUtils;

public class ChapterShowList extends Activity {

    public static String BOOK = "BOOK";
    public static String CHAPTERS = "CHAPTERS";
    public static String PLAYING_CHAPTER = "PLAYING_CHAPTER";

    public Book book;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapters);

        AdView mAdView = (AdView) findViewById(R.id.adViewChapters);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        book = (Book) getIntent().getSerializableExtra(BOOK);
        // convert to linked book
        book = ConfigUtils.bookWithChapters.get(book.getBookDir());

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

                //resume old playing chapter
                ChapterPlayer.startPlayer(me, playingChapter);
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

    @Override
    protected void onResume() {
        super.onResume();
        checkCurrentPlayingState();
    }

    private void checkCurrentPlayingState() {
        Chapter oldPlayerState = PlayThread.getPlayerState(this);
        if(oldPlayerState != null && oldPlayerState.getBook().getBookTitle().equals(book.getBookTitle())){
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
                            sdt.execute();
                        }

                    }else{
                        Book book = oldPlayerState.getBook();

                        //execute asynch download
                        SimpleDownloadTask sdt = new SimpleDownloadTask(book.getRemoteImageUrl(), book.getLocalImageFilePath(), null);
                        sdt.execute();
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