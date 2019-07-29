package com.twassing.adminapp;

import android.annotation.TargetApi;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.provider.Settings;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LogBuilder {

    private String UUID;
    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mAdminName;
    private Context context;
    private String TAG;
    private List<String> sList;
    private SecurityEvent securityEvent;

    LogBuilder(Context context)
    {
        this.context = context;
        TAG = context.getPackageName();
        mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mAdminName = new ComponentName(context, MyAdmin.class);
        UUID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public List<String> ProcessSecurityLogs(List<android.app.admin.SecurityLog.SecurityEvent> logs) {
        if (logs == null) {
            Log.w(TAG, "logs == null, are you polling too early?");
            return null;
        } else {
            Log.d(TAG, "Incoming logs size: " + logs.size());
            List<String> logList = new ArrayList<String>();
            for (android.app.admin.SecurityLog.SecurityEvent event : logs)
            {
                logList.add(processEvent(new SecurityEvent(event)));
            }
            return logList;
        }
    }

    public List<String> ProcessNetworkLogs(List<android.app.admin.NetworkEvent> logs) {
        if (logs == null) {
            Log.w(TAG, "logs == null, are you polling too early?");
            return null;
        } else {
            Log.d(TAG, "Incoming logs size: " + logs.size());
            List<String> logList = new ArrayList<String>();
            for (android.app.admin.NetworkEvent event : logs)
            {
                logList.add(processEvent(new NetworkEvent(event)));
            }
            return logList;
        }
    }

    private String processEvent(LogEvent log) {

        StringBuilder sb = new StringBuilder();

        //HEAD
        sb.append("<").append(log.getLogPRI()).append(">"); //PRI
        sb.append("1 "); //Version
        sb.append(log.getTimestamp());
        sb.append(" Android.").append(UUID); //HostName
        sb.append(" AdminApp "); //AppName
        sb.append(android.os.Process.myPid()); //PROCID
        sb.append(" - ");

        //StructuredText
        sb.append("- ");

        //Message
        sb.append(log.getMessage());

        return sb.toString();
    }
}
