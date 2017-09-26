package newtech.audiolibrary.bean;

import android.graphics.drawable.Drawable;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import newtech.audiolibrary.AudioBookShowList;

/**
 * Created by MartireAn on 18/09/2017.
 */

public class Book implements Serializable {

    String appDir;
    String providerName;
    String bookTitle;

    URL remoteImageUrl;

    // transient avoid loop in Gson conversion
    transient ArrayList<Chapter> chapters;
    transient Drawable localImageResource;

    public Book(String title){
        setBookTitle(title);
    }

    public URL getRemoteImageUrl() {
        return remoteImageUrl;
    }

    public void setRemoteImageUrl(URL remoteImageUrl) {
        this.remoteImageUrl = remoteImageUrl;
    }

    public String getLocalImageFileName() {
        String imageFilePath = getRemoteImageUrl().getPath();
        String localImageFileName = imageFilePath.substring(imageFilePath.lastIndexOf('/') + 1);
        return localImageFileName;
    }

    public String getLocalImageFilePath() {
        String localImageFilePath = getAppDir() + File.separator + getBookTitle() + File.separator +
                AudioBookShowList.metadata + File.separator + getLocalImageFileName();
        return localImageFilePath;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public Drawable getLocalImageResource() {
        return localImageResource;
    }

    public void setLocalImageResource(Drawable localImageResource) {
        this.localImageResource = localImageResource;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getAppDir() {
        return appDir;
    }

    public void setAppDir(String appDir) {
        this.appDir = appDir;
    }

    public ArrayList<Chapter> getChapters() {
        if(chapters == null){
            chapters = new ArrayList<>();
        }
        return chapters;
    }

    public void setChapters(ArrayList<Chapter> chapters) {
        this.chapters = chapters;
    }
}
