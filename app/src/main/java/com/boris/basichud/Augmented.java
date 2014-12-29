package com.boris.basichud;

import android.app.Activity;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.lang.Math;


public class Augmented extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private final float alpha = 0.8f;
    private float[] gravity = new float[3];
    private float[] linear_acceleration= new float[3];
    private static Camera mCamera;
    private MyGLSurfaceView mGLView;
    private FrameLayout preview;
    private CameraPreview mPreview;
    private float HUDangle = 0;
    private boolean portrait = true;
    private float incshift = 0;
    private float xshift = 0;
    private float yshift = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_augmented);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGLView = new MyGLSurfaceView(this);
        mGLView.setZOrderMediaOverlay(true);
        mCamera=getCameraInstance();
        setScreenOrientation();
        mPreview = new CameraPreview(this, mCamera);
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        preview.addView(mGLView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_augmented, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        setScreenOrientation();
    }

    public void setScreenOrientation(){
        Activity activity = this;
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(0,info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch(rotation){
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }
        int result;
        result = (info.orientation - degrees + 360) % 360;
        mCamera.setDisplayOrientation(result);
    }

    @Override
    protected void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this);
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            mCamera = getCameraInstance();
            mPreview.assignCamera(mCamera);
            setScreenOrientation();
            if (!getActionBar().isShowing())
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }


    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];

            float angle = (float) Math.atan2(gravity[1], gravity[0]);
            angle = angle * 180/3.14f;
            Activity activity = this;
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            switch(rotation){
                case Surface.ROTATION_0: angle = angle - 90; break;
                case Surface.ROTATION_90: angle = angle - 0; break;
                case Surface.ROTATION_180: angle = angle - 270; break;
                case Surface.ROTATION_270: angle = angle - 180; break;
            }
            angle = - angle % 360;
            if ((angle >= -10) && (angle <= 10)) {
                HUDangle = 180;
                portrait = true;
                xshift=linear_acceleration[0];
                yshift=linear_acceleration[1];
                incshift = (float) Math.atan2(gravity[2], gravity[1]);
            }
            if ((angle >= 80) && (angle <= 100)) {
                HUDangle = 270;
                portrait = false;
                xshift=linear_acceleration[1];
                yshift=linear_acceleration[0];
                incshift = (float) Math.atan2(gravity[2], gravity[0]);
            }
            if ((angle >= 170) && (angle <= 190)){
                HUDangle = 0;
                portrait = true;
                xshift=linear_acceleration[0];
                yshift=linear_acceleration[1];
                incshift = -1f*((float) Math.atan2(gravity[2], gravity[1]));
            }
            if ((angle >= 260) || (angle <= -80)) {
                HUDangle = 90;
                portrait = false;
                xshift=linear_acceleration[1];
                yshift=linear_acceleration[0];
                incshift = -1f*((float) Math.atan2(gravity[2], gravity[0]));
            }
            if (incshift>0.5f*(float)Math.PI)
                incshift=incshift-(float)Math.PI;
            else if (incshift<-0.5f*(float)Math.PI)
                incshift=incshift+(float)Math.PI;
            incshift = 0.4f*incshift/((float) Math.PI);
            xshift = 0.1f*xshift;
            yshift = 0.1f*yshift;
            mGLView.setHUDInfo(angle, HUDangle, portrait, incshift, xshift, yshift);
        }
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
}

