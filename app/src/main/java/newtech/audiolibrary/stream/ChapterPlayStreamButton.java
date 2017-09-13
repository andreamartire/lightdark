package newtech.audiolibrary.stream;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.Serializable;

import newtech.audiolibrary.ChapterPlayer;
import newtech.audiolibrary.ChapterShowList;
import newtech.audiolibrary.R;
import newtech.audiolibrary.bean.Chapter;

/**
 * Created by MartireAn on 28/08/2017.
 */

public class ChapterPlayStreamButton extends AppCompatImageButton {

    Chapter chapter;

    public static MediaPlayer mediaPlayer = new MediaPlayer();

    public ChapterPlayStreamButton(Context context) {
        this(context, null);
        this.setClickListener();
    }

    public ChapterPlayStreamButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.setClickListener();
    }

    public ChapterPlayStreamButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setClickListener();
    }

    public Chapter getChapter() {
        return chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }

    public void setClickListener(){

        final Context currentContext = this.getContext();

        this.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick (View v) {
                //get the row the clicked button is in
                LinearLayout linearLayout = (LinearLayout) v.getParent();

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
                    if (!currentChapter.existsLocalFile()) {
                        //stream from web
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.setDataSource(currentChapter.getUrl());
                        mediaPlayer.prepare(); // might take long! (for buffering, etc)
                    } else {
                        //play local resource
                        String localFilePath = currentChapter.getLocalFilePath();
                        mediaPlayer = MediaPlayer.create(currentContext, Uri.parse(localFilePath));
                        mediaPlayer.setLooping(false);
                    }

                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //manage chapter play
                Intent intent = new Intent(v.getContext(), ChapterPlayer.class);

                //pass data thought intent to another activity
                intent.putExtra(ChapterPlayer.CHAPTER, currentChapter);

                currentContext.startActivity(intent);
            }
        });
    }
}
