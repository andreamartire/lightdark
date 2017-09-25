package newtech.audiolibrary.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import newtech.audiolibrary.AudioBookShowList;
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
            if(book.getImageUrl() != null){

                String imageFilePath = book.getImageUrl().getPath();
                String imageFileName = imageFilePath.substring(imageFilePath.lastIndexOf('/') + 1);
                String localImageFile = book.getAppDir() + File.separator + book.getBookTitle() + File.separator +
                        AudioBookShowList.metadata + File.separator + imageFileName;

                //check if file name exists in book/metadata/
                if(new File(localImageFile).exists()){
                    //select current image
                    Drawable image = Drawable.createFromPath(localImageFile);

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
                    SimpleDownloadTask sdt = new SimpleDownloadTask(book.getImageUrl(), localImageFile);
                    sdt.execute();

                    //if downloaded
                    if(new File(localImageFile).exists()){
                        Drawable image = Drawable.createFromPath(localImageFile);

                        //TODO resize image
                    }else{
                        //default image
                        //FIXME
                        Drawable image = context.getResources().getDrawable(book.getImageResId());
                        imageView.setImageDrawable(image);
                    }
                }
            }
            else{
                //default image
                //FIXME
                Drawable image = context.getResources().getDrawable(book.getImageResId());
                imageView.setImageDrawable(image);
            }
        }

        return convertView;
    }
}
