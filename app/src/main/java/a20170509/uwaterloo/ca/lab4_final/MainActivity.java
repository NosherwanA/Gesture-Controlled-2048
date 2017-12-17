package a20170509.uwaterloo.ca.lab4_final;


import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.annotation.RequiresApi;
import java.util.Timer;


public class MainActivity extends AppCompatActivity {

    RelativeLayout ll;//Lab 2 Cleanup

    TextView tvAccelerometer;
    AccelerometerEventListener accListener;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ll = (RelativeLayout)findViewById(R.id.layout1);

        ll.getLayoutParams().width = 1000;
        ll.getLayoutParams().height = 1000;
        ll.setBackgroundResource(R.drawable.gameboard);

        tvAccelerometer = new TextView(getApplicationContext());
        tvAccelerometer.setText("Accelerometer Instantaneous Readings");
        tvAccelerometer.setTextColor(Color.BLACK);
        tvAccelerometer.setTextSize(40.0f);

        Timer myGameLoop = new Timer();
        GameLoopTask myGameLoopTask = new GameLoopTask(this, ll, getApplicationContext());
        myGameLoopTask.run();
        myGameLoop.schedule(myGameLoopTask, 30,15);

        SensorManager sensorManager =(SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor Accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        accListener = new AccelerometerEventListener(tvAccelerometer,myGameLoopTask);
        sensorManager.registerListener(accListener, Accelerometer,  SensorManager.SENSOR_DELAY_GAME);

        ll.addView(tvAccelerometer);

    }

}


