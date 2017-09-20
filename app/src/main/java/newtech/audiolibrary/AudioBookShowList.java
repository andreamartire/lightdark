package newtech.audiolibrary;

/**
 * Created by andrea on 18/07/17.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import newtech.audiolibrary.adapters.BookAdapter;
import newtech.audiolibrary.adapters.PlayThread;
import newtech.audiolibrary.bean.Book;
import newtech.audiolibrary.bean.Chapter;
import newtech.audiolibrary.utils.MyFileUtils;

public class AudioBookShowList extends Activity {

    private static String audiobooks = "audiobooks";
    private static String contents = "contents";
    private static String metadata = "metadata";
    private static String provider = "provider";
    private static String name = "name";
    private static String title = "title";
    private static String url = "url";

    private static String DEFAULT_PROVIDER = "default_provider";
    private static String DEFAULT_TITLE = "default_title";
    private static String DEFAULT_BOOK = "default_book";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audiobooks);
        ListView listView = (ListView)findViewById(R.id.audiobooks_listview);

        ArrayList<Book> bookList = new ArrayList<>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open("config.json")));

            String message = org.apache.commons.io.IOUtils.toString(reader);

            JsonObject o = new JsonParser().parse(message).getAsJsonObject();

            JsonElement audioBooks = o.get(audiobooks);
            if(audioBooks != null){
                JsonArray audioBooksArray = audioBooks.getAsJsonArray();

                for (int i = 0 ; i < audioBooksArray.size(); i++) {
                    JsonObject obj = audioBooksArray.get(i).getAsJsonObject();

                    //setting provider
                    String providerName = DEFAULT_PROVIDER;
                    String bookTitle = DEFAULT_BOOK;
                    if(obj.get(metadata) != null && obj.get(metadata).getAsJsonArray().size() > 0){
                        JsonElement metadataObj = obj.get(metadata).getAsJsonArray().get(0);
                        if(metadataObj != null){
                            JsonElement providerObj = metadataObj.getAsJsonObject().get(AudioBookShowList.provider);
                            if(providerObj != null){
                                JsonElement providerNameObj = providerObj.getAsJsonObject().get(name);
                                if(providerNameObj != null){
                                    providerName = providerNameObj.getAsString();
                                }
                            }

                            JsonElement bookTitleObj = metadataObj.getAsJsonObject().get(title);
                            if(bookTitleObj != null){
                                bookTitle = bookTitleObj.getAsString();
                            }
                        }
                    }

                    if(obj.get(contents) != null && obj.get(contents).getAsJsonArray().size() > 0){
                        JsonElement firstContentElement = obj.get(contents).getAsJsonArray().get(0);

                        // fallback management. if book have metadata without title, select first chapter
                        if(DEFAULT_BOOK.equals(bookTitle) && firstContentElement != null){
                            bookTitle = firstContentElement.getAsJsonObject().get("title").getAsString();
                        }

                        Book book = new Book(bookTitle);
                        book.setProviderName(providerName);
                        book.setAppDir(this.getBaseContext().getFilesDir().getAbsolutePath());
                        //FIXME
                        book.setBookTitle(bookTitle != null ? bookTitle.replaceAll("/", "_") : DEFAULT_TITLE);

                        String randomBook = "book" + (new Random().nextInt(Integer.MAX_VALUE)%9+1) +"_small";
                        book.setImageResId(this.getResources().getIdentifier(randomBook, "drawable", this.getPackageName()));

                        //add book entry
                        bookList.add(book);

                        //setting chapters
                        Iterator it = obj.get(contents).getAsJsonArray().iterator();
                        while (it.hasNext()){
                            JsonElement jsonChapter = (JsonElement) it.next();

                            Chapter chapter = new Chapter();

                            JsonObject chapterObj = jsonChapter.getAsJsonObject();
                            if(chapterObj != null){
                                if(chapterObj.get(title) != null && chapterObj.get(url) != null){
                                    chapter.setChapterTitle(chapterObj.get(title).getAsString().replaceAll("/", "_"));
                                    chapter.setUrl(chapterObj.get(url).getAsString());
                                    chapter.setBook(book);
                                    book.getChapters().add(chapter);
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }

        BookAdapter arrayAdapter = new BookAdapter(this.getBaseContext(), R.layout.single_book, bookList);
        listView.setAdapter(arrayAdapter);

        //init tap listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int i, long l){
                //manage tap on audiobook list
                Intent intent = new Intent(v.getContext(), ChapterShowList.class);

                Book book = (Book) adapterView.getItemAtPosition(i);

                //pass data thought intent to another activity

                intent.putExtra(ChapterShowList.BOOK_IMAGE_ID, (Serializable) book.getImageResId());

                intent.putExtra(ChapterShowList.CHAPTERS, (Serializable) book.getChapters());

                startActivity(intent);
            }
        });

        final Context me = this;

        Button playingResumeButton = (Button) this.findViewById(R.id.currentPlayingResumeButton);
        playingResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chapter playingChapter = PlayThread.getPlayerState(me);

                if(playingChapter != null){
                    Book currentBook = playingChapter.getBook();

                    //manage tap on audiobook list
                    Intent intent = new Intent(v.getContext(), ChapterShowList.class);
                    //pass data thought intent to another activity
                    intent.putExtra(ChapterShowList.CHAPTERS, (Serializable) currentBook.getChapters());
                    intent.putExtra(ChapterShowList.BOOK_IMAGE_ID, (Serializable) currentBook.getImageResId());

                    startActivity(intent);
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
            //Toast.makeText(this, "Player was playing: " + oldPlayerState.getFileName() + " at duration: " + oldPlayerState.getCurrentDuration() + "/" + oldPlayerState.getTotalDuration(), Toast.LENGTH_LONG).show();
            TextView playingBookTitle = (TextView) this.findViewById(R.id.currentPlayingBookTitle);
            playingBookTitle.setText(oldPlayerState.getBook().getBookTitle());
            TextView playingChapterTitle = (TextView) this.findViewById(R.id.currentPlayingChapterTitle);
            playingChapterTitle.setText(oldPlayerState.getFileName());
            TextView playingChapterPercentage = (TextView) this.findViewById(R.id.currentPlayingChapterPercentage);
            playingChapterPercentage.setText(oldPlayerState.getCurrentDuration()+"/"+oldPlayerState.getTotalDuration());
        }
    }
}