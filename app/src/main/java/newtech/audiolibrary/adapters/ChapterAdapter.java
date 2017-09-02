package newtech.audiolibrary.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import newtech.audiolibrary.R;
import newtech.audiolibrary.bean.Chapter;
import newtech.audiolibrary.stream.ChapterDownloadButton;
import newtech.audiolibrary.stream.ChapterPlayStreamButton;

/**
 * Created by MartireAn on 27/08/2017.
 */

public class ChapterAdapter extends ArrayAdapter<Chapter> {

    private Context context;
    private int resource;
    private ArrayList<Chapter> chapters;

    public ChapterAdapter(Context context, int resource, ArrayList<Chapter> chapters) {
        super(context, resource, chapters);
        this.context = context;
        this.resource = resource;
        this.chapters = chapters;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        Chapter chapter = chapters.get(position);

        if (chapter != null){
            if (convertView == null){
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.single_chapter, null);
            }

            TextView chapterTitle = (TextView) convertView.findViewById(R.id.chapterTitle);
            chapterTitle.setText(chapter.getTitle());

            ChapterPlayStreamButton playStreamButton = (ChapterPlayStreamButton) convertView.findViewById(R.id.playButton);
            playStreamButton.setChapter(chapter);

            if(chapter.existsLocalFile()){
                playStreamButton.setText("PLAY");
            }

            ChapterDownloadButton downloadButton = (ChapterDownloadButton) convertView.findViewById(R.id.downloadButton);
            downloadButton.setChapter(chapter);
        }

        return convertView;
    }
}
