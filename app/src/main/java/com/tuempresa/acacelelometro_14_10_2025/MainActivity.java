package com.tuempresa.acacelelometro_14_10_2025;
import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    TextView xCoor, yCoor, zCoor, estado, precision;
    SensorManager gestorSensores;
    Sensor acelerometro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xCoor = findViewById(R.id.xcoor);
        yCoor = findViewById(R.id.ycoor);
        zCoor = findViewById(R.id.zcoor);
        estado = findViewById(R.id.estado);
        precision = findViewById(R.id.precision);

        gestorSensores = (SensorManager) getSystemService(SENSOR_SERVICE);
        acelerometro = gestorSensores.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (acelerometro != null) {
            gestorSensores.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "Tu dispositivo no tiene acelerómetro", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            xCoor.setText(String.format("X: %.2f", x));
            yCoor.setText(String.format("Y: %.2f", y));
            zCoor.setText(String.format("Z: %.2f", z));

            // Detectar movimiento
            double magnitud = Math.sqrt(x * x + y * y + z * z);
            if (magnitud > 11) {
                estado.setText("¡Demasiado movimiento! Cuidado con el terremoto");
            } else if (magnitud < 9) {
                estado.setText("¡Estás inclinando mucho el teléfono!");
            } else {
                estado.setText("Todo estable. ¡Buen control de gravedad!");
            }
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