package newtech.audiolibrary.buttons;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import newtech.audiolibrary.R;
import newtech.audiolibrary.bean.Chapter;
import newtech.audiolibrary.task.DownloadTask;

/**
 * Created by MartireAn on 28/08/2017.
 */

public class ChapterDownloadButton extends AppCompatImageButton {

    Chapter chapter;

    public ChapterDownloadButton(Context context) {
        this(context, null);
        setDownloadHandler();
    }

    public ChapterDownloadButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        setDownloadHandler();
    }

    public ChapterDownloadButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDownloadHandler();
    }

    public Chapter getChapter() {
        return chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }

    public void setDownloadHandler(){

        final Context currentContext = this.getContext();

        this.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //get the row the clicked button is in
                ChapterDownloadButton downloadButton = (ChapterDownloadButton) v;

                //manage tap on chapter's list
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(),R.style.CustomDialogTheme);

                Chapter currentChapter = downloadButton.getChapter();

                try {
                    //execute download

                    // instantiate it within the onCreate method
                    ProgressDialog mProgressDialog = new ProgressDialog(currentContext);
                    mProgressDialog.setIndeterminate(true);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mProgressDialog.setCancelable(true);
                    mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                    // execute this when the downloader must be fired
                    final DownloadTask downloadTask = new DownloadTask(currentContext, mProgressDialog, currentChapter, v);

                    downloadTask.execute();

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
        });
    }
}
