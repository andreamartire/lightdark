package newtech.audiolibrary;

/**
 * Created by andrea on 18/07/17.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

        ArrayList<Book> bookTitles = new ArrayList<>();
        final Map<String,List<Chapter>> chaptersByTitle = new HashMap<String,List<Chapter>>();

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
                        String randomBook = "book" + (new Random().nextInt(Integer.MAX_VALUE)%9+1) +"_small";
                        book.setImageResId(this.getResources().getIdentifier(randomBook, "drawable", this.getPackageName()));

                        //add book entry
                        bookTitles.add(book);

                        //init book's chapters list
                        if(!chaptersByTitle.containsKey(bookTitle)){
                            chaptersByTitle.put(bookTitle, new ArrayList<Chapter>());
                        }

                        //setting chapters
                        Iterator it = obj.get(contents).getAsJsonArray().iterator();
                        while (it.hasNext()){
                            JsonElement jsonChapter = (JsonElement) it.next();

                            Chapter chapter = new Chapter();

                            chapter.setAppDir(this.getBaseContext().getFilesDir().getAbsolutePath());
                            chapter.setProviderName(providerName);
                            chapter.setBookTitle(bookTitle != null ? bookTitle.replaceAll("/", "_") : DEFAULT_TITLE);

                            JsonObject chapterObj = jsonChapter.getAsJsonObject();
                            if(chapterObj != null){
                                if(chapterObj.get(title) != null && chapterObj.get(url) != null){
                                    chapter.setTitle(chapterObj.get(title).getAsString().replaceAll("/", "_"));
                                    chapter.setUrl(chapterObj.get(url).getAsString());
                                }
                            }

                            chaptersByTitle.get(bookTitle).add(chapter);
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

        BookAdapter arrayAdapter = new BookAdapter(this.getBaseContext(), R.layout.single_book, bookTitles);
        listView.setAdapter(arrayAdapter);

        //init tap listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int i, long l){
                //manage tap on audiobook list
                Intent intent = new Intent(v.getContext(), ChapterShowList.class);

                Book book = (Book) adapterView.getItemAtPosition(i);

                //pass data thought intent to another activity
                intent.putExtra(ChapterShowList.TITLE, title);

                //TODO review chapters container in book
                intent.putExtra(ChapterShowList.CHAPTERS, (Serializable) chaptersByTitle.get(book.getTitle()));

                intent.putExtra(ChapterShowList.BOOK_IMAGE_ID, (Serializable) book.getImageResId());

                startActivity(intent);
            }
        });

        Chapter oldPlayerState = PlayThread.getPlayerState(this);
        if(oldPlayerState != null){
            Toast.makeText(this, "Player was playing: " + oldPlayerState.getFileName() + " at duration: " + oldPlayerState.getCurrentDuration(), Toast.LENGTH_LONG).show();

            //TODO manage player state

            PlayThread.deletePlayerState(this);
        }
    }
}