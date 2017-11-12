package techbrain.libroparlante.buttons;

import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.View;

import techbrain.libroparlante.ChapterPlayer;
import techbrain.libroparlante.bean.Chapter;

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

                ChapterPlayer.startPlayer(currentContext, currentChapter);
            }
        });
    }
}
