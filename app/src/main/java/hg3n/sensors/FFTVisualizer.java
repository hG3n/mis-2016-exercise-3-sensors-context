package hg3n.sensors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.GradientDrawable;
import android.nfc.Tag;
import android.os.Debug;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Array;
import java.util.LinkedList;

/**
 * Created by hGen on 13/05/16.
 */
public class FFTVisualizer extends View {

    private static final String TAG = "FFTVisualizer\t";

    private float _width;
    private float _height;

    private LinkedList<AccelerometerData> _data_queue;

    private LinkedList<Float> _mean_queue = new LinkedList<Float>();
    private int _mean_iterator = 1;

    private int _frame = 0;

    private FFT _fft = null;

    public FFTVisualizer(Context context, Activity activity) {
        super(context);
    }

    public FFTVisualizer(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        drawDiagram(canvas);
        if(_data_queue != null && _data_queue != null) {
            drawData(canvas);
        }

        ++_frame;
    }

    private void drawDiagram(Canvas canvas) {

        // init once in the first frame
        if(_frame < 1) {
            _width = canvas.getWidth();
            _height = canvas.getHeight();
        }

        // fill canvas black
        canvas.drawColor(Color.BLACK);

        // define color
        Paint color = new Paint();
        color.setStyle(Paint.Style.FILL);
        color.setColor(Color.WHITE);
        color.setStrokeWidth(3.0f);

        // draw time axis
        canvas.drawLine(0.0f, _height / 2, _width, _height / 2, color);

        // draw y-value lines
        color.setStrokeWidth(1.0f);
        color.setColor(Color.GRAY);
        for (int i = 1; i < 20; ++i) {
            float temp_y = map(i, 0, 20, 0, _height);
            canvas.drawLine(0.0f, temp_y, _width, temp_y, color);
        }
    }

    private void drawData(Canvas canvas) {
        drawGraphFrame(canvas, 'r', Color.WHITE); // real component of fft
//        drawGraphFrame(canvas, 'i', Color.GREEN); // imaginary component of fft
    }

    public void resetIterator() {
        _mean_iterator = 1;
    }

    private void drawGraphFrame(Canvas canvas, Character component, int color_code) {

        if(_fft.n != _data_queue.size())
            return;

        if(!(component == 'r' || component == 'i'))
            return;

        // initialize fft parameters
        double x[] = new double[_fft.n];
        double y[] = new double[_fft.n];
        for(int i = 0; i < _fft.n; ++i) {
            y[i] = 0;
            x[i] = _data_queue.get(i).magnitude;
        }

        // perform fft
        _fft.fft(x, y);

        double[] res = new double[_fft.n/2];
        if(component == 'r') {
            for(int i = 0; i < res.length; ++i) {
                res[i] = x[i];
            }
        } else {
            for(int i = 0; i < res.length; ++i) {
                res[i] = y[i];
            }
        }


        // setup frequency domain
        double f[] = new double[(int)_fft.n/2];

        // init f and get abs of resulting fft aplitude
        double max = 0.0;
        for(int i = 0; i < (int)_fft.n/2; ++i) {
            res[i] = Math.abs(res[i]);
            if(res[i] > max) {
                max = res[i];
            }
            f[i] = i*(1/(int)_fft.n/2);
        }
        max = 100;

        if(_mean_iterator > 100) {
            _mean_iterator = 1;
        }

        if(_fft.n/2 == _mean_queue.size()) {
            for(int i = 0; i < _fft.n/2; ++i) {
                float value = (float)_mean_queue.get(i);
                float first =(float) (value * (1- 1/_mean_iterator));
                float second = (float) (res[i] * (1/_mean_iterator));
                value = first + second;
                _mean_queue.set(i, value);
            }
            ++_mean_iterator;
        } else {
            _mean_queue.clear();
            _mean_iterator = 1;
            for(int i = 0; i < _fft.n/2; ++i) {
                _mean_queue.add((float)res[i]);
            }
        }

        // define color
        Paint color = new Paint();
        color.setColor(color_code);
        color.setStrokeWidth(2.0f);

        // get first coordinates
        Point2d last_data_point = new Point2d();
        last_data_point.x = map(0, 0, 0, (float) max, _width);
        last_data_point.y = map((float) res[0], 0, (float) max, _height/2, 0);

        // create graph
        for(int i = 0; i < (int)_fft.n/2; ++i) {
            // get y value of axis
            float x_value = map(i, 0, (int)_fft.n/2-1, 0, _width);
            float y_value = map((float) res[i], 0, (float) max, _height/2, 0);

            // draw x line
            canvas.drawLine(last_data_point.x, last_data_point.y, x_value, y_value, color);

            // save last x axis coord
            last_data_point.x = x_value;
            last_data_point.y = y_value;
        }
    }

    public void updateValues(LinkedList<AccelerometerData> new_queue) {
        _data_queue = new_queue;
        _fft = new FFT(_data_queue.size());
        invalidate();
    }

    // master mapping function
    private float map(float value, float in_min, float in_max, float out_min, float out_max) {
        return out_min + ((out_max - out_min) / (in_max - in_min)) * (value - in_min);
    }

    public float getMean() {
        float result = 0;
        for(float item: _mean_queue) {
            result += item/_mean_queue.size();
        }
        return result;
    }
}