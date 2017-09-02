package newtech.audiolibrary;

/**
 * Created by andrea on 24/08/17.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
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
    public static String TITLE = "TITLE";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapters);

        ListView chaptersListView = (ListView)findViewById(R.id.chapters_listview);

        String title = (String) getIntent().getSerializableExtra(TITLE);
        ArrayList<Chapter> chapters = (ArrayList<Chapter>) getIntent().getSerializableExtra(CHAPTERS);

        ArrayAdapter<Chapter> arrayAdapter = new ChapterAdapter(this.getBaseContext(), R.layout.single_chapter, chapters);
        chaptersListView.setAdapter(arrayAdapter);
    }
}