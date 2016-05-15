package hg3n.sensors;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.sax.TextElementListener;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.Math;
import java.util.LinkedList;

/**
 * Created by hGen on 13/05/16.
 */
public class AccelerometerEventListener implements SensorEventListener {

    private static final String TAG = "AccEL\t";

    private TextView _x_text_field;
    private TextView _y_text_field;
    private TextView _z_text_field;

    private float[] _gravity = new float[3];
    private float[] _linear_acceleration = new float[3];

    private int _data_frame;
    private LinkedList<AccelerometerData> _data_queue = new LinkedList<AccelerometerData>();

    private SensorVisualizer _sensor_visualizer;

    public AccelerometerEventListener(Activity activity) {
        _x_text_field = (TextView)activity.findViewById(R.id.x_axis_value);
        _y_text_field = (TextView)activity.findViewById(R.id.y_axis_value);
        _z_text_field = (TextView)activity.findViewById(R.id.z_axis_value);

        _sensor_visualizer = (SensorVisualizer)activity.findViewById(R.id.data_view);
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

        // get magnitued valus
        double magnitude = - calcMagnitude(_linear_acceleration[0], _linear_acceleration[1], _linear_acceleration[2]);
        Log.d(TAG, Double.toString(magnitude));

        // get current time
        long current_time = System.currentTimeMillis();

        // create new accelerometer date object
        AccelerometerData temp_data = new AccelerometerData();
        temp_data.x = - _linear_acceleration[0];
        temp_data.y = - _linear_acceleration[1];
        temp_data.z = - _linear_acceleration[2];
        temp_data.magnitude = magnitude;
        temp_data.timestamp = current_time;

        // if there are more than 100 elements in the data queue remove the last added
        if(_data_frame > 99) {
            _data_queue.removeFirst();
            _data_queue.add(temp_data);
        } else {
            _data_queue.add(temp_data);
        }

        // update visualizer data queue
        _sensor_visualizer.updateValues(_data_queue);

        // set values to text views
        _x_text_field.setText(String.format("%.2f", _linear_acceleration[0]));
        _y_text_field.setText(String.format("%.2f", _linear_acceleration[1]));
        _z_text_field.setText(String.format("%.2f", _linear_acceleration[2]));

        ++_data_frame;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private double calcMagnitude(float x, float y, float z) {
        double sum = Math.pow(x,2) + Math.pow(y,2) + Math.pow(z,2);
        return Math.sqrt(sum);
    }
}

class AccelerometerData {
    public float x;
    public float y;
    public float z;
    public double magnitude;
    public long timestamp;
}
