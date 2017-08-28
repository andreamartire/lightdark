package newtech.audiolibrary.stream;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.widget.Button;

import newtech.audiolibrary.bean.Chapter;

/**
 * Created by MartireAn on 28/08/2017.
 */

public class ChapterPlayStreamButton extends AppCompatButton{

    Chapter chapter;

    public ChapterPlayStreamButton(Context context) {
        this(context, null);
    }

    public ChapterPlayStreamButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChapterPlayStreamButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Chapter getChapter() {
        return chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }
}
