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
import newtech.audiolibrary.bean.Chapter;
import newtech.audiolibrary.stream.ChapterDownloadButton;
import newtech.audiolibrary.stream.ChapterPlayStreamButton;
import newtech.audiolibrary.task.DownloadTask;

public class ChapterShowList extends Activity {

    public static String CHAPTERS = "CHAPTERS";
    public static String BOOK_IMAGE_ID = "BOOK_IMAGE_ID";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapters);
;
        ArrayList<Chapter> chapters = (ArrayList<Chapter>) getIntent().getSerializableExtra(CHAPTERS);

        ArrayAdapter<Chapter> arrayAdapter = new ChapterAdapter(this.getBaseContext(), R.layout.single_chapter, chapters);
        ListView chaptersListView = (ListView) findViewById(R.id.chapters_listview);
        chaptersListView.setAdapter(arrayAdapter);

        int imageResId = (Integer) getIntent().getSerializableExtra(BOOK_IMAGE_ID);
        ImageView bookImage = (ImageView) findViewById(R.id.chapters_bookImageView);
        bookImage.setImageResource(imageResId);

/*        Bitmap bitmap = ((BitmapDrawable)bookImage.getDrawable()).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, 300, 250, false);
        bookImage.setImageDrawable(new BitmapDrawable(getResources(), bitmapResized));*/
    }
}