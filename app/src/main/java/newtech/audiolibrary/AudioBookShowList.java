package newtech.audiolibrary;

/**
 * Created by andrea on 18/07/17.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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

    private static HashMap<String, Book> bookWithChapters = new HashMap<String, Book>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audiobooks);

        ListView listView = (ListView)findViewById(R.id.audiobooks_listview);

        ArrayList<Book> bookList = new ArrayList<>();

        new ConfigUtils(this, bookWithChapters, "config.json", bookList).invoke();

        bookAdapter = new BookAdapter(this.getBaseContext(), R.layout.single_book, bookList);
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
            }
        });

        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                bookAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                bookAdapter.getFilter().filter(newText);
                return false;
            }
        });

        final Context me = this;

        ImageView playingBookImage = (ImageView) this.findViewById(R.id.currentPlayingBookImage);
        playingBookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chapter playingChapter = PlayThread.getPlayerState(me);

                if(playingChapter != null){
                    Book currentBook = playingChapter.getBook();

                    //fetch chapters - not saved in player state
                    currentBook = bookWithChapters.get(currentBook.getBookTitle());

                    //TODO need refactoring
                    Chapter linkedChapter = playingChapter.getMatchingChapter(currentBook.getChapters());

                    Intent intent = new Intent(me, ChapterShowList.class);

                    //pass data thought intent to another activity
                    intent.putExtra(ChapterShowList.BOOK, currentBook);
                    intent.putExtra(ChapterShowList.CHAPTERS, currentBook.getChapters());
                    intent.putExtra(ChapterShowList.PLAYING_CHAPTER, linkedChapter);

                    me.startActivity(intent);
                }
            }
        });

        checkCurrentPlayingState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkCurrentPlayingState();
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
                TextView playingChapterPercentage = (TextView) this.findViewById(R.id.currentPlayingChapterPercentage);
                playingChapterPercentage.setText(oldPlayerState.getCurrentDuration()+"/"+oldPlayerState.getTotalDuration());

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
                            SimpleDownloadTask sdt = new SimpleDownloadTask(book.getRemoteImageUrl(), book.getLocalImageFilePath());
                            sdt.execute();
                        }
                    }
                }

                Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
                image = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 1000, 600, true));

                ImageView resumeBookImageView = (ImageView) this.findViewById(R.id.currentPlayingBookImage);

                //select downloaded image
                resumeBookImageView.setImageDrawable(image);
            }
            else{
                //Toast.makeText(this, "Old playing chapter was deleted", Toast.LENGTH_LONG).show();
            }
        }
    }
}