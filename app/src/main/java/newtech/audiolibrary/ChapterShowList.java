package newtech.audiolibrary;

/**
 * Created by andrea on 24/08/17.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import newtech.audiolibrary.adapters.ChapterAdapter;
import newtech.audiolibrary.bean.Book;
import newtech.audiolibrary.bean.Chapter;
import newtech.audiolibrary.stream.ChapterDownloadButton;
import newtech.audiolibrary.stream.ChapterPlayStreamButton;
import newtech.audiolibrary.task.DownloadTask;

public class ChapterShowList extends Activity {

    public static String BOOK = "BOOK";
    public static String CHAPTERS = "CHAPTERS";
    public static String PLAYING_CHAPTER = "PLAYING_CHAPTER";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapters);

        Book book = (Book) getIntent().getSerializableExtra(BOOK);

        ArrayList<Chapter> chapters = (ArrayList<Chapter>) getIntent().getSerializableExtra(CHAPTERS);

        ArrayAdapter<Chapter> arrayAdapter = new ChapterAdapter(this.getBaseContext(), R.layout.single_chapter, chapters);
        ListView chaptersListView = (ListView) findViewById(R.id.chapters_listview);
        chaptersListView.setAdapter(arrayAdapter);

        String localFileName = book.getLocalImageFilePath();
        ImageView bookImageView = (ImageView) findViewById(R.id.chapters_bookImageView);

        Drawable bookImage = Drawable.createFromPath(localFileName);
        if(bookImage == null){
            //TODO default image not found
        }else{
            Bitmap bitmap = ((BitmapDrawable) bookImage).getBitmap();
            bookImage = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 1000, 600, true));
            bookImageView.setImageDrawable(bookImage);
        }

        Chapter playingChapter = (Chapter) getIntent().getSerializableExtra(PLAYING_CHAPTER);
        if(playingChapter != null){
            //resume old playing chapter
            ChapterPlayStreamButton.startPlayer(this, playingChapter);
        }
    }
}