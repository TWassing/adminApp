package com.twassing.adminapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class FileHandler {

    private static String TAG = "FileHandler";
    private Context context;

    FileHandler(Context context)
    {
        this.context = context;
    }

    public void writeToExternalSdCard(List<String> stringList)
    {
        File dir = getSecurityLogsDir();
        dir.mkdirs();
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss").format(new Date());
        File file = new File (dir, "securitylog-" + timeStamp + ".txt");

        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            for (String s : stringList)
                pw.println(s);
            pw.flush();
            pw.close();
            f.flush();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "******* File not found. Did you" +
                    " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public boolean removeSecurityFiles()
    {
        boolean success = false;
        File directory = getSecurityLogsDir();
        if (!directory.exists())
        {
            Log.d("SecurityLog1", "dir doesn't exist or no permissions");
            return false;
        }
        String[] files = directory.list();
        if (files != null && files.length > 0) {
            Log.d("hoi", "Nr of files: " + files.length);
            for (String file : files) {
                if (new File(directory, file).exists())
                {
                    if(!new File(directory,file).delete()) {
                        Log.d("hoi", "Cannot delete file");
                        return success;
                    }
                    else
                    {
                        success = true;
                    }
                }
                else
                {
                    Log.d(TAG, "Files doesn't exists");
                    return success;
                }

            }
        }
        return success;
    }

    public List<String> fromLogFilesToList()
    {
        File directory = getSecurityLogsDir();
        List<String> stringList = new ArrayList<>();

        if (!directory.exists())
        {
            Log.d("SecurityLog2", "dir doesn't exist or no permissions");
            return null;
        }
        File[] files = directory.listFiles();

        if (files != null) {
            try {
                for (File file : files) {
                    Scanner s = new Scanner(file);
                    while (s.hasNextLine()){
                        stringList.add(s.nextLine());
                    }
                    s.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.i(TAG, "******* File not found. Did you" +
                        " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
            }
        }
        return stringList;
    }

    private File getSecurityLogsDir()
    {
        if(ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 225);
        }
        return new File (Environment.getExternalStorageDirectory().toString()+ "/securityLog");
    }
}
