package com.boris.basichud;

import android.app.Activity;
import android.hardware.Sensor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.hardware.SensorManager;
import android.widget.TextView;


public class MainActivity extends Activity {
    public final static String EXTRA_MESSAGE ="com.boris.test.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SensorManager manager = (SensorManager) getBaseContext().getSystemService(SENSOR_SERVICE);
        TextView aboutTitle = (TextView) findViewById(R.id.about_title);
        TextView about = (TextView) findViewById(R.id.about);
        TextView sensorsTitle = (TextView) findViewById(R.id.sensors_title);
        TextView sensorText = (TextView) findViewById(R.id.sensors);
        aboutTitle.setTextSize(20);
        aboutTitle.append("About:");
        about.setTextSize(15);
        about.append("This application serves as a demonstration of a simple heads-up display. " +
                "Central bars serve as an artificial horizon, green scale on the right shows by how " +
              "much is the device tilted upwards or downwards, while red scales on the top and left sides " +
                "show the device's x and y acceleration accordingly. User interface rotates accordingly if the " +
                "device screen is sufficiently rotated. Tapping the screen in HUD mode toggles the visibility " +
                "of the action and status bars.\n");
        sensorsTitle.setTextSize(20);
        sensorsTitle.append("Sensor list:");
        sensorText.setTextSize(15);
        sensorText.append("Accelerometer present: ");
        if (manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            sensorText.append("true\n");
        } else {
            sensorText.append("false\n");
        }
        sensorText.append("Gyroscope present: ");
        if (manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null){
            sensorText.append("true\n");
        } else {
            sensorText.append("false\n");
        }
        sensorText.append("Light sensor present: ");
        if (manager.getDefaultSensor(Sensor.TYPE_LIGHT) != null){
            sensorText.append("true\n");
        } else {
            sensorText.append("false\n");
        }
        sensorText.append("Significant motion sensor present: ");
        if (manager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION) != null){
            sensorText.append("true\n");
        }
        else {
            sensorText.append("false\n");
        }
        sensorText.append("Magnetometer present: ");
        if (manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null){
            sensorText.append("true\n");
        }
        else {
            sensorText.append("false\n");
        }
        sensorText.append("Linear accelerometer present: ");
        if (manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null){
            sensorText.append("true\n");
        }
        else {
            sensorText.append("false\n");
        }
        sensorText.append("Proximity sensor present: ");
        if (manager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null){
            sensorText.append("true\n");
        }
        else {
            sensorText.append("false\n");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent preview = new Intent(this, Augmented.class);
            startActivity(preview);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
