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
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import newtech.audiolibrary.adapters.BookAdapter;
import newtech.audiolibrary.adapters.PlayThread;
import newtech.audiolibrary.bean.Book;
import newtech.audiolibrary.bean.Chapter;
import newtech.audiolibrary.task.SimpleDownloadTask;
import newtech.audiolibrary.utils.ImageUtils;
import newtech.audiolibrary.utils.MyFileUtils;

public class AudioBookShowList extends Activity {

    private static String audiobooks = "audiobooks";
    private static String contents = "contents";
    public static String metadata = "metadata";
    private static String provider = "provider";
    private static String image = "image";
    private static String name = "name";
    private static String title = "title";
    private static String url = "url";

    private static String DEFAULT_PROVIDER = "default_provider";
    private static String DEFAULT_TITLE = "default_title";
    private static String DEFAULT_BOOK = "default_book";

    private BookAdapter bookAdapter;

    private static HashMap<String, Book> bookWithChapters = new HashMap<String, Book>();

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
                    String imageUrl = null;
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

                            JsonElement imageObj = metadataObj.getAsJsonObject().get(AudioBookShowList.image);
                            if(imageObj != null){
                                imageUrl = imageObj.getAsString();
                            }
                        }
                    }

                    if(obj.get(contents) != null && obj.get(contents).getAsJsonArray().size() > 0){
                        JsonElement firstContentElement = obj.get(contents).getAsJsonArray().get(0);

                        // fallback management. if book have metadata without title, select first chapter title
                        if(DEFAULT_BOOK.equals(bookTitle) && firstContentElement != null){
                            bookTitle = firstContentElement.getAsJsonObject().get("title").getAsString();
                        }

                        String descBook = "";
                        if(firstContentElement.getAsJsonObject().get("desc") != null){
                            descBook = firstContentElement.getAsJsonObject().get("desc").getAsString();
                        }

                        Book book = new Book(bookTitle);
                        book.setProviderName(providerName);
                        book.setDescr(descBook);
                        book.setAppDir(this.getBaseContext().getFilesDir().getAbsolutePath());
                        //select random image
                        book.setLocalImageResource(ImageUtils.getRandomDefaultImage(this));

                        //TODO add author
                        try{
                            book.setRemoteImageUrl(imageUrl != null ? new URL(imageUrl) : null);

                            if(new File(book.getLocalImageFileName()).exists()){
                                //select local image
                                Drawable image = Drawable.createFromPath(book.getLocalImageFileName());
                                book.setLocalImageResource(image);
                            }
                        }
                        catch (MalformedURLException e){
                            e.printStackTrace();
                        }

                        //FIXME not good programming
                        book.setBookTitle(bookTitle != null ? bookTitle.replaceAll("/", "_") : DEFAULT_TITLE);

                        //add book entry
                        bookList.add(book);
                        bookWithChapters.put(book.getBookTitle(), book);

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
                System.out.println("Test query: " + query);
                bookAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                System.out.println("Test newText: " + newText);
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

                    Intent intent = new Intent(me, ChapterShowList.class);

                    //pass data thought intent to another activity
                    intent.putExtra(ChapterShowList.BOOK, currentBook);
                    intent.putExtra(ChapterShowList.CHAPTERS, currentBook.getChapters());
                    intent.putExtra(ChapterShowList.PLAYING_CHAPTER, playingChapter);

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

                String localFileImage = oldPlayerState.getBook().getLocalImageFilePath();
                if(new File(localFileImage).exists()){
                    //set file image
                    //select current image
                    Drawable image = Drawable.createFromPath(localFileImage);

                    if(image == null){
                        //get random default image
                        image = ImageUtils.getRandomDefaultImage(this);

                        Book book = oldPlayerState.getBook();

                        //execute asynch download
                        SimpleDownloadTask sdt = new SimpleDownloadTask(book.getRemoteImageUrl(), book.getLocalImageFilePath());
                        sdt.execute();
                    }

                    Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
                    image = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 1000, 600, true));

                    ImageView resumeBookImageView = (ImageView) this.findViewById(R.id.currentPlayingBookImage);

                    //select downloaded image
                    resumeBookImageView.setImageDrawable(image);
                }
            }
            else{
                //TODO comment
                Toast.makeText(this, "Old playing chapter was deleted", Toast.LENGTH_LONG).show();
            }
        }
    }
}