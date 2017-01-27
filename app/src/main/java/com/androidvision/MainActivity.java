package com.androidvision;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.androidvision.Camera.CameraPreview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
                        stopCamera();
                        checkPermissions();
                    }
                }
        );

        
        // Add a listener to the Capture button
        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera

                        if(checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE))
                             myCamera.takePicture(null, null, mPicture);
                    }
                }
        );
    }

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

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

    private void checkPermissions() {

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }

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

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);


            if (pictureFile == null){

                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();

                galleryAddPic(pictureFile);

                myCamera.stopPreview();
                myCamera.startPreview();
            } catch (FileNotFoundException e) {

            } catch (IOException e) {

            }

        }
    };

    private void galleryAddPic(File f) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "AndroidCV");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("AndroidCV", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }




}
