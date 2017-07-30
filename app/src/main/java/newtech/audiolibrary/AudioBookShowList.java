package newtech.audiolibrary;

/**
 * Created by andrea on 18/07/17.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AudioBookShowList extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audiobooks);
        ListView listView = (ListView)findViewById(R.id.audiobooks_listview);

        List<String> bookTitles = new ArrayList<>();
        Map<String,List<String>> chaptersByTitle = new HashMap<String,List<String>>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("config.json")));

            String message = org.apache.commons.io.IOUtils.toString(reader);

            JsonParser parser = new JsonParser();
            JsonObject o = parser.parse(message).getAsJsonObject();

            JsonElement audioBooks = o.get("audiobooks");
            JsonArray audioBooksArray = audioBooks.getAsJsonArray();

            for (int i = 0 ; i < audioBooksArray.size(); i++) {
                JsonObject obj = audioBooksArray.get(i).getAsJsonObject();

                JsonElement firstElBook = obj.get("contents").getAsJsonArray().get(0);
                String title = firstElBook.getAsJsonObject().get("title").getAsString();

                bookTitles.add(title);

                //iterate over chapters
                if(!chaptersByTitle.containsKey(title)){
                    chaptersByTitle.put(title, new LinkedList<String>());
                }

                Iterator it = obj.get("contents").getAsJsonArray().iterator();
                while (it.hasNext()){
                    JsonElement chapter = (JsonElement) it.next();
                    chaptersByTitle.get(title).add(chapter.getAsJsonObject().get("title").getAsString());
                }
            }

        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }

        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this, R.layout.single_book, R.id.bookView, bookTitles);
        listView.setAdapter(arrayAdapter);

        //init tap listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int i, long l){
                System.out.println("i " + adapterView.getItemAtPosition(i));
                System.out.println("l " + l);
            }
        });
    }

    private void readConfigFile() {

    }
}