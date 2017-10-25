package newtech.audiolibrary.bean;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by MartireAn on 24/08/2017.
 */

public class Chapter implements Serializable {

    public static String MP3_EXTENSION = ".mp3";

    int chapterId;
    Book book;

    String chapterTitle;
    String url;

    // used only for saving current playing chapter
    int currentDuration;
    int totalDuration;

    transient Chapter nextChapter;
    transient Chapter previousChapter;

    boolean isDownloading = false;

    public Chapter(){

    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
    }

    public Chapter getPreviousChapter() {
        return previousChapter;
    }

    public void setPreviousChapter(Chapter previousChapter) {
        this.previousChapter = previousChapter;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public Chapter getNextChapter() {
        return nextChapter;
    }

    public void setNextChapter(Chapter nextChapter) {
        this.nextChapter = nextChapter;
    }

    public int getCurrentDuration() {
        return currentDuration;
    }

    public void setCurrentDuration(int currentDuration) {
        this.currentDuration = currentDuration;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public String getBookDir() {
        return getProviderDir() + File.separator + getBook().getBookTitle();
    }

    public String getFileName(){
        return getChapterTitle().replaceAll(":", "") + MP3_EXTENSION;
    }

    public String getProviderDir(){
        return getBook().getAppDir() + File.separator + getBook().getProviderName();
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getLocalFilePath() {
        return getProviderDir() + File.separator + getBook().getBookTitle() + File.separator + getFileName();
    }

    public Boolean existsLocalFile() {
        String localFilePath = getLocalFilePath();
        boolean exists = new File(localFilePath).exists();
        return exists;
    }

    public Chapter getMatchingChapter(ArrayList<Chapter> chapters) {
        if(chapters != null){
            for(Chapter linkedChapter : chapters){
                if(this.getLocalFilePath().equals(linkedChapter.getLocalFilePath())){
                    return linkedChapter;
                }
            }
        }

        return null;
    }

    public int getPlayedPercentage(){
        if(totalDuration > 0){
            Double percentage = ((double)currentDuration)/totalDuration;
            return (int) Math.round(percentage*100);
        }
        return 0;
    }
}
