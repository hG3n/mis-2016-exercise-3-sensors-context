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
 * Created by basti-laptop on 15/05/16.
 */
public class FFTWindowSizeAdjust implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "FFTWindowSizeAdjust\t";

    private AccelerometerEventListener _sensor_event_listener;

    public FFTWindowSizeAdjust(AccelerometerEventListener listener) {
        _sensor_event_listener = listener;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
        int input_value;
        int MIN = 1;
        int MAX = 10;

        // check if process is greater than minimum value
        if(progress < MIN) {
            input_value = MIN;
        } else if(progress > MAX) {
            input_value = MAX;
        } else {
            input_value = progress;
        }

        _sensor_event_listener.setFFTWindowSize((int)Math.pow((double)2,(double)input_value));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

}
