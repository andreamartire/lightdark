package newtech.audiolibrary.bean;

import java.io.File;
import java.io.Serializable;

/**
 * Created by MartireAn on 24/08/2017.
 */

public class Chapter implements Serializable {

    public static String MP3_EXTENSION = ".mp3";

    String appDir;
    String providerName;
    String bookTitle;
    String title;
    String url;

    // used only for saving current playing chapter
    int currentDuration;

    public Chapter(){

    }

    public int getCurrentDuration() {
        return currentDuration;
    }

    public void setCurrentDuration(int currentDuration) {
        this.currentDuration = currentDuration;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAppDir() {
        return appDir;
    }

    public void setAppDir(String appDir) {
        this.appDir = appDir;
    }

    public String getBookDir() {
        return getProviderDir() + File.separator + getBookTitle();
    }

    public String getFileName(){
        return getTitle() + MP3_EXTENSION;
    }

    public String getProviderDir(){
        return getAppDir() + File.separator + getProviderName();
    }

    public String getLocalFilePath() {
        return getProviderDir() + File.separator + getBookTitle() + File.separator + getFileName();
    }

    public Boolean existsLocalFile() {
        String localFilePath = getLocalFilePath();
        boolean exists = new File(localFilePath).exists();
        return exists;
    }
}
