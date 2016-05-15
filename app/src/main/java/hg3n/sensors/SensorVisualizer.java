package hg3n.sensors;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.GradientDrawable;
import android.nfc.Tag;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Array;
import java.util.LinkedList;

/**
 * Created by hGen on 13/05/16.
 */
public class SensorVisualizer extends View {

    private static final String TAG = "SensorVisualizer\t";

    private float _width;
    private float _height;

    private LinkedList<AccelerometerData> _data_queue;

    private int _frame = 0;

    public SensorVisualizer(Context context) {
        super(context);
    }

    public SensorVisualizer(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        drawDiagram(canvas);
        if(_data_queue != null) {
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

        // draw axis graphs
        drawGraphFrame(canvas, 'x', Color.GREEN);
        drawGraphFrame(canvas, 'y', Color.BLUE);
        drawGraphFrame(canvas, 'z', Color.RED);
        drawGraphFrame(canvas, 'm', Color.WHITE);
    }

    private void drawGraphFrame(Canvas canvas, Character axis, int color_code) {

        // define color
        Paint color = new Paint();
        color.setColor(color_code);
        color.setStrokeWidth(2.0f);

        // get first coordinates
        Point2d last_data_point = new Point2d();
        last_data_point.x = map(0, 0, 99, 0, _width);
        if(axis == 'x') {
            last_data_point.y = map(_data_queue.get(0).x, 0, 15, 0, _height/2);
        } else if (axis == 'y') {
            last_data_point.y = map(_data_queue.get(0).y, 0, 15, 0, _height/2);
        } else if (axis == 'z') {
            last_data_point.y = map(_data_queue.get(0).z, 0, 15, 0, _height/2);
        } else if (axis == 'm') {
            last_data_point.y = (float) map((float) _data_queue.get(0).magnitude, 0, 10, 0, _height / 2);
        }

        // create graph
        for(int i = 0; i < _data_queue.size(); ++i) {
            // get current item
            AccelerometerData current_item = _data_queue.get(i);

            // get y value of axis
            float x_value = map(i, 0, 99, 0, _width);
            float y_value = 0.0f;
            if(axis == 'x') {
                y_value = map(current_item.x, 0, 15, 0, _height/2);
            } else if (axis == 'y') {
                y_value = map(current_item.y, 0, 15, 0, _height/2);
            } else if (axis == 'z') {
                y_value = map(current_item.z, 0, 15, 0, _height/2);
            } else if (axis == 'm') {
                y_value = (float) map((float) current_item.magnitude, 0, 10, 0, _height/2);
            }

            // draw x line
            canvas.drawLine(last_data_point.x, last_data_point.y + _height/2, x_value, y_value + _height/2, color);

            // save last x axis coord
            last_data_point.x = x_value;
            last_data_point.y = y_value;
        }
    }

    public void updateValues(LinkedList<AccelerometerData> new_queue) {
        _data_queue = new_queue;
        invalidate();
    }

    // master mapping function
    private float map(float value, float in_min, float in_max, float out_min, float out_max) {
        return out_min + ((out_max - out_min) / (in_max - in_min)) * (value - in_min);
    }
}

class Point2d {
    float x;
    float y;
}
