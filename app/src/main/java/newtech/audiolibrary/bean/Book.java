package newtech.audiolibrary.bean;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by MartireAn on 18/09/2017.
 */

public class Book implements Serializable {

    String appDir;
    String providerName;
    String bookTitle;
    URL imageUrl;

    int imageResId;

    transient List<Chapter> chapters;

    public Book(String title){
        setBookTitle(title);
    }

    public URL getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(URL imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
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

    public List<Chapter> getChapters() {
        if(chapters == null){
            chapters = new LinkedList<>();
        }
        return chapters;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }
}
