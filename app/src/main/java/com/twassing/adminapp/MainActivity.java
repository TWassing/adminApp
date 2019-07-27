package com.twassing.adminapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.security.KeyStore;
import java.util.HashSet;

import javax.net.ssl.SSLSocketFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button lock, disable, enable, showLogging;
    public static final int RESULT_ENABLE = 11;
    private DevicePolicyManager devicePolicyManager;
    private ActivityManager activityManager;
    private ComponentName compName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        compName = new ComponentName(this, MyAdmin.class);

        lock = (Button) findViewById(R.id.lockBtn);
        disable = (Button) findViewById(R.id.disableAdminBtn);
        enable = (Button) findViewById(R.id.enableAdminBtn);
        showLogging = (Button) findViewById(R.id.showLoggingBtn);

        lock.setOnClickListener(this);
        enable.setOnClickListener(this);
        disable.setOnClickListener(this);
        showLogging.setOnClickListener(this);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        boolean isActive = devicePolicyManager.isAdminActive(compName);
    }

    @Override
    public void onClick(View view) {
        if (view == lock)
        {
            boolean active = devicePolicyManager.isAdminActive(compName);
            if (active)
            {
                devicePolicyManager.lockNow();
            }
            else
            {
                Toast.makeText(this, " You need to enable device admin!", Toast.LENGTH_SHORT).show();
            }
        }
        else if (view == enable)
        {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "this text explains why bitches");
            startActivityForResult(intent, RESULT_ENABLE);
        }
        else if (view == disable)
        {
            devicePolicyManager.removeActiveAdmin(compName);
            Log.d("HALLO", "onClick: MIAUW");
            if (devicePolicyManager.isNetworkLoggingEnabled(compName))
            {
                Log.d("HALLO", "SICKE POESKNOL MIAUW");
            }
        }

        else if(view == showLogging)
        {
            Intent intent = new Intent(this, showSecurityLoggingActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case RESULT_ENABLE:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(MainActivity.this, "You enabled the Admin Device features", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "You disabled the Admin Device features", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
