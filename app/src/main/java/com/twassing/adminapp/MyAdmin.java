package com.twassing.adminapp;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.app.admin.NetworkEvent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PersistableBundle;
import android.os.UserHandle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class MyAdmin extends DeviceAdminReceiver {

    private int attempts = 0;
    ComponentName component;
    SocketConnection socketConnection;

    @Override
    public void onEnabled(Context context, Intent intent)
    {
        component = getWho(context);
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        if(dpm != null)
        {
            Log.d("touchscreen", "POEESSSKNOLLL");
            dpm.setSecurityLoggingEnabled(getWho(context), true);
            dpm.setNetworkLoggingEnabled(getWho(context), true);
            if(dpm.isSecurityLoggingEnabled(getWho(context)))
            {
                Log.d("touchscreen", "button: SECURITY LOGGING IS ENABLED");
            }
            if(dpm.isNetworkLoggingEnabled(getWho(context)))
            {
                Log.d("touchscreen", "button: NETWORK LOGGING IS ENABLED");
            }
        }

        Toast.makeText(context, "Device Admin : Enabled", Toast.LENGTH_LONG).show();
        Log.d("touchscreen", "button: ADMIN ENABLED");
    }

    @Override
    public void onDisabled(Context context, Intent intent)
    {
        Toast.makeText(context, "Device Admin : Disabled", Toast.LENGTH_LONG).show();
        Log.d("touchscreen", "poep: ADMIN DISABLED");
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent, UserHandle user)
    {
        DevicePolicyManager DevicePolicyManager=
                (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        if(DevicePolicyManager != null)
            attempts = DevicePolicyManager.getCurrentFailedPasswordAttempts();
        Log.e("Security", "Password failed : " + attempts + " attempts" );
    }

    @Override
    public void onPasswordSucceeded(@NonNull Context context, @NonNull Intent intent, @NonNull UserHandle user)
    {
        attempts = 0;
    }


    @Override
    public void onSecurityLogsAvailable(Context context, Intent intent)
    {
        Toast.makeText(context,
                "Security logging available",
                Toast.LENGTH_LONG)
                .show();
        Log.d("securitylogging", "onSecurityLogsAvailable() called");
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        if(dpm == null)
            Log.d("securitylogging", "DPM = null");
        LogBuilder logBuilder1 = new LogBuilder(context);
        SocketConnection socketConnection1 = new SocketConnection(context);
        FileHandler fileHandler1 = new FileHandler(context, "securityLog");
        List<String> logList1 = new ArrayList<>();

        List<String> savedList = fileHandler1.fromLogFilesToList();
        List<android.app.admin.SecurityLog.SecurityEvent> securityList = dpm.retrieveSecurityLogs(getComponentName(context));
        List<String> newList = logBuilder1.ProcessSecurityLogs(securityList);

        if(savedList != null) {
            if (savedList.size() > 0) {
                logList1.addAll(savedList);
            }
        }

        if(newList != null)
        {
            if (newList.size() > 0)
            {
                logList1.addAll(newList);
                fileHandler1.writeToExternalSdCard(newList);
            }
        }
        if (logList1.size() > 0) {
            socketConnection1.ConnectAndSendMessage(logList1);

            if (socketConnection1.getLoggingSent()) {
                fileHandler1.removeLogFilesFromDir();
            }
        }

    }
    @Override
    public void onNetworkLogsAvailable(Context context, Intent intent, long batchToken, int networkLogsCount) {
        Log.i("DPM", "onNetworkLogsAvailable(), batchToken: " + batchToken
                + ", event count: " + networkLogsCount);
        List<NetworkEvent> events = null;
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        try {
            events = dpm.retrieveNetworkLogs(getComponentName(context), batchToken);
            Log.d("DPM", "Network logs retrieved");
        } catch (SecurityException e) {
            Log.e("DPM",
                    "Exception while retrieving network logs batch with batchToken: " + batchToken
                    , e);
        }

        if (events == null) {
            Log.e("DPM", "Failed to retrieve network logs batch with batchToken: " + batchToken);
            return;
        }

        LogBuilder logBuilder = new LogBuilder(context);
        SocketConnection socketConnection = new SocketConnection(context);
        FileHandler fileHandler = new FileHandler(context, "networkLog");
        List<String> logList = new ArrayList<>();

        List<String> savedList = fileHandler.fromLogFilesToList();
        List<String> newList = logBuilder.ProcessNetworkLogs(dpm.retrieveNetworkLogs(getComponentName(context), batchToken));

        if(savedList != null) {
            if (savedList.size() > 0) {
                logList.addAll(savedList);
            }
        }

        if(newList != null)
        {
            if (newList.size() > 0)
            {
                logList.addAll(newList);
                fileHandler.writeToExternalSdCard(newList);
            }
        }
        if (logList.size() > 0) {
            socketConnection.ConnectAndSendMessage(logList);

            if (socketConnection.getLoggingSent()) {
                fileHandler.removeLogFilesFromDir();
            }
        }
    }

    public static ComponentName getComponentName(Context context) {
        return new ComponentName(context.getApplicationContext(), MyAdmin.class);
    }

}
