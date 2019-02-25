package net.pcswv.drivesafe;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private Context mContext;
    private boolean detectEnabled;
    private TextView textViewDetectState;
    private Button buttonToggleDetect;
    private Button buttonExit;
    PermissionChecker permissionChecker;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private String Stat = null;
    private int Stint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();
        Stat = prefs.getString("Stat", null);
        textViewDetectState = (TextView) findViewById(R.id.textViewDetectState);

        buttonToggleDetect = (Button) findViewById(R.id.buttonDetectToggle);
        buttonToggleDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDetectEnabled(!detectEnabled);
            }
        });

        buttonExit = (Button) findViewById(R.id.buttonExit);
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDetectEnabled(false);
                MainActivity.this.finish();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setDetectEnabled(boolean enable) {
        detectEnabled = enable;
        Stat = prefs.getString("Stat", null);
        Toast.makeText(mContext, Stat, Toast.LENGTH_SHORT).show();
        if(Stat!=null) {
            Stint = Integer.valueOf(Stat);
            Toast.makeText(this, Stat, Toast.LENGTH_SHORT).show();
            if (Stint == 1) {
                enable(enable);
            }
            if (Stint == 2) {
                Toast.makeText(this, "You denied needed permissions for this app to function. Please give the permission's requested", Toast.LENGTH_SHORT).show();
                permck();
            }
            if (Stint == 0) {
                Toast.makeText(this, "Please grant requested permissions.", Toast.LENGTH_SHORT).show();
                permck();
            }
        }
    }
    private void permck() {
        permissionChecker = new PermissionChecker(mContext, MainActivity.this);
        permissionChecker.checkpermissions(Manifest.permission.READ_PHONE_STATE, "RPS", "RPS", mContext);
        permissionChecker.checkpermissions(Manifest.permission.PROCESS_OUTGOING_CALLS, "POC", "POC", mContext);
    }
    private void enable(boolean enable) {
        Intent intent = new Intent(this, CallDetectService.class);
        if (enable) {
            // start detect service
            startService(intent);

            buttonToggleDetect.setText("Turn off");
            textViewDetectState.setText("Detecting");
        }
        else {
            // stop detect service
            stopService(intent);

            buttonToggleDetect.setText("Turn on");
            textViewDetectState.setText("Not detecting");
        }
    }
}
