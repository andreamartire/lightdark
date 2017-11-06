package newtech.audiolibrary;

/**
 * Created by andrea on 18/07/17.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.Serializable;

import newtech.audiolibrary.adapters.BookAdapter;
import newtech.audiolibrary.adapters.PlayThread;
import newtech.audiolibrary.bean.Book;
import newtech.audiolibrary.bean.Chapter;
import newtech.audiolibrary.task.SimpleDownloadTask;
import newtech.audiolibrary.utils.ConfigUtils;
import newtech.audiolibrary.utils.ImageUtils;
import newtech.audiolibrary.utils.MyFileUtils;

public class TestAds extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audiobooks);

        MobileAds.initialize(this, "ca-app-pub-1872225169177247~3010272652");
    }
}