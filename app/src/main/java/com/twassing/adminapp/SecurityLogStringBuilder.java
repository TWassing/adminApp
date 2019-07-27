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

public class SecurityLogStringBuilder {

    private String UUID;
    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mAdminName;
    private Context context;
    private String TAG;
    private List<String> sList;
    private SecurityEvent securityEvent;

    SecurityLogStringBuilder(Context context)
    {
        this.context = context;
        TAG = context.getPackageName();
        mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mAdminName = new ComponentName(context, MyAdmin.class);
        UUID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public List<String> getSecurityLogsAsList()
    {
        sList = processEvents(mDevicePolicyManager.retrieveSecurityLogs(mAdminName));
        return sList;
    }

    private List<String> processEvents(List<android.app.admin.SecurityLog.SecurityEvent> logs) {
        if (logs == null) {
            Log.w(TAG, "logs == null, are you polling too early?");
            return null;
        } else {
            Log.d(TAG, "Incoming logs size: " + logs.size());
            List<String> stringList = new ArrayList<>();
            for (android.app.admin.SecurityLog.SecurityEvent event : logs) {
                StringBuilder sb = new StringBuilder();
                SecurityEvent securityEvent = new SecurityEvent(event);
                //HEAD
                sb.append("<").append(securityEvent.getLogPRI()).append(">"); //PRI
                sb.append("1 "); //Version
                sb.append(securityEvent.getTimestamp());
                sb.append(" Android.").append(UUID); //HostName
                sb.append(" AdminApp "); //AppName
                sb.append(android.os.Process.myPid()); //PROCID
                sb.append(" - ");

                //StructuredText
                sb.append("- ");

                //Message

                sb.append("LogID=").append(securityEvent.getLogID()).append(", ");
                sb.append("LogLevel=").append(securityEvent.getLogLevel()).append(", ");
                sb.append("LogEvent=").append(securityEvent.getLogEvent()).append(", ");
                sb.append("Description=[ ").append(securityEvent.getLogDescription()).append("]");

                stringList.add(sb.toString());
            }
            return stringList;
        }
    }
}
