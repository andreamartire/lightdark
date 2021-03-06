package techbrain.libro_parlante.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import techbrain.libro_parlante.R;

public class ImageUtils {

    static ArrayList<Drawable> images;

    public static Drawable getRandomDefaultImage(Context context, Integer customWidth, Integer customHeight) {
        if(images == null){
            images = new ArrayList<>();
            images.add(scaleImage(context, context.getResources().getDrawable(R.drawable.book1_small), customWidth, customHeight));
            images.add(scaleImage(context, context.getResources().getDrawable(R.drawable.book2_small), customWidth, customHeight));
            images.add(scaleImage(context, context.getResources().getDrawable(R.drawable.book3_small), customWidth, customHeight));
            images.add(scaleImage(context, context.getResources().getDrawable(R.drawable.book4_small), customWidth, customHeight));
            images.add(scaleImage(context, context.getResources().getDrawable(R.drawable.book5_small), customWidth, customHeight));
            images.add(scaleImage(context, context.getResources().getDrawable(R.drawable.book6_small), customWidth, customHeight));
            images.add(scaleImage(context, context.getResources().getDrawable(R.drawable.book7_small), customWidth, customHeight));
            images.add(scaleImage(context, context.getResources().getDrawable(R.drawable.book8_small), customWidth, customHeight));
            images.add(scaleImage(context, context.getResources().getDrawable(R.drawable.book9_small), customWidth, customHeight));
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
        Drawable drawable = null;
        try{
            Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
            drawable = new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap, width, length, true));
        }
        catch (Throwable e){
            e.printStackTrace();
        }
        return drawable;
    }

    public static Integer getRealWidthSize(WindowManager windowManager){
        Display display = windowManager.getDefaultDisplay();
        Integer realWidth = 0;
        Integer realHeight = 0;

        if (Build.VERSION.SDK_INT >= 17){
            //new pleasant way to get real metrics
            DisplayMetrics realMetrics = new DisplayMetrics();
            display.getRealMetrics(realMetrics);
            realWidth = realMetrics.widthPixels;
            realHeight = realMetrics.heightPixels;
        } else if (Build.VERSION.SDK_INT >= 14) {
            //reflection for this weird in-between time
            try {
                Method mGetRawH = Display.class.getMethod("getRawHeight");
                Method mGetRawW = Display.class.getMethod("getRawWidth");
                realWidth = (Integer) mGetRawW.invoke(display);
                realHeight = (Integer) mGetRawH.invoke(display);
            } catch (Exception e) {
                //this may not be 100% accurate, but it's all we've got
                realWidth = display.getWidth();
                realHeight = display.getHeight();
                Log.e("Display Info", "Couldn't use reflection to get the real display metrics.");
            }

        } else {
            //This should be close, as lower API devices should not have window navigation bars
            realWidth = display.getWidth();
            realHeight = display.getHeight();
        }

        return realWidth;
    }
}
