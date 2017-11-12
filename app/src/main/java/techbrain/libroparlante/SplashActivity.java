package techbrain.libroparlante;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import newtech.audiolibrary.R;
import techbrain.libroparlante.utils.ConfigUtils;

/**
 * Created by andrea on 18/10/17.
 */

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        final Activity me = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                ConfigUtils.invoke(me, "config.json");
                SplashActivity.this.finish();

                Intent intent = new Intent(SplashActivity.this, AudioBookShowList.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, 0);
                overridePendingTransition(0,0);
            }
        }).start();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}