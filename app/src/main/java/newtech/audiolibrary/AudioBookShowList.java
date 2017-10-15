package newtech.audiolibrary;

/**
 * Created by andrea on 18/07/17.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import newtech.audiolibrary.adapters.BookAdapter;
import newtech.audiolibrary.adapters.PlayThread;
import newtech.audiolibrary.bean.Book;
import newtech.audiolibrary.bean.Chapter;
import newtech.audiolibrary.task.SimpleDownloadTask;
import newtech.audiolibrary.utils.ConfigUtils;
import newtech.audiolibrary.utils.ImageUtils;
import newtech.audiolibrary.utils.MyFileUtils;

public class AudioBookShowList extends Activity {

    private BookAdapter bookAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audiobooks);

        ConfigUtils.invoke(this, "config.json");

        final SearchView searchView = (SearchView) findViewById(R.id.searchView);
        final LinearLayout currentPlayingInfo = (LinearLayout) findViewById(R.id.currentPlayingInfo);

        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                bookAdapter.getFilter().filter(query);
                currentPlayingInfo.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                bookAdapter.getFilter().filter(newText);
                if(newText != null && newText.length() > 0){
                    currentPlayingInfo.setVisibility(View.GONE);
                }else{
                    currentPlayingInfo.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        ListView listView = (ListView)findViewById(R.id.audiobooks_listview);
        bookAdapter = new BookAdapter(getBaseContext(), R.layout.single_book, ConfigUtils.bookList);
        listView.setAdapter(bookAdapter);
        //init tap listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int i, long l){
                //manage tap on audiobook list
                Intent intent = new Intent(v.getContext(), ChapterShowList.class);

                Book book = (Book) adapterView.getItemAtPosition(i);

                //pass data thought intent to another activity
                intent.putExtra(ChapterShowList.BOOK, (Serializable) book);
                intent.putExtra(ChapterShowList.CHAPTERS, (Serializable) book.getChapters());

                startActivity(intent);

                //TODO reset focus
                searchView.clearFocus();
            }
        });

        final Context me = this;

        final ImageView playingBookImage = (ImageView) findViewById(R.id.currentPlayingBookImage);
        playingBookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chapter playingChapter = PlayThread.getPlayerState(me);

                if(playingChapter != null){
                    Book currentBook = playingChapter.getBook();

                    //fetch chapters - not saved in player state
                    currentBook = ConfigUtils.bookWithChapters.get(currentBook.getBookDir());

                    Intent intent = new Intent(me, ChapterShowList.class);

                    //pass data thought intent to another activity
                    intent.putExtra(ChapterShowList.BOOK, currentBook);
                    intent.putExtra(ChapterShowList.CHAPTERS, currentBook.getChapters());
                    intent.putExtra(ChapterShowList.PLAYING_CHAPTER, playingChapter);

                    me.startActivity(intent);

                    //TODO reset search view
                    searchView.clearFocus();
                }
            }
        });

        checkCurrentPlayingState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkCurrentPlayingState();

        final SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setQuery("", false);
    }

    private void checkCurrentPlayingState() {
        Chapter oldPlayerState = PlayThread.getPlayerState(this);
        if(oldPlayerState != null){
            if(MyFileUtils.exists(oldPlayerState.getLocalFilePath())){
                //Toast.makeText(this, "Player was playing: " + oldPlayerState.getFileName() + " at duration: " + oldPlayerState.getCurrentDuration() + "/" + oldPlayerState.getTotalDuration(), Toast.LENGTH_LONG).show();
                TextView playingBookTitle = (TextView) this.findViewById(R.id.currentPlayingBookTitle);
                playingBookTitle.setText(oldPlayerState.getBook().getBookTitle());
                TextView playingChapterTitle = (TextView) this.findViewById(R.id.currentPlayingChapterTitle);
                playingChapterTitle.setText(oldPlayerState.getFileName());

                double bookPercentage = oldPlayerState.getBook().getBookPlayerPercentage(oldPlayerState);
                ProgressBar progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
                progressBar.setProgress((int) bookPercentage);
                progressBar.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                String localImageFileName = oldPlayerState.getBook().getLocalImageFileName();

                //get random default image
                Drawable image = ImageUtils.getRandomDefaultImage(this);

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

                ImageView resumeBookImageView = (ImageView) this.findViewById(R.id.currentPlayingBookImage);

                Point size = new Point();
                getWindowManager().getDefaultDisplay().getSize(size);

                //select downloaded image
                resumeBookImageView.setImageDrawable(ImageUtils.scaleImage(this, image, size.x, (int) size.x*3/5));
            }
            else{
                //Toast.makeText(this, "Old playing chapter was deleted", Toast.LENGTH_LONG).show();
            }
        }else{
            // TODO no book playing
            View playingInfo = this.findViewById(R.id.currentPlayingInfo);
            playingInfo.setVisibility(View.GONE);
        }
    }
}