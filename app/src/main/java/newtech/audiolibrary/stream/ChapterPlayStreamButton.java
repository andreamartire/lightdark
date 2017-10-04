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
                ChapterPlayStreamButton playStreamButton = (ChapterPlayStreamButton) v;

                Chapter currentChapter = playStreamButton.getChapter();

                //TODO get linked chapter

                ChapterPlayer.startPlayer(currentContext, currentChapter);
            }
        });
    }
}
