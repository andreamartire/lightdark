package newtech.audiolibrary.utils;

import android.content.Context;
import android.provider.MediaStore;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;

import newtech.audiolibrary.bean.Chapter;

/**
 * Created by MartireAn on 31/08/2017.
 */

public class MyFileUtils {

    public static void mkDir(String dirPath){
        if(!new File(dirPath).exists()){
            new File(dirPath).mkdirs();
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

    public static void deleteFileIfExists(String filePath) {
        try {
            if(new File(filePath).exists()){
                new File(filePath).delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void listDirFiles(String inputDir){
        if(new File(inputDir).isDirectory()){
            File[] filesList = new File(inputDir).listFiles();
            if(filesList != null){
                for(File f : filesList){
                    long size = f.length();
                    System.out.println("File: " + f.getAbsolutePath());
                    System.out.println("Size: " + size);
                }
            }
        }
    }

    public static void renameFile(String filePathTmp, String filePath) {
        if(new File(filePathTmp).exists()){
            new File(filePathTmp).renameTo(new File(filePath));
        }
    }

    public static void copy(File src, File dst) {
        try{
            FileInputStream inStream = new FileInputStream(src);
            FileOutputStream outStream = new FileOutputStream(dst);
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inStream.close();
            outStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean exists(String localFilePath) {
        return new File(localFilePath).exists();
    }
}
