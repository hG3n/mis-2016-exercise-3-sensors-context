package hg3n.sensors;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by hGen on 13/05/16.
 */
public class SampleRateAdjust implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "SampleRateAdjust\t";

    private Activity _activity;
    private SensorManager _sensor_manager;
    private Sensor _accelerometer;
    private SensorEventListener _sensor_event_listener;

    public SampleRateAdjust(Activity activity, SensorManager manager, Sensor sensor, SensorEventListener listener) {
        _activity = activity;
        _sensor_manager = manager;
        _accelerometer = sensor;
        _sensor_event_listener = listener;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){

        int input_value;
        int MIN = 1000;

        // check if process is greater than minimum value
        if(progress < MIN) {
            input_value = MIN;
        } else {
            input_value = progress;
        }

        // unregister manager to change input value
        _sensor_manager.unregisterListener(_sensor_event_listener);

        // re register eventlistener with new input value
        _sensor_manager.registerListener(_sensor_event_listener, _accelerometer, input_value);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

}
