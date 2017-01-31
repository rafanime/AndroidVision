package com.androidvision;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.androidvision.Camera.CameraPreview;

public class MainActivity extends Activity {

    private Camera myCamera;
    private CameraPreview myPreview;
    private FrameLayout preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(checkPermission(Manifest.permission.CAMERA, MY_PERMISSIONS_REQUEST_CAMERA))
            startCamera();

        Button switchButton = (Button) findViewById(R.id.Switch);
        switchButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        switchCamera();
                        if(checkPermission(Manifest.permission.CAMERA, MY_PERMISSIONS_REQUEST_CAMERA)) {
                            stopCamera();
                            startCamera();
                        }
                    }
                }
        );
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

    private void startCamera () {
        myCamera = getCameraInstance();

        myPreview = new CameraPreview(this, myCamera);

        preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(myPreview);
    }

    private static int camId = Camera.CameraInfo.CAMERA_FACING_FRONT;

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(camId); // attempt to get a Camera instance
            c.setDisplayOrientation(90);
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private void stopCamera() {
        if (myCamera != null){
            preview.removeView(myPreview);
            myCamera.release();
            myCamera = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA);

        if(permissionCheck == PackageManager.PERMISSION_GRANTED) startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopCamera();
    }

    public void switchCamera(){
        if (camId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            camId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }else {
            camId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            findViewById(R.id.full_view).setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }
}
