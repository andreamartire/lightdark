package newtech.audiolibrary.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import newtech.audiolibrary.R;

public class ImageUtils {

    public static Drawable getRandomDefaultImage(Context context) {
        ArrayList<Integer> images = new ArrayList<>();
        images.add(R.drawable.book1_small);
        images.add(R.drawable.book2_small);
        images.add(R.drawable.book3_small);
        images.add(R.drawable.book4_small);
        images.add(R.drawable.book5_small);
        images.add(R.drawable.book6_small);
        images.add(R.drawable.book7_small);
        images.add(R.drawable.book8_small);
        images.add(R.drawable.book9_small);
        Collections.shuffle(images, new Random(System.nanoTime()));

        return context.getResources().getDrawable(images.get(0));
    }
}