package com.three.shake;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class GyroscopeFragment extends Fragment {

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private TextView textViewX;
    private TextView textViewY;
    private TextView textViewZ;
    private ImageView imageView;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch rotationSwitch;

    private long rotationResetStartTime = 0;
    private static final float ANGULAR_THRESHOLD = 1.0f;
    private static final long RESET_TIME_THRESHOLD = 3000;
    SensorEventListener gyroscopeListener;

   /* float angularVelocityX = 0;
    float angularVelocityY = 0;
    float angularVelocityZ = 0;*/
   float angularVelocityX;
    float angularVelocityY;
    float angularVelocityZ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_angular_text, container, false);

        textViewX = rootView.findViewById(R.id.textViewX);
        textViewY = rootView.findViewById(R.id.textViewY);
        textViewZ = rootView.findViewById(R.id.textViewZ);
        imageView = rootView.findViewById(R.id.imageView);
        rotationSwitch = rootView.findViewById(R.id.rotationSwitch);

        String textX = textViewX.getText().toString();
        String textY = textViewY.getText().toString();
        String textZ = textViewZ.getText().toString();

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if (gyroscopeSensor == null) {
            Log.d("Gyro", "Something went wrong with Gyro");
        }

        gyroscopeListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {
                angularVelocityX = event.values[0];
                angularVelocityY = event.values[1];
                angularVelocityZ = event.values[2];

                /*angularVelocityX += event.values[0];
                angularVelocityY += event.values[1];
                angularVelocityZ += event.values[2];*/

                setTextForTextView(textX + angularVelocityX,textViewX);
                setTextForTextView(textY + angularVelocityY,textViewY);
                setTextForTextView(textZ + angularVelocityZ,textViewZ);

                rotateImage(angularVelocityX, angularVelocityY, angularVelocityZ);

                if (angularVelocityZ > 1){
                    Context context = requireContext();
                    String message = "Z-axis angular velocity is high!";
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }

                if (angularVelocityY>1){
                    rotationSwitch.setChecked(true);
                }
                else if(angularVelocityY<-1){
                    rotationSwitch.setChecked(false);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        sensorManager.registerListener(
                gyroscopeListener,
                gyroscopeSensor,
                SensorManager.SENSOR_DELAY_NORMAL
        );

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();

        sensorManager.unregisterListener(gyroscopeListener);
    }

    @Override
    public void onResume() {
        super.onResume();

        sensorManager.registerListener(
                gyroscopeListener,
                gyroscopeSensor,
                SensorManager.SENSOR_DELAY_NORMAL
        );
    }

    public void setTextForTextView(String velocityValue, TextView textView){
        textView.setText(velocityValue);
    }

    public void rotateImage(float angularVelocityX, float angularVelocityY, float angularVelocityZ){


        if (Math.abs(angularVelocityX) < ANGULAR_THRESHOLD &&
                Math.abs(angularVelocityY) < ANGULAR_THRESHOLD &&
                Math.abs(angularVelocityZ) < ANGULAR_THRESHOLD) {

            if (rotationResetStartTime == 0) {
                rotationResetStartTime = System.currentTimeMillis();
            } else {
                long currentTime = System.currentTimeMillis();
                if (currentTime - rotationResetStartTime >= RESET_TIME_THRESHOLD) {

                    imageView.setRotationX(0);
                    imageView.setRotationY(0);
                    imageView.setRotation(0);
                    rotationResetStartTime = 0;
                }
            }
        } else {
            rotationResetStartTime = 0;
        }

        imageView.setRotationX(imageView.getRotationX() + angularVelocityX*3);
        imageView.setRotationY(imageView.getRotationY() - angularVelocityY*3);
        imageView.setRotation(imageView.getRotation() + angularVelocityZ*3);
    }
}
