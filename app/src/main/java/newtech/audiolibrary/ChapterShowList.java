package newtech.audiolibrary;

/**
 * Created by andrea on 24/08/17.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import newtech.audiolibrary.bean.Chapter;

public class ChapterShowList extends Activity {

    public static String CHAPTERS = "CHAPTERS";
    public static String TITLE = "TITLE";

    private MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapters);
        ListView chaptersListView = (ListView)findViewById(R.id.chapters_listview);

        Intent intent = getIntent();
        String title = (String) intent.getSerializableExtra(TITLE);
        List<Chapter> chapters = (List<Chapter>) intent.getSerializableExtra(CHAPTERS);

        ArrayAdapter<Chapter> arrayAdapter = new ArrayAdapter<Chapter>(this, R.layout.single_chapter, R.id.chapterView, chapters);
        chaptersListView.setAdapter(arrayAdapter);

        //init tap listener
        chaptersListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int i, long l){
                //manage tap on chapter's list
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                Chapter chapter = (Chapter) adapterView.getItemAtPosition(i);

                builder.setMessage(chapter.getUrl()).setTitle("Play " + chapter.getTitle()).setCancelable(true);
                AlertDialog dialog = builder.create();
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                });
                dialog.show();


                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mediaPlayer.setDataSource(chapter.getUrl());
                    mediaPlayer.prepare(); // might take long! (for buffering, etc)
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
            }
        });

        //show dialog
        /*

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Tap Id: " + chapters).setTitle(title).setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();

        */
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