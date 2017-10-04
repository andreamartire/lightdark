package newtech.audiolibrary.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import newtech.audiolibrary.AudioBookShowList;
import newtech.audiolibrary.R;
import newtech.audiolibrary.bean.Book;
import newtech.audiolibrary.bean.Chapter;

public class ConfigUtils {

    private Context context;
    private ArrayList<Book> bookList;
    private String configFile;
    private HashMap<String, Book> bookWithChapters;

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

    public ConfigUtils(Context context, HashMap<String, Book> bookWithChapters, String configFile, ArrayList<Book> bookList) {
        this.context = context;
        this.bookWithChapters = bookWithChapters;
        this.bookList = bookList;
        this.configFile = configFile;
    }

    public void invoke() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open(configFile)));

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
                            JsonElement providerObj = metadataObj.getAsJsonObject().get(provider);
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

                            JsonElement imageObj = metadataObj.getAsJsonObject().get(image);
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
                        book.setAppDir(context.getFilesDir().getAbsolutePath());
                        //select random image
                        book.setLocalImageResource(ImageUtils.getRandomDefaultImage(context));

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

                                    //set curr element as nextChapter in current last element
                                    Chapter lastChapter = null;
                                    if(!book.getChapters().isEmpty()){
                                        lastChapter = book.getChapters().get(book.getChapters().size()-1);
                                        lastChapter.setNextChapter(chapter);
                                    }
                                    //add current element to chapters
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
    }
}
