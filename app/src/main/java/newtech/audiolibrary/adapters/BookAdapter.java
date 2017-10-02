package newtech.audiolibrary.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import newtech.audiolibrary.R;
import newtech.audiolibrary.bean.Book;
import newtech.audiolibrary.task.SimpleDownloadTask;

/**
 * Created by MartireAn on 19/09/2017.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    private Context context;
    private int resource;
    private ArrayList<Book> books;
    private ArrayList<Book> booksBackup;
    private BookFilter filter;

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
            bookTitle.setText(book.getBookTitle());

            ImageView imageView = (ImageView) convertView.findViewById(R.id.bookImageView);

            //default image
            imageView.setImageDrawable(book.getLocalImageResource());

            if(book.getRemoteImageUrl() != null){

                //check if file name exists in book/metadata/
                if(new File(book.getLocalImageFilePath()).exists()){
                    //select current image
                    Drawable image = Drawable.createFromPath(book.getLocalImageFilePath());

                    if(image != null){
                        Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
                        image = new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap, 500, 300, true));

                        //select downloaded image
                        imageView.setImageDrawable(image);
                    }else{
                        //FIXME manage
                    }
                }else{
                    //download file out of main thread
                    SimpleDownloadTask sdt = new SimpleDownloadTask(book.getRemoteImageUrl(), book.getLocalImageFilePath());
                    sdt.execute();

                    //if downloaded
                    if(new File(book.getLocalImageFilePath()).exists()){
                        Drawable image = Drawable.createFromPath(book.getLocalImageFilePath());

                        //TODO resize image
                    }
                }
            }
        }

        return convertView;
    }

    @Override
    public Filter getFilter()
    {
        if(filter == null){
            filter = new BookFilter();
        }
        return filter;
    }

    private class BookFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // NOTE: this function is *always* called from a background thread, and
            // not the UI thread.
            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if(constraint != null && constraint.toString().length() > 0)
            {
                ArrayList<Book> filt = new ArrayList<Book>();
                ArrayList<Book> lItems = new ArrayList<Book>();
                synchronized (this)
                {
                    //if is null execute backup
                    if(booksBackup == null){
                        booksBackup = (ArrayList<Book>) books.clone();
                    }

                    //restore from backup
                    books = (ArrayList<Book>) booksBackup.clone();

                    //copy from backup
                    lItems.addAll(books);
                }
                for(int i = 0, l = lItems.size(); i < l; i++)
                {
                    Book m = lItems.get(i);
                    if(m.getBookTitle().toLowerCase().contains(constraint))
                        filt.add(m);
                }
                result.count = filt.size();
                result.values = filt;
            }
            else
            {
                synchronized(this)
                {
                    result.values = booksBackup;
                    result.count = booksBackup.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // NOTE: this function is *always* called from the UI thread.
            ArrayList<Book> filtered = (ArrayList<Book>) results.values;

            clear();
            for(int i = 0, l = filtered.size(); i < l; i++){
                add(filtered.get(i));
            }

            notifyDataSetChanged();
        }
    }
}
