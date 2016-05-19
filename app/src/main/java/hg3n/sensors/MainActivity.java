package hg3n.sensors;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity\t";

    private int _sample_rate = 100;

    private SeekBar _sample_rate_bar;
    private SeekBar _fft_size_bar;

    private SensorManager _sensor_manager;
    private AccelerometerEventListener _sensor_event_listener;
    private Sensor _accelerometer;

    private NotificationManager _notification_manager;
    private NotificationCompat.Builder _notification;

    private int _notification_id = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init gui fields
        _sample_rate_bar = (SeekBar)this.findViewById(R.id.sample_rate);
        _fft_size_bar = (SeekBar)this.findViewById(R.id.window_size);

        // fill private members
        _sensor_manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        _accelerometer = _sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // init and register SensorEventListener
        _sensor_event_listener = new AccelerometerEventListener(this);
        _sensor_manager.registerListener(_sensor_event_listener, _accelerometer, _sample_rate);

        // init and add SeekBarEventListener
        _sample_rate_bar.setOnSeekBarChangeListener(new SampleRateAdjust(_sensor_manager, _accelerometer, _sensor_event_listener));
        _fft_size_bar.setOnSeekBarChangeListener(new FFTWindowSizeAdjust(_sensor_event_listener));

        _notification_manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        _notification = new NotificationCompat.Builder(this);
    }

    @Override
    public void onPause() {
        super.onPause();
//        _sensor_manager.unregisterListener(_sensor_event_listener);
    }

    @Override
    public void onResume() {
        super.onResume();
//        _sensor_manager.registerListener(_sensor_event_listener, _accelerometer, _sample_rate);
    }

    public void notify(float value) {

        _notification.setContentTitle("Your activity changed to:");
        _notification.setSmallIcon(R.drawable.notification_icon);
        if(value < 2.5)
            _notification.setContentText("Sitting!");
        else if(value >= 4.0 && value <= 10.5)
            _notification.setContentText("Walking!");
        else if(value >= 12.0 && value <= 20.0)
            _notification.setContentText("Running!");
        else
            _notification.setContentText("No activity recognized!");

        _notification_manager.notify(_notification_id,_notification.build());
    }

}
