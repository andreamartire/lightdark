package newtech.audiolibrary.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import newtech.audiolibrary.R;

public class ImageUtils {

    static ArrayList<Drawable> images;

    public static Drawable getRandomDefaultImage(Context context) {
        if(images == null){
            images = new ArrayList<>();
            images.add(context.getResources().getDrawable(R.drawable.book1_small));
            images.add(context.getResources().getDrawable(R.drawable.book2_small));
            images.add(context.getResources().getDrawable(R.drawable.book3_small));
            images.add(context.getResources().getDrawable(R.drawable.book4_small));
            images.add(context.getResources().getDrawable(R.drawable.book5_small));
            images.add(context.getResources().getDrawable(R.drawable.book6_small));
            images.add(context.getResources().getDrawable(R.drawable.book7_small));
            images.add(context.getResources().getDrawable(R.drawable.book8_small));
            images.add(context.getResources().getDrawable(R.drawable.book9_small));
        }
        Collections.shuffle(images, new Random(System.nanoTime()));

        return images.get(0);
    }

    public static boolean isValidUri(String uri) {
        final URL url;
        try {
            url = new URL(uri);
        } catch (Exception e1) {
            return false;
        }
        return "http".equalsIgnoreCase(url.getProtocol()) || "https".equalsIgnoreCase(url.getProtocol());
    }

    public static Drawable scaleImage(Context context, Drawable image, int width, int length){
        Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
        return new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap, width, length, true));
    }
}
