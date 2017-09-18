package newtech.audiolibrary.bean;

import java.io.File;
import java.io.Serializable;

/**
 * Created by MartireAn on 18/09/2017.
 */

public class Book implements Serializable {

    String title;

    public Book(String title){
        setTitle(title);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
