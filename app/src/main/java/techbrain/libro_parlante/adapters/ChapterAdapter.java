package techbrain.libro_parlante.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import techbrain.libro_parlante.R;
import techbrain.libro_parlante.bean.Chapter;
import techbrain.libro_parlante.buttons.ChapterDeleteButton;
import techbrain.libro_parlante.buttons.ChapterDownloadButton;
import techbrain.libro_parlante.buttons.ChapterPlayStreamButton;
import techbrain.libro_parlante.utils.MyFileUtils;

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

        try {
            final Chapter chapter = chapters.get(position);

            if (chapter != null){
                if (convertView == null){
                    LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = vi.inflate(R.layout.single_chapter, null);
                }

                final ChapterPlayStreamButton playStreamButton = (ChapterPlayStreamButton) convertView.findViewById(R.id.playButton);
                playStreamButton.setChapter(chapter);

                final ChapterDownloadButton downloadButton = (ChapterDownloadButton) convertView.findViewById(R.id.downloadButton);
                downloadButton.setChapter(chapter);

                ChapterDeleteButton deleteButton = (ChapterDeleteButton) convertView.findViewById(R.id.deleteButton);
                deleteButton.setChapter(chapter);

                TextView chapterTitle = (TextView) convertView.findViewById(R.id.chapterTitle);
                chapterTitle.setText(chapter.getChapterTitle());
                chapterTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!MyFileUtils.exists(chapter.getLocalFilePath())){
                            downloadButton.callOnClick();
                        }else{
                            playStreamButton.callOnClick();
                        }
                    }
                });

                if(!chapter.isDownloading()){
                    if(chapter.existsLocalFile()){
                        playStreamButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                        playStreamButton.setVisibility(View.VISIBLE);
                        downloadButton.setVisibility(View.GONE);
                        deleteButton.setVisibility(View.VISIBLE);
                    } else {
                        playStreamButton.setVisibility(View.GONE);
                        downloadButton.setVisibility(View.VISIBLE);
                        deleteButton.setVisibility(View.GONE);
                    }
                }else{
                    playStreamButton.setVisibility(View.GONE);
                    downloadButton.setVisibility(View.GONE);
                    deleteButton.setVisibility(View.GONE);
                }
            }
        }catch (Throwable t){
            t.printStackTrace();
        }

        return convertView;
    }
}
