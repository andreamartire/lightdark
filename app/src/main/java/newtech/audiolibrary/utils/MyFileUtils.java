package newtech.audiolibrary.utils;

import java.io.File;
import java.io.IOException;

/**
 * Created by MartireAn on 31/08/2017.
 */

public class MyFileUtils {

    public static void mkDir(String dirPath){
        if(!new File(dirPath).exists()){
            new File(dirPath).mkdir();
        }
    }

    public static void touchFile(String filePath) {
        try {
            if(!new File(filePath).exists()){
                new File(filePath).createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
