package newtech.audiolibrary.bean;

import java.io.File;
import java.io.Serializable;
import java.util.Random;

/**
 * Created by MartireAn on 18/09/2017.
 */

public class Book implements Serializable {

    String title;
    int imageResId;

    public Book(String title){
        setTitle(title);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }
}
