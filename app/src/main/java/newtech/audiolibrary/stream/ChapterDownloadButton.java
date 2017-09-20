package newtech.audiolibrary.stream;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import newtech.audiolibrary.ChapterShowList;
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
                LinearLayout linearLayout = (LinearLayout)v.getParent();

                ChapterDownloadButton downloadButton = (ChapterDownloadButton) v;

                //manage tap on chapter's list
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                TextView title = (TextView) linearLayout.findViewById(R.id.chapterTitle);

                Chapter currentChapter = downloadButton.getChapter();

                builder.setMessage(currentChapter.getUrl())
                        .setTitle("Download " + currentChapter.getChapterTitle())
                        .setCancelable(true);

                try {
                    //execute download

                    // instantiate it within the onCreate method
                    ProgressDialog mProgressDialog = new ProgressDialog(currentContext);
                    mProgressDialog.setIndeterminate(true);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mProgressDialog.setCancelable(true);
                    mProgressDialog.setMessage(currentChapter.getUrl());
                    mProgressDialog.setTitle("Download " + currentChapter.getChapterTitle());

                    // execute this when the downloader must be fired
                    final DownloadTask downloadTask = new DownloadTask(currentContext, mProgressDialog, currentChapter, v);

                    String bookDir = currentChapter.getBook().getBookTitle();
                    String fileName = currentChapter.getFileName();

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
