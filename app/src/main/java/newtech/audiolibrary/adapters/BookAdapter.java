package newtech.audiolibrary.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import newtech.audiolibrary.R;
import newtech.audiolibrary.bean.Book;

/**
 * Created by MartireAn on 19/09/2017.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    private Context context;
    private int resource;
    private ArrayList<Book> books;

    public BookAdapter(Context context, int resource, ArrayList<Book> books) {
        super(context, resource, books);
        this.context = context;
        this.resource = resource;
        this.books = books;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        Book book = books.get(position);

        if (book != null){
            if (convertView == null){
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.single_book, null);
            }

            TextView bookTitle = (TextView) convertView.findViewById(R.id.bookTitleView);
            bookTitle.setText(book.getTitle());

            ImageView imageView = (ImageView) convertView.findViewById(R.id.bookImageView);
            imageView.setImageResource(book.getImageResId());
        }

        return convertView;
    }
}
