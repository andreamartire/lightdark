package newtech.audiolibrary;

/**
 * Created by andrea on 24/08/17.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import newtech.audiolibrary.adapters.ChapterAdapter;
import newtech.audiolibrary.bean.Chapter;

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

        View singleChapterView = this.getLayoutInflater().inflate(R.layout.single_chapter, chaptersListView, false);
        Button playButton = (Button) singleChapterView.findViewById(R.id.playButton);
    }

    public void myClickHandler(View v) {
        //get the row the clicked button is in
        LinearLayout linearLayout = (LinearLayout)v.getParent();

        //manage tap on chapter's list
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

        TextView title = (TextView) linearLayout.findViewById(R.id.chapterTitle);


        builder.setMessage(title.getText()).setTitle("Play " + title.getText()).setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mediaPlayer.stop();
                mediaPlayer.release(); // release resources
            }
        });
        dialog.show();

//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        try {
//            mediaPlayer.setDataSource(chapter.getUrl());
//            mediaPlayer.prepare(); // might take long! (for buffering, etc)
//            mediaPlayer.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private List<String> getTitles(List<Chapter> chapters) {
        List<String> titles = new LinkedList<String>();
        if(chapters != null){
            for(Chapter c : chapters){
                titles.add(c.getTitle());
            }
        }
        return titles;
    }
}