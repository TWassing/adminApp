package com.twassing.adminapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button save;
    private EditText ipAddrText, portText, certPasswordText;
    public static final int RESULT_ENABLE = 11;
    private DevicePolicyManager devicePolicyManager;
    private ActivityManager activityManager;
    private ComponentName compName;
    private SharedPreferences.Editor editor;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        setContentView(R.layout.activity_main);
        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        compName = new ComponentName(this, MyAdmin.class);

        save = (Button) findViewById(R.id.saveBtn);
        ipAddrText = (EditText) findViewById(R.id.ipAddrTxt);
        portText = (EditText) findViewById(R.id.portTxt);
        certPasswordText = (EditText) findViewById(R.id.certPasswordTxt);
        Switch securityLoggingSwitch = (Switch)  findViewById(R.id.securityLoggingSwitch);
        Switch networkLoggingSwitch = (Switch) findViewById(R.id.networkLoggingSwitch);
        Switch removeLogSwitch = (Switch) findViewById(R.id.removeLogSwitch);
        Switch sendLogSwitch = (Switch) findViewById(R.id.sendLogSwitch);

        securityLoggingSwitch.setChecked(pref.getBoolean("securityLoggingEnabled", false));
        networkLoggingSwitch.setChecked(pref.getBoolean("networkLoggingEnabled", false));
        removeLogSwitch.setChecked(pref.getBoolean("removeLogEnabled", false));
        sendLogSwitch.setChecked(pref.getBoolean("sendLogEnabled", false));
        ipAddrText.setText(pref.getString("ipAddrString", ""));
        portText.setText(pref.getString("portString", ""));
        certPasswordText.setText(pref.getString("certPasswordString", ""));

        save.setOnClickListener(this);
        securityLoggingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                devicePolicyManager.setSecurityLoggingEnabled(compName, isChecked);
                editor.putBoolean("securityLoggingEnabled", isChecked);
                editor.apply();
                if(devicePolicyManager.isSecurityLoggingEnabled(compName))
                {
                    Log.d("MyAdmin", "Security logging enabled");
                    Toast.makeText(getApplicationContext(), "Security logging : Enabled", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Security logging : Disabled", Toast.LENGTH_LONG).show();
                }
            }

        });
        networkLoggingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                devicePolicyManager.setNetworkLoggingEnabled(compName, isChecked);
                editor.putBoolean("networkLoggingEnabled", isChecked);
                editor.apply();
                if(devicePolicyManager.isNetworkLoggingEnabled(compName))
                {
                    Log.d("MyAdmin", "Network logging enabled");
                    Toast.makeText(getApplicationContext(), "Network logging : Enabled", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Network logging : Disabled", Toast.LENGTH_LONG).show();
                }
            }

        });

        removeLogSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("removeLogEnabled", isChecked);
                editor.apply();
            }

        });

        sendLogSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("sendLogEnabled", isChecked);
                editor.apply();
            }

        });

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 225);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        boolean isActive = devicePolicyManager.isAdminActive(compName);
    }

    @Override
    public void onClick(View view) {
        if(view == save)
        {
            Intent intent = new Intent(this, showSecurityLoggingActivity.class);
            editor.putString("ipAddrString", ipAddrText.getText().toString());
            editor.putString("portString", portText.getText().toString());
            editor.putString("certPasswordString", certPasswordText.getText().toString());
            editor.apply();
            Toast.makeText(getApplicationContext(), "Log collector settings saved", Toast.LENGTH_LONG).show();
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