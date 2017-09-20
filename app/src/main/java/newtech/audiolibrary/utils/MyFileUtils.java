package newtech.audiolibrary.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import newtech.audiolibrary.bean.Chapter;

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
}