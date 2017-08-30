package newtech.audiolibrary.bean;

import java.io.Serializable;

/**
 * Created by MartireAn on 24/08/2017.
 */

public class Chapter implements Serializable {
    String bookTitle;
    String title;
    String url;
    String localFile;

    public Chapter(){

    }

    public Chapter(String title, String url, String localFile) {
        this.title = title;
        this.url = url;
        this.localFile = localFile;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocalFile() {
        return localFile;
    }

    public void setLocalFile(String localFile) {
        this.localFile = localFile;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
