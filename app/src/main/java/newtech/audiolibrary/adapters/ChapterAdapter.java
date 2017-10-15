package newtech.audiolibrary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import newtech.audiolibrary.R;
import newtech.audiolibrary.bean.Chapter;
import newtech.audiolibrary.buttons.ChapterDeleteButton;
import newtech.audiolibrary.buttons.ChapterDownloadButton;
import newtech.audiolibrary.buttons.ChapterPlayStreamButton;

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
            chapterTitle.setText(chapter.getChapterTitle());

            ChapterPlayStreamButton playStreamButton = (ChapterPlayStreamButton) convertView.findViewById(R.id.playButton);
            playStreamButton.setChapter(chapter);

            ChapterDownloadButton downloadButton = (ChapterDownloadButton) convertView.findViewById(R.id.downloadButton);
            downloadButton.setChapter(chapter);

            ChapterDeleteButton deleteButton = (ChapterDeleteButton) convertView.findViewById(R.id.deleteButton);
            deleteButton.setChapter(chapter);

            if(chapter.existsLocalFile()){
                playStreamButton.setImageResource(R.drawable.ic_play_arrow_black_72dp);
                playStreamButton.setVisibility(View.VISIBLE);
                downloadButton.setVisibility(View.GONE);
                deleteButton.setVisibility(View.VISIBLE);
            } else {
                //TODO
                //playStreamButton.setImageResource(R.drawable.ic_subscriptions_black_72dp);
                playStreamButton.setVisibility(View.GONE);
                downloadButton.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.GONE);
            }
        }

        return convertView;
    }
}
