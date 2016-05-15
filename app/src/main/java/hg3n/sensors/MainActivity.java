package hg3n.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity\t";

    private int _sample_rate = 100;

    private SeekBar _sample_rate_bar;

    private SensorManager _sensor_manager;
    private SensorEventListener _sensor_event_listener;
    private Sensor _accelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init gui fields
        _sample_rate_bar = (SeekBar)this.findViewById(R.id.sample_rate);

        // fill private members
        _sensor_manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        _accelerometer = _sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // init and register SensorEventListener
        _sensor_event_listener = new AccelerometerEventListener(this);
        _sensor_manager.registerListener(_sensor_event_listener, _accelerometer, _sample_rate);

        // init and add SeekBarEventListener
        _sample_rate_bar.setOnSeekBarChangeListener(new SampleRateAdjust(this, _sensor_manager, _accelerometer, _sensor_event_listener));
    }

    @Override
    public void onPause() {
        super.onPause();
        _sensor_manager.unregisterListener(_sensor_event_listener);
    }

    @Override
    public void onResume() {
        super.onResume();
        _sensor_manager.registerListener(_sensor_event_listener, _accelerometer, _sample_rate);
    }
}
