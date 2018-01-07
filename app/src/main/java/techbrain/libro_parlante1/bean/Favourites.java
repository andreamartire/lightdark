package techbrain.libro_parlante1.bean;

import java.util.ArrayList;

/**
 * Created by MartireAn on 22/12/2017.
 */

public class Favourites {
    ArrayList<Book> books = new ArrayList<>();

    public Favourites() {

    }

    public ArrayList<Book> getBooks() {
        return books;
    }

    public void setBooks(ArrayList<Book> books) {
        this.books = books;
    }
}
