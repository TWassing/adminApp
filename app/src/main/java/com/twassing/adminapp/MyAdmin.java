package com.twassing.adminapp;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.app.admin.NetworkEvent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.UserHandle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class MyAdmin extends DeviceAdminReceiver {

    private int attempts = 0;
    ComponentName component;

    @Override
    public void onEnabled(Context context, Intent intent)
    {
        component = getWho(context);
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        if(dpm != null)
        {
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
        Log.d("DPM", "button: ADMIN ENABLED");
    }

    @Override
    public void onDisabled(Context context, Intent intent)
    {
        Toast.makeText(context, "Device Admin : Disabled", Toast.LENGTH_LONG).show();
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
        SharedPreferences pref = context.getSharedPreferences("MyPref", MODE_PRIVATE);
        SocketConnection socketConnection1 = new SocketConnection(context, pref.getString("ipAddrString", ""), Integer.valueOf(pref.getString("portString", "")), pref.getString("certPasswordString", ""));
        FileHandler fileHandler1 = new FileHandler(context, "security");
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
            if (pref.getBoolean("sendLogEnabled", false))
            {
                socketConnection1.ConnectAndSendMessage(logList1);

                if (pref.getBoolean("removeLogEnabled", false)) {
                    if (socketConnection1.getLoggingSent()) {
                        fileHandler1.removeLogFilesFromDir();
                    }
                }
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
        SharedPreferences pref = context.getSharedPreferences("MyPref", MODE_PRIVATE);
        SocketConnection socketConnection = new SocketConnection(context, pref.getString("ipAddrString", ""), Integer.valueOf(pref.getString("portString", "")), pref.getString("certPasswordString", ""));
        FileHandler fileHandler = new FileHandler(context, "network");
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
            if (pref.getBoolean("sendLogEnabled", false))
            {
                socketConnection.ConnectAndSendMessage(logList);

                if (pref.getBoolean("removeLogEnabled", false)) {
                    if (socketConnection.getLoggingSent()) {
                        fileHandler.removeLogFilesFromDir();
                    }
                }
            }
        }
    }

    public static ComponentName getComponentName(Context context) {
        return new ComponentName(context.getApplicationContext(), MyAdmin.class);
    }

}
