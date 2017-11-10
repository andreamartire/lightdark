package newtech.audiolibrary.utils;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import newtech.audiolibrary.AudioBookShowList;
import newtech.audiolibrary.SplashActivity;
import newtech.audiolibrary.bean.Book;
import newtech.audiolibrary.bean.Chapter;

public final class ConfigUtils {

    public static ArrayList<Book> bookList = new ArrayList<Book>();
    public static HashMap<String, Book> bookWithChapters = new HashMap<String, Book>();

    public static String audiobooks = "audiobooks";
    private static String contents = "contents";
    public static String metadata = "metadata";
    private static String provider = "provider";
    private static String image = "image";
    private static String other_images = "other_images";
    private static String image_300 = "image_300";
    private static String image_433 = "image_433";
    private static String name = "name";
    private static String title = "title";
    private static String url = "url";
    private static String author = "author";

    private static String DEFAULT_PROVIDER = "default_provider";
    private static String DEFAULT_TITLE = "default_title";
    private static String DEFAULT_BOOK = "default_book";


    public static void invoke(Activity activity, String configFile) {

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(activity.getAssets().open(configFile)));

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
                    String imageUrl = null, image433Url = null, image300Url = null;
                    String authorBook = "";

                    if(obj.get(metadata) != null){
                        JsonElement metadataObj = obj.get(metadata).getAsJsonObject();
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

                            JsonElement otherImagesEl = metadataObj.getAsJsonObject().get(other_images);
                            if(otherImagesEl != null){
                                JsonElement image433El = otherImagesEl.getAsJsonObject().get(image_433);
                                if(image433El != null){
                                    image433Url = image433El.getAsString();
                                }
                                JsonElement image300El = otherImagesEl.getAsJsonObject().get(image_300);
                                if(image300El != null){
                                    image300Url = image300El.getAsString();
                                }
                            }

                            JsonElement imageObj = metadataObj.getAsJsonObject().get(image);
                            if(imageObj != null){
                                imageUrl = imageObj.getAsString();
                            }

                            if(metadataObj.getAsJsonObject().get(author) != null){
                                authorBook = metadataObj.getAsJsonObject().get(author).getAsString();
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
                        book.setAuthor(authorBook);
                        book.setAppDir(activity.getFilesDir().getAbsolutePath());

                        //select random image
                        Drawable randomImage = ImageUtils.getRandomDefaultImage(activity);

                        Integer realWidth = ImageUtils.getRealWidthSize(activity.getWindowManager());
                        int customWidth = realWidth*45/100;
                        int customHeight = customWidth*3/5;
                        book.setLocalImageResource(ImageUtils.scaleImage(activity, randomImage, customWidth, customHeight));

                        try{
                            if(ImageUtils.isValidUri(image433Url)){
                                book.setRemoteImageUrl(image433Url != null ? new URL(image433Url) : null);
                            }else if(ImageUtils.isValidUri(image300Url)){
                                book.setRemoteImageUrl(image300Url != null ? new URL(image300Url) : null);
                            }else {
                                book.setRemoteImageUrl(imageUrl != null ? new URL(imageUrl) : null);
                            }

                            //disabled avoid out of memory
                            /*if(new File(book.getLocalImageFilePath()).exists()){
                                //select local image

                                Drawable image = Drawable.createFromPath(book.getLocalImageFilePath());
                                book.setLocalImageResource(image);
                            }*/
                        }
                        catch (MalformedURLException e){
                            e.printStackTrace();
                        }

                        //FIXME not good programming
                        book.setBookTitle(bookTitle != null ? bookTitle.replaceAll("/", "_") : DEFAULT_TITLE);

                        //add book entry
                        bookList.add(book);
                        bookWithChapters.put(book.getBookDir(), book);

                        //setting chapters
                        Iterator it = obj.get(contents).getAsJsonArray().iterator();

                        Chapter previousChapter = null;

                        int chapterIndex = 1;
                        while (it.hasNext()){
                            JsonElement jsonChapter = (JsonElement) it.next();

                            Chapter chapter = new Chapter();

                            JsonObject chapterObj = jsonChapter.getAsJsonObject();
                            if(chapterObj != null){
                                if(chapterObj.get(title) != null && chapterObj.get(url) != null){
                                    chapter.setChapterTitle(chapterObj.get(title).getAsString().replaceAll("/", "_"));
                                    chapter.setUrl(chapterObj.get(url).getAsString());
                                    chapter.setBook(book);
                                    chapter.setPreviousChapter(previousChapter);

                                    //set chapter index
                                    chapter.setChapterId(chapterIndex);
                                    chapterIndex++;

                                    //set curr element as nextChapter in current last element
                                    Chapter lastChapter = null;
                                    if(!book.getChapters().isEmpty()){
                                        lastChapter = book.getChapters().get(book.getChapters().size()-1);
                                        lastChapter.setNextChapter(chapter);
                                    }
                                    //add current element to chapters
                                    book.getChapters().add(chapter);

                                    previousChapter = chapter;
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
