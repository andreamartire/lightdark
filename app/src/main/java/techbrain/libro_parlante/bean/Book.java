package techbrain.libro_parlante.bean;

import android.graphics.drawable.Drawable;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

import techbrain.libro_parlante.utils.ConfigUtils;

/**
 * Created by MartireAn on 18/09/2017.
 */

public class Book implements Serializable {

    String appDir;
    String providerName;
    String bookTitle;
    String descr;
    String author;
    String bookDir;

    URL remoteImageUrl;

    // transient avoid loop in Gson conversion
    transient ArrayList<Chapter> chapters = new ArrayList<Chapter>();
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

    public String getBookDir() {
        return getAppDir() + File.separator + getProviderName() + File.separator + getBookTitle() + File.separator;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getLocalImageFileName() {
        if(getRemoteImageUrl() != null){
            String imageFilePath = getRemoteImageUrl().getPath();
            String localImageFileName = imageFilePath.substring(imageFilePath.lastIndexOf('/') + 1);
            return localImageFileName;
        }
        return null;
    }

    public String getLocalImageFilePath() {
        String localImageFileName = getLocalImageFileName();
        if(localImageFileName != null){
            return getAppDir() + File.separator + getBookTitle() + File.separator +
                    ConfigUtils.metadata + File.separator + localImageFileName;
        }
        return null;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public double getBookPlayerPercentage(Chapter playingChapter){
        int totChapDur = playingChapter.getTotalDuration();
        int currChapDur = playingChapter.getCurrentDuration();

        if(chapters != null){
            int numChapters = chapters.size();

            int chapterIndex = playingChapter.getChapterId();

            int evalTotalBookDur = totChapDur*numChapters;
            int evalPlayedBookDur = totChapDur*(chapterIndex-1) + currChapDur;

            double globalPerc = (Math.round(((double)evalPlayedBookDur)/((double)evalTotalBookDur)*100));
            return globalPerc;
        }

        return 0;
    }
}
