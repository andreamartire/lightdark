package techbrain.libro_parlante.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import techbrain.libro_parlante.R;
import techbrain.libro_parlante.bean.Book;
import techbrain.libro_parlante.task.SimpleDownloadTask;
import techbrain.libro_parlante.utils.ImageUtils;

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

        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null){
            convertView = vi.inflate(R.layout.single_book, null);
        }

        if(position >= books.size()){
            System.out.println("");
        }else{
            final Book book = books.get(position);

            if (book != null){
                LinearLayout rowBookLayout = (LinearLayout) convertView.findViewById(R.id.rowBookLayout);

//                LinearLayout adsBookLayout = (LinearLayout) convertView.findViewById(R.id.adsBookLayout);

                LinearLayout bookLayout = (LinearLayout) convertView.findViewById(R.id.bookLayout);

                if(book.isAds()){
                    bookLayout.setVisibility(View.GONE);
//                    adsBookLayout.setVisibility(View.VISIBLE);
//
//                    if(adsBookLayout != null && adsBookLayout.getChildCount() == 0){
//                        AdView adView = new AdView(context);
//                        adView.setAdSize(AdSize.LARGE_BANNER);
//                        adView.setAdUnitId("ca-app-pub-1872225169177247/2541119276");
//
//                        adsBookLayout.addView(adView);
//                        adsBookLayout.setVisibility(View.VISIBLE);
//                    }
                }
                else{
                    bookLayout.setVisibility(View.VISIBLE);
                    //adsBookLayout.setVisibility(View.GONE);

                    TextView bookTitle = (TextView) convertView.findViewById(R.id.bookTitleView);
                    bookTitle.setText(book.getBookTitle());

                    TextView bookAuthor = (TextView) convertView.findViewById(R.id.bookAuthorView);
                    bookAuthor.setText(book.getAuthor());

                    TextView bookProvider = (TextView) convertView.findViewById(R.id.providerView);
                    bookProvider.setText(book.getProviderSite());

                    ImageView imageView = (ImageView) convertView.findViewById(R.id.bookImageView);
                    //default image
                    imageView.setImageDrawable(book.getLocalImageResource());

                    if(book.isHasFileDownloaded()){
                        imageView.setBackgroundResource(R.color.colorLightBlue);
                    }else{
                        imageView.setBackgroundResource(R.color.colorWhite);
                    }

                    if(book.getRemoteImageUrl() != null){

                        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

                        Integer realWidth = ImageUtils.getRealWidthSize(wm);

                        final int xSize = realWidth*45/100;
                        final int hSize = xSize*3/5;

                        //check if file name exists in book/metadata/
                        if(new File(book.getLocalImageFilePath()).exists()){
                            //select current image
                            Drawable image = Drawable.createFromPath(book.getLocalImageFilePath());

                            if(image != null){
                                //select downloaded image
                                imageView.setImageDrawable(ImageUtils.scaleImage(context, image, xSize, hSize));
                            }
                        }else{
                            final BookAdapter arrayAdapter = this;
                            //download file out of main thread
                            SimpleDownloadTask sdt = new SimpleDownloadTask(context, book.getRemoteImageUrl(), book.getLocalImageFilePath(), new Callable<Integer>() {
                                @Override
                                public Integer call() throws Exception {
                                    arrayAdapter.notifyDataSetChanged();
                                    if(new File(book.getLocalImageFilePath()).exists()){
                                        //select local image

                                        Drawable image = Drawable.createFromPath(book.getLocalImageFilePath());
                                        //size based on screen width
                                        book.setLocalImageResource(ImageUtils.scaleImage(context, image, xSize, hSize));
                                    }
                                    return 0;
                                }
                            });

                            try{
                                sdt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            } catch (Throwable t){
                                t.printStackTrace();
                            }

                            //if downloaded
                            //if(new File(book.getLocalImageFilePath()).exists()){
                            //    Drawable image = Drawable.createFromPath(book.getLocalImageFilePath());
                            //}
                        }
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

                ArrayList<Book> filteredBooks = new ArrayList<Book>();
                for(int i = 0, l = lItems.size(); i < l; i++){
                    Book m = lItems.get(i);
                    if(m.getBookTitle().toLowerCase().contains(constraint)
                            || m.getDescr().toLowerCase().contains(constraint)
                            || m.getAuthor().toLowerCase().contains(constraint))
                        filteredBooks.add(m);
                }

                //set as current dataset
                books = filteredBooks;

                result.count = filteredBooks.size();
                result.values = filteredBooks;
            }
            else
            {
                //if is null execute backup
                if(booksBackup == null){
                    booksBackup = (ArrayList<Book>) books.clone();
                }

                //set as current dataset
                books = (ArrayList<Book>) booksBackup.clone();

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

            if(filtered != null){
                for(int i = 0, l = filtered.size(); i < l; i++){
                    add(filtered.get(i));
                }
            }

            notifyDataSetChanged();
        }
    }
}
