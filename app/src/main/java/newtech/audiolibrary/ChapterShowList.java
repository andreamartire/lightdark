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
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
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
    public static String MP3_EXTENSION = ".mp3";

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

    public void myClickPlayHandler(View v) {
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
            if(currentChapter.getLocalFile() == null){
                //stream from web
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(currentChapter.getUrl());
                mediaPlayer.prepare(); // might take long! (for buffering, etc)
            }else{
                //play local resource
                mediaPlayer = MediaPlayer.create(this, Uri.parse(currentChapter.getLocalFile()));
                mediaPlayer.setLooping(false);
            }

            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // declare the dialog as a member field of your activity
    ProgressDialog mProgressDialog;

    public void myClickDownloadHandler(View v) {
        //get the row the clicked button is in
        LinearLayout linearLayout = (LinearLayout)v.getParent();

        ChapterDownloadButton downloadButton = (ChapterDownloadButton) v;

        //manage tap on chapter's list
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

        TextView title = (TextView) linearLayout.findViewById(R.id.chapterTitle);

        Chapter currentChapter = downloadButton.getChapter();

        builder.setMessage(currentChapter.getUrl())
                .setTitle("Download " + currentChapter.getTitle())
                .setCancelable(true);

        try {
            //execute download

            // instantiate it within the onCreate method
            ProgressDialog mProgressDialog = new ProgressDialog(ChapterShowList.this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setMessage(currentChapter.getUrl());
            mProgressDialog.setTitle("Download " + currentChapter.getTitle());

            // execute this when the downloader must be fired
            final DownloadTask downloadTask = new DownloadTask(ChapterShowList.this, mProgressDialog);
            String dirPath = this.getBaseContext().getFilesDir() + File.separator + currentChapter.getBookTitle();
            String fileName = currentChapter.getTitle() + MP3_EXTENSION;
            downloadTask.execute(currentChapter.getUrl(), dirPath, fileName);

            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    downloadTask.cancel(true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}