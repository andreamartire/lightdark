package newtech.audiolibrary.buttons;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import newtech.audiolibrary.R;
import newtech.audiolibrary.bean.Chapter;
import newtech.audiolibrary.utils.MyFileUtils;

/**
 * Created by MartireAn on 28/08/2017.
 */

public class ChapterDeleteButton extends AppCompatImageButton {

    Chapter chapter;

    public ChapterDeleteButton(Context context) {
        this(context, null);
        setDeleteHandler();
    }

    public ChapterDeleteButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        setDeleteHandler();
    }

    public ChapterDeleteButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDeleteHandler();
    }

    public Chapter getChapter() {
        return chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }

    public void setDeleteHandler(){

        final Context currentContext = this.getContext();

        this.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //get the row the clicked button is in
                final LinearLayout linearLayout = (LinearLayout)v.getParent();

                ChapterDeleteButton deleteButton = (ChapterDeleteButton) v;
                final Chapter currentChapter = deleteButton.getChapter();

                final Dialog dialog = new Dialog(v.getContext(),R.style.CustomDialogTheme);
                dialog.getWindow();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.confirm_delete);
                dialog.setCancelable(false);

                Button confirmBtn = (Button) dialog.findViewById(R.id.confirmDeleteBtn);
                Button cancelBtn = (Button) dialog.findViewById(R.id.cancelDeleteBtn);

                confirmBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        dialog.dismiss();
                        MyFileUtils.deleteFileIfExists(currentChapter.getLocalFilePath());

                        //force repaint chapter list view
                        ListView chapterListView = (ListView) linearLayout.getRootView().findViewById(R.id.chapters_listview);
                        final ArrayAdapter adapter = ((ArrayAdapter) chapterListView.getAdapter());
                        adapter.notifyDataSetChanged();
                    }
                });

                cancelBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }
}
