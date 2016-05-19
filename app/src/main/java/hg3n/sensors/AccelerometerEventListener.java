package hg3n.sensors;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.lang.Math;
import java.util.LinkedList;

/**
 * Created by hGen on 13/05/16.
 */
public class AccelerometerEventListener implements SensorEventListener {

    private static final String TAG = "AccEL\t";

    private float[] _gravity = new float[3];
    private float[] _linear_acceleration = new float[3];

    private int _data_frame;
    private LinkedList<AccelerometerData> _vis_data_queue = new LinkedList<AccelerometerData>();
    private LinkedList<AccelerometerData> _fft_data_queue = new LinkedList<AccelerometerData>();
    private LinkedList<AccelerometerData> _recording_queue = new LinkedList<AccelerometerData>();
    private int _fft_window_size = 256;

    private SensorVisualizer _sensor_visualizer;
    private FFTVisualizer _fft_visualizer;

    private MainActivity _activity;

    public AccelerometerEventListener(MainActivity activity) {

        _activity = activity;
        _sensor_visualizer = (SensorVisualizer)activity.findViewById(R.id.data_view);
        _fft_visualizer = (FFTVisualizer)activity.findViewById(R.id.fft_view);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float alpha = 0.8f;

        // Isolate the force of gravity with the low-pass filter.
        _gravity[0] = alpha * _gravity[0] + (1 - alpha) * event.values[0];
        _gravity[1] = alpha * _gravity[1] + (1 - alpha) * event.values[1];
        _gravity[2] = alpha * _gravity[2] + (1 - alpha) * event.values[2];

        // Remove the gravity contribution with the high-pass filter.
        _linear_acceleration[0] = event.values[0] - _gravity[0];
        _linear_acceleration[1] = event.values[1] - _gravity[1];
        _linear_acceleration[2] = event.values[2] - _gravity[2];

        // get magnitued values
        double magnitude = - calcMagnitude(_linear_acceleration[0], _linear_acceleration[1], _linear_acceleration[2]);

        // get current time
        long current_time = System.currentTimeMillis();

        // create new accelerometer date object
        AccelerometerData temp_data = new AccelerometerData();
        temp_data.x = - _linear_acceleration[0];
        temp_data.y = - _linear_acceleration[1];
        temp_data.z = - _linear_acceleration[2];
        temp_data.magnitude = magnitude;
        temp_data.timestamp = current_time;

        // update all UI
        updateSensorVisualizer(temp_data);
        updateFFTVisualizer(temp_data);
        updateTextViews();

        ++_data_frame;
    }

    private void updateSensorVisualizer(AccelerometerData data) {
        // if there are more than 100 elements in the data queue remove the last added
        if(_vis_data_queue.size() > 99) {
            _vis_data_queue.removeFirst();
            _vis_data_queue.add(data);
        } else {
            _vis_data_queue.add(data);
        }

        // update visualizer data queue
        _sensor_visualizer.updateValues(_vis_data_queue);
    }

    private void updateFFTVisualizer(AccelerometerData data) {
        // if there are more than determined window size elements in the fft data queue remove the last added
        if(_fft_data_queue.size() >= _fft_window_size) {
            while(_fft_data_queue.size() >= _fft_window_size) {
                _fft_data_queue.removeFirst();
            }
            _fft_data_queue.add(data);
            // only update if full window contained by queue
            _fft_visualizer.updateValues(_fft_data_queue);
        } else {
            _fft_data_queue.add(data);
        }

        float mean = _fft_visualizer.getMean();
        _activity.notify(mean);
    }

    public void resetIterator() {
        _fft_visualizer.resetIterator();
    }

    private void updateTextViews() {
        // set values to text views
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private double calcMagnitude(float x, float y, float z) {
        double sum = Math.pow(x,2) + Math.pow(y,2) + Math.pow(z,2);
        return Math.sqrt(sum);
    }

    public void setFFTWindowSize(int value) {
        _fft_window_size = value;
    }
}

class AccelerometerData {
    public float x;
    public float y;
    public float z;
    public double magnitude;
    public long timestamp;
}