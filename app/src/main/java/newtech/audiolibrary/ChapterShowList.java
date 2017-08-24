package newtech.audiolibrary;

/**
 * Created by andrea on 24/08/17.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
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

public class ChapterShowList extends Activity {

    public static String CHAPTERS = "CHAPTERS";
    public static String TITLE = "TITLE";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapters);
        ListView chaptersListView = (ListView)findViewById(R.id.chapters_listview);

        Intent intent = getIntent();
        String title = (String) intent.getSerializableExtra(TITLE);
        List<String> chapters = (List<String>) intent.getSerializableExtra(CHAPTERS);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.single_chapter, R.id.chapterView, chapters);
        chaptersListView.setAdapter(arrayAdapter);

        //init tap listener
        chaptersListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int i, long l){
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage((String) adapterView.getItemAtPosition(i)).setTitle("Play").setCancelable(true);
                AlertDialog dialog = builder.create();
                dialog.show();
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
}