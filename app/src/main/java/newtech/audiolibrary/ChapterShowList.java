package newtech.audiolibrary;

/**
 * Created by andrea on 24/08/17.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
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
import newtech.audiolibrary.stream.ChapterPlayStreamButton;

public class ChapterShowList extends Activity {

    public static String CHAPTERS = "CHAPTERS";
    public static String TITLE = "TITLE";

    public static MediaPlayer mediaPlayer = new MediaPlayer();

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

    public void myClickHandler(View v) {
        //get the row the clicked button is in
        LinearLayout linearLayout = (LinearLayout)v.getParent();

        ChapterPlayStreamButton playStreamButton = (ChapterPlayStreamButton) v;

        //manage tap on chapter's list
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

        TextView title = (TextView) linearLayout.findViewById(R.id.chapterTitle);

        Chapter currentChapter = playStreamButton.getChapter();

        builder.setMessage(currentChapter.getUrl())
                .setTitle("Play " + currentChapter.getTitle())
                .setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mediaPlayer.stop();
                mediaPlayer.release(); // release resources
            }
        });
        dialog.show();

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(currentChapter.getUrl());
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}