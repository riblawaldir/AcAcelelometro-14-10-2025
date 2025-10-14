package com.tuempresa.acacelelometro_14_10_2025;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    TextView xCoor, yCoor, zCoor, estado, precision;
    ScrollView mainLayout;
    ImageView rocketImage;

    TextView acelX, acelY, acelZ;
    TextView gyroX, gyroY, gyroZ;
    TextView magnetX, magnetY, magnetZ;

    SensorManager gestorSensores;
    Sensor acelerometro, giroscopio, magnetometro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayout = findViewById(R.id.main);
        rocketImage = findViewById(R.id.rocketImage);
        mainLayout.setBackgroundColor(Color.rgb(80, 0, 180));

        xCoor = findViewById(R.id.xcoor);
        yCoor = findViewById(R.id.ycoor);
        zCoor = findViewById(R.id.zcoor);
        estado = findViewById(R.id.estado);
        precision = findViewById(R.id.precision);

        acelX = findViewById(R.id.acel_x);
        acelY = findViewById(R.id.acel_y);
        acelZ = findViewById(R.id.acel_z);

        gyroX = findViewById(R.id.gyro_x);
        gyroY = findViewById(R.id.gyro_y);
        gyroZ = findViewById(R.id.gyro_z);

        magnetX = findViewById(R.id.magnet_x);
        magnetY = findViewById(R.id.magnet_y);
        magnetZ = findViewById(R.id.magnet_z);

        gestorSensores = (SensorManager) getSystemService(SENSOR_SERVICE);
        acelerometro = gestorSensores.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        giroscopio = gestorSensores.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magnetometro = gestorSensores.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (acelerometro != null)
            gestorSensores.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);
        else
            Toast.makeText(this, "Tu dispositivo no tiene acelerómetro", Toast.LENGTH_LONG).show();

        if (giroscopio != null)
            gestorSensores.registerListener(this, giroscopio, SensorManager.SENSOR_DELAY_NORMAL);
        else
            Toast.makeText(this, "Tu dispositivo no tiene giroscopio", Toast.LENGTH_LONG).show();

        if (magnetometro != null)
            gestorSensores.registerListener(this, magnetometro, SensorManager.SENSOR_DELAY_NORMAL);
        else
            Toast.makeText(this, "Tu dispositivo no tiene magnetómetro", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int tipo = event.sensor.getType();

        if (tipo == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            xCoor.setText(String.format("X: %.2f", x));
            yCoor.setText(String.format("Y: %.2f", y));
            zCoor.setText(String.format("Z: %.2f", z));

            acelX.setText(String.format("X: %.2f", x));
            acelY.setText(String.format("Y: %.2f", y));
            acelZ.setText(String.format("Z: %.2f", z));

            rocketImage.setRotation(x * 5);

            int color = Color.rgb(
                    (int) Math.min(180, Math.abs(x * 25)),
                    (int) Math.min(40, Math.abs(y * 5)),
                    (int) Math.min(255, Math.abs(z * 30))
            );

            mainLayout.animate().setDuration(250).withEndAction(() ->
                    mainLayout.setBackgroundColor(color)
            ).start();

            double magnitud = Math.sqrt(x * x + y * y + z * z);
            if (magnitud > 11)
                estado.setText("¡Demasiado movimiento! Cuidado con el terremoto");
            else if (magnitud < 9)
                estado.setText("¡Estás inclinando mucho el teléfono!");
            else
                estado.setText("Todo estable. ¡Buen control de gravedad!");
        }

        if (tipo == Sensor.TYPE_GYROSCOPE) {
            float gx = event.values[0];
            float gy = event.values[1];
            float gz = event.values[2];

            gyroX.setText(String.format("X: %.2f", gx));
            gyroY.setText(String.format("Y: %.2f", gy));
            gyroZ.setText(String.format("Z: %.2f", gz));
        }

        if (tipo == Sensor.TYPE_MAGNETIC_FIELD) {
            float mx = event.values[0];
            float my = event.values[1];
            float mz = event.values[2];

            double magnitud = Math.sqrt(mx * mx + my * my + mz * mz);
            magnetX.setText(String.format("Total: %.2f", magnitud));
            magnetY.setText(String.format("Y: %.2f", my));
            magnetZ.setText(String.format("Z: %.2f", mz));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        String mensaje;
        switch (accuracy) {
            case SensorManager.SENSOR_STATUS_UNRELIABLE:
                mensaje = "Precisión baja: el sensor está confundido.";
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                mensaje = "Precisión baja: intenta no moverlo tanto.";
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                mensaje = "Precisión media: ¡va mejorando!";
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                mensaje = "Precisión alta: ¡perfecto!";
                break;
            default:
                mensaje = "Estado desconocido.";
        }
        precision.setText(mensaje);
    }

    @Override
    protected void onResume() {
        super.onResume();
        gestorSensores.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gestorSensores.unregisterListener(this);
    }
}
