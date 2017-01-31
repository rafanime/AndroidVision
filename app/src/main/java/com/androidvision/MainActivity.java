package com.androidvision;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission(Manifest.permission.CAMERA, MY_PERMISSIONS_REQUEST_CAMERA);
    }

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;

    private boolean checkPermission(String permission, int request){
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                permission);

        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    request);
            return false;
        } else return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Success -> " , " We have camera permission");

                } else {
                }
                return;
            }

        }
    }



}
