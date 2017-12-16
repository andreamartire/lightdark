package techbrain.libro_parlante.buttons;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import techbrain.libro_parlante.bean.Chapter;
import techbrain.libro_parlante.task.DownloadTask;

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

            ProgressBar downloadProgress = null;

            ViewGroup row = (ViewGroup) v.getParent();
            for (int itemPos = 0; itemPos < row.getChildCount(); itemPos++) {
                View view = row.getChildAt(itemPos);
                if (view instanceof ProgressBar) {
                    downloadProgress = (ProgressBar) view; //Found it!
                    downloadProgress.setVisibility(VISIBLE);
                    downloadProgress.setProgress(0);
                    downloadButton.setVisibility(GONE);
                    break;
                }
            }

            //manage tap on chapter's list
            //AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(),R.style.CustomDialogTheme);

            Chapter currentChapter = downloadButton.getChapter();

            try{
                if(!currentChapter.isDownloading()){
                    //execute download
                    currentChapter.setDownloading(true);

                    // execute this when the downloader must be fired
                    final DownloadTask downloadTask = new DownloadTask(currentContext, downloadProgress, currentChapter, v, null);

                    downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }catch (Throwable t){
                t.printStackTrace();
            }
            }
        });
    }
}
