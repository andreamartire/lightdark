package newtech.audiolibrary.player;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import newtech.audiolibrary.ChapterPlayer;
import newtech.audiolibrary.R;
import newtech.audiolibrary.bean.Chapter;

/**
 * Created by MartireAn on 06/09/2017.
 */

public class PlayPauseButton extends AppCompatButton {

    public PlayPauseButton(Context context) {
        this(context, null);
        this.setClickListener();
    }

    public PlayPauseButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.setClickListener();
    }

    public PlayPauseButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setClickListener();
    }

    public void setClickListener(){

        final Context currentContext = this.getContext();

        final TextView currentDurationView = (TextView) findViewById(R.id.playChapter_currentDuration);

        this.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick (View v) {

                if(ChapterPlayer.mediaPlayer.isPlaying()){
                    ChapterPlayer.mediaPlayer.pause();
                }else{
                    ChapterPlayer.mediaPlayer.start();
                    //TODO schedule refresh duration
                    //currentDurationView.post(ChapterPlayer.updatePlayerCurrentDuration);
                }
            }
        });
    }
}
