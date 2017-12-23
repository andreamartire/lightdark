package techbrain.libro_parlante;

/**
 * Created by andrea on 18/07/17.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.ShareActionProvider;
import android.view.ActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Locale;

import techbrain.libro_parlante.R;
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

public class AudioBookShowList extends AppCompatActivity {

    private BookAdapter bookAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Context me = this;

        switch (item.getItemId()) {
            case R.id.shareElement:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");

                String shareBodyText = getResources().getString(R.string.share_message);

                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                startActivity(Intent.createChooser(sharingIntent, "Share"));
                return true;

            case R.id.favourites:
                Intent intent = new Intent(me, FavouritesShowList.class);

                FavouritesShowList.loadFavourites(this);
                startActivity(intent);

                return true;

            case R.id.infoElement:
                //Toast.makeText(me, "Tutti i contenuti audio e le immagini sono liberamente accessibili in rete e scaricati direttamente dai siti web dei rispettivi possessori dei diritti. Nessun contenuto è ospitato su server dell'applicazione", Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(me);
                builder.setMessage(R.string.info_message)
                        .setTitle(R.string.info_title);

                AlertDialog dialog = builder.create();
                dialog.show();
                return true;

            case R.id.contactElement:
                String email = getResources().getString(R.string.contact_email);
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",email, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Libro Parlante");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audiobooks);

        String currLang = Locale.getDefault().getLanguage();
        if("it".equalsIgnoreCase(currLang)){
            AppRater.app_launched(this);
        }

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.showOverflowMenu();
        setSupportActionBar(myToolbar);

        final Context me = this;

        final SearchView searchView = (SearchView) findViewById(R.id.searchView);

        ListView listView = (ListView)findViewById(R.id.audiobooks_listview);

        ViewGroup myHeader = (ViewGroup) getLayoutInflater().inflate(R.layout.resume_book, listView, false);
        listView.addHeaderView(myHeader, null, false);

        final LinearLayout currentPlayingInfo = (LinearLayout) findViewById(R.id.currentPlayingInfo);

        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                bookAdapter.getFilter().filter(query);
                bookAdapter.notifyDataSetChanged();
                currentPlayingInfo.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                bookAdapter.getFilter().filter(newText);
                if(newText != null && newText.length() > 0){
                    currentPlayingInfo.setVisibility(View.GONE);
                }else{
                    currentPlayingInfo.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        bookAdapter = new BookAdapter(this, R.layout.single_book, ConfigUtils.bookList);
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

            //TODO reset focus
            searchView.clearFocus();
            }
        });

        final ImageView playingBookImage = (ImageView) findViewById(R.id.currentPlayingBookImage);
        playingBookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Chapter playingChapter = PlayThread.getPlayerState(me);

            if(playingChapter != null){
                Book currentBook = playingChapter.getBook();

                //fetch chapters - not saved in player state
                currentBook = ConfigUtils.bookWithChapters.get(currentBook.getBookDir());

                Intent intent = new Intent(me, ChapterShowList.class);

                //pass data thought intent to another activity
                intent.putExtra(ChapterShowList.BOOK, currentBook);
                intent.putExtra(ChapterShowList.CHAPTERS, currentBook.getChapters());
                intent.putExtra(ChapterShowList.PLAYING_CHAPTER, playingChapter);

                me.startActivity(intent);
            }
            }
        });

        checkCurrentPlayingState();

        MobileAds.initialize(this, "ca-app-pub-1872225169177247~3010272652");

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkCurrentPlayingState();

        final SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setFocusable(false);
        searchView.setQuery("", false);
    }

    private void checkCurrentPlayingState() {
        Chapter oldPlayerState = PlayThread.getPlayerState(this);

        LinearLayout currPlayingInfo = (LinearLayout) this.findViewById(R.id.currentPlayingInfo);;

        if(oldPlayerState != null){
            if(MyFileUtils.exists(oldPlayerState.getLocalFilePath())){
                //Toast.makeText(this, "Player was playing: " + oldPlayerState.getFileName() + " at duration: " + oldPlayerState.getCurrentDuration() + "/" + oldPlayerState.getTotalDuration(), Toast.LENGTH_LONG).show();
                currPlayingInfo.setVisibility(View.VISIBLE);

                RelativeLayout currPlayingInfoRelative = (RelativeLayout) this.findViewById(R.id.currentPlayingInfoRelative);
                currPlayingInfoRelative.setVisibility(View.VISIBLE);

                TextView playingBookTitle = (TextView) this.findViewById(R.id.currentPlayingBookTitle);
                playingBookTitle.setText(oldPlayerState.getBook().getBookTitle());
                playingBookTitle.setVisibility(View.VISIBLE);

                double bookPercentage = oldPlayerState.getBook().getBookPlayerPercentage(oldPlayerState);
                ProgressBar progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
                progressBar.setProgress((int) bookPercentage);
                progressBar.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                String localImageFileName = oldPlayerState.getBook().getLocalImageFileName();
                Drawable image = oldPlayerState.getBook().getLocalImageResource();

                if(localImageFileName != null){
                    String localFileImage = oldPlayerState.getBook().getLocalImageFilePath();
                    /*if(new File(localFileImage).exists()){
                        //set file image
                        //select current image
                        Drawable imageFromLocalFile = Drawable.createFromPath(localFileImage);

                        if(image != null) {
                            //select local drawable
                            image = imageFromLocalFile;
                        }else{
                            Book book = oldPlayerState.getBook();

                            //execute asynch download
                            SimpleDownloadTask sdt = new SimpleDownloadTask(this, book.getRemoteImageUrl(), book.getLocalImageFilePath(), null);
                            try{
                                sdt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            } catch (Throwable t){
                                t.printStackTrace();
                            }
                        }
                    }else{
                        Book book = oldPlayerState.getBook();

                        //execute asynch download
                        SimpleDownloadTask sdt = new SimpleDownloadTask(this, book.getRemoteImageUrl(), book.getLocalImageFilePath(), null);
                        try{
                            sdt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } catch (Throwable t){
                            t.printStackTrace();
                        }
                    }*/
                }

                Integer realWidth = ImageUtils.getRealWidthSize(getWindowManager());
                int customWidth = realWidth;
                int customHeight = customWidth*3/5;

                ImageView resumeBookImageView = (ImageView) this.findViewById(R.id.currentPlayingBookImage);

                //select downloaded image
                resumeBookImageView.setImageDrawable(ImageUtils.scaleImage(this, image, customWidth, customHeight));
                resumeBookImageView.setVisibility(View.VISIBLE);
            }
            else{
                //Toast.makeText(this, "Old playing chapter was deleted", Toast.LENGTH_LONG).show();
                currPlayingInfo.setVisibility(View.GONE);
            }
        }else{
            // TODO no book playing
            View playingInfo = this.findViewById(R.id.currentPlayingInfo);
            playingInfo.setVisibility(View.GONE);
        }
    }
}