package newtech.audiolibrary;

/**
 * Created by andrea on 24/08/17.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

import newtech.audiolibrary.adapters.ChapterAdapter;
import newtech.audiolibrary.bean.Book;
import newtech.audiolibrary.bean.Chapter;
import newtech.audiolibrary.stream.ChapterPlayStreamButton;
import newtech.audiolibrary.utils.ImageUtils;

public class ChapterShowList extends Activity {

    public static String BOOK = "BOOK";
    public static String CHAPTERS = "CHAPTERS";
    public static String PLAYING_CHAPTER = "PLAYING_CHAPTER";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapters);

        final Context currentContext = this;

        Book book = (Book) getIntent().getSerializableExtra(BOOK);

        ArrayList<Chapter> chapters = (ArrayList<Chapter>) getIntent().getSerializableExtra(CHAPTERS);

        ArrayAdapter<Chapter> arrayAdapter = new ChapterAdapter(this.getBaseContext(), R.layout.single_chapter, chapters);
        ListView chaptersListView = (ListView) findViewById(R.id.chapters_listview);
        chaptersListView.setAdapter(arrayAdapter);
        /*chaptersListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
                ChapterPlayStreamButton playStreamButton = (ChapterPlayStreamButton) view.findViewById(R.id.playButton);

                Chapter currentChapter = playStreamButton.getChapter();

                if(currentChapter != null && currentChapter.existsLocalFile()){
                    ChapterPlayer.startPlayer(currentContext, currentChapter);
                }else{
                    Toast.makeText(currentContext, "Effettua prima il download del capitolo", Toast.LENGTH_LONG).show();
                }
            }
        });*/

        String localFileName = book.getLocalImageFilePath();
        ImageView bookImageView = (ImageView) findViewById(R.id.chapters_bookImageView);

        Drawable bookImage = Drawable.createFromPath(localFileName);
        if(bookImage == null){
            //TODO default image not found
        }else{
            Point size = new Point();
            getWindowManager().getDefaultDisplay().getSize(size);

            bookImageView.setImageDrawable(ImageUtils.scaleImage(this, bookImage, size.x, (int) size.x*3/5));
        }

        Chapter playingChapter = (Chapter) getIntent().getSerializableExtra(PLAYING_CHAPTER);
        if(playingChapter != null){
            //resume old playing chapter
            ChapterPlayer.startPlayer(this, playingChapter);
        }
    }
}