package net.pcswv.drivesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

public class PermissionChecker extends Activity{
    private static final int MY_PERMISSIONS_REQUEST_CODE = 123;
    private Context mContext;
    private Activity mActivity;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public PermissionChecker(Context context, Activity activity) {
        mContext = context;
        mActivity = activity;
    }

    public void checkpermissions (final String req, String msg, String title, Context ctx) {
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        editor = prefs.edit();
        String stat = prefs.getString("Stat", null);
        if (stat == null) {
            editor.putString("Stat", "3").commit();
        } else {
            if (ContextCompat.checkSelfPermission(mActivity, req)
                    != PackageManager.PERMISSION_GRANTED) {
                editor.putString("Stat", "0").commit();
                // Do something, when permissions not granted
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        mActivity, req)) {
                    // If we should give explanation of requested permissions

                    // Show an alert dialog here with request explanation
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setMessage(msg);
                    builder.setTitle(title);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            editor.putString("Stat", "1").commit();
                            ActivityCompat.requestPermissions(
                                    mActivity,
                                    new String[]{
                                            req
                                    },
                                    MY_PERMISSIONS_REQUEST_CODE
                            );
                        }
                    });
                    builder.setNeutralButton("Cancel", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    // Directly request for required permissions, without explanation
                    ActivityCompat.requestPermissions(
                            mActivity,
                            new String[]{
                                    req
                            },
                            MY_PERMISSIONS_REQUEST_CODE
                    );
                }
            } else {
                // Do something, when permissions are already granted
                Toast.makeText(mContext, "Permissions already granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_CODE:{
                // When request is cancelled, the results array are empty
                if(
                        (grantResults.length >0) &&
                                (grantResults[0]
                                        + grantResults[1]
                                        + grantResults[2]
                                        == PackageManager.PERMISSION_GRANTED
                                )
                ){
                    // Permissions are granted
                    editor.putString("Stat", "1").commit();
                    Toast.makeText(mContext,"Permissions granted.",Toast.LENGTH_SHORT).show();
                }else {
                    // Permissions are denied
                    editor.putString("Stat", "2").commit();
                    Toast.makeText(mContext,"Permissions denied.",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}