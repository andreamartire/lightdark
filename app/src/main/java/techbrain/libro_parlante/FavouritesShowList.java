package techbrain.libro_parlante;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Config;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import techbrain.libro_parlante.adapters.BookAdapter;
import techbrain.libro_parlante.adapters.PlayThread;
import techbrain.libro_parlante.bean.Book;
import techbrain.libro_parlante.bean.Chapter;
import techbrain.libro_parlante.bean.Favourites;
import techbrain.libro_parlante.task.SimpleDownloadTask;
import techbrain.libro_parlante.utils.AppRater;
import techbrain.libro_parlante.utils.ConfigUtils;
import techbrain.libro_parlante.utils.ImageUtils;
import techbrain.libro_parlante.utils.MyFileUtils;

public class FavouritesShowList extends AppCompatActivity {

    public static String FAVOURITES_FILE = "favourites.json";
    public static String METADATA = ".metadata";

    BookAdapter bookAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.favouriteToolbar);
        myToolbar.showOverflowMenu();
        setSupportActionBar(myToolbar);

        ListView listView = (ListView)findViewById(R.id.favourites_listview);

        bookAdapter = new BookAdapter(this, R.layout.single_book, ConfigUtils.favouriteBooks);
        listView.setAdapter(bookAdapter);
        //init tap listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int i, long l){
            //manage tap on audiobook list
            Intent intent = new Intent(v.getContext(), ChapterShowList.class);

            Book book = (Book) adapterView.getItemAtPosition(i);

            //pass data thought intent to another activity
            intent.putExtra(ChapterShowList.BOOK, (Serializable) book);
            intent.putExtra(ChapterShowList.CHAPTERS, (Serializable) book.getChapters());

            startActivity(intent);
            }
        });
    }

    public static void saveFavourites(Context context) {
        try{
            String metadataFilePath = context.getFilesDir().getAbsolutePath() + File.separator + METADATA + File.separator;
            MyFileUtils.mkDir(metadataFilePath);

            String favouriteFilePath = metadataFilePath + File.separator + FAVOURITES_FILE;

            MyFileUtils.deleteFileIfExists(favouriteFilePath);

            Favourites favourites = new Favourites();
            favourites.setBooks(ConfigUtils.favouriteBooks);

            String jsonStr = new Gson().toJson(favourites);

            FileWriter fw = new FileWriter(favouriteFilePath);
            fw.write(jsonStr);
            fw.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void loadFavourites(Context context) {
        if (context != null && context.getFilesDir() != null && context.getFilesDir().getAbsolutePath() != null) {
            String metadataFilePath = context.getFilesDir().getAbsolutePath() + File.separator + METADATA + File.separator;
            String favouritesFilePath = metadataFilePath + File.separator + FAVOURITES_FILE;

            try {
                Favourites favourites = new Gson().fromJson(new FileReader(favouritesFilePath), Favourites.class);
                System.out.println("Loaded favourites: " + favourites);
                ConfigUtils.favouriteBooks.clear();

                if(favourites != null && favourites.getBooks() != null){
                    for(Book favBook : favourites.getBooks()){
                        Book favBookWithChapters = ConfigUtils.getBookWithChapters(favBook);
                        if(favBookWithChapters != null){
                            ConfigUtils.favouriteBooks.add(favBookWithChapters);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavourites(this);

        if(bookAdapter != null){
            bookAdapter.notifyDataSetChanged();
        }
    }
}