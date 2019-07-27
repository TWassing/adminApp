package com.twassing.adminapp;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.os.UserHandle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MyAdmin extends DeviceAdminReceiver {

    private int attempts = 0;
    DevicePolicyManager dpm;
    ComponentName component;
    SecurityLogStringBuilder securityLogStringBuilder;
    SocketConnection socketConnection;

    @Override
    public void onEnabled(Context context, Intent intent)
    {
        component = getWho(context);
        dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        if(dpm != null)
        {
            Log.d("touchscreen", "POEESSSKNOLLL");
            dpm.setSecurityLoggingEnabled(getWho(context), true);
            dpm.setNetworkLoggingEnabled(getWho(context), true);
        }
        if(dpm.isSecurityLoggingEnabled(getWho(context)))
        {
            Log.d("touchscreen", "button: SECURITY LOGGING IS ENABLED BITCHESESESESESES");
        }
        if(dpm.isNetworkLoggingEnabled(getWho(context)))
        {
            Log.d("touchscreen", "button: NETWORK LOGGING IS ENABLED BITCHESESESESESES");
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
                "Security logging available MIAUW",
                Toast.LENGTH_LONG)
                .show();
        Log.d("securitylogging", "onSecurityLogsAvailable() called");


    }
    @Override
    public void onNetworkLogsAvailable(Context context, Intent intent, long batchToken, int networkLogsCount) {
        Log.d("touchscreen", "hoi");
        Log.d("touchscreen", "POEPOEPOEPOEPOEPOESKNOL");
    }

    public static ComponentName getComponentName(Context context) {
        return new ComponentName(context.getApplicationContext(), DeviceAdminReceiver.class);
    }

}
