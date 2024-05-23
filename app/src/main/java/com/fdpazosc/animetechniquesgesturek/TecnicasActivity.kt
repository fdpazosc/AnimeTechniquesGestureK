package com.fdpazosc.animetechniquesgesturek

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Camera
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.floor

class TecnicasActivity : AppCompatActivity() {
    private lateinit var vibrador: Vibrator
    private lateinit var mCameraManager: CameraManager
    private lateinit var mCameraId: String
    private lateinit var coordenadasProximadad: String
    private lateinit var coordenadasGirocopio: String
    private lateinit var coordenadasRotacion: String
    private lateinit var coordenadasAcelerometro: String
    private lateinit var dispCamara: Camera
    private lateinit var mp: MediaPlayer
    private var activo = true
    private var telefonoCerca = false
    private var paso = 0
    private lateinit var valorProximidad: TextView
    private lateinit var valoresGiroscopio: TextView
    private lateinit var valoresRotacion: TextView
    private lateinit var valoresAcelerometro: TextView
    private lateinit var sensorManager: SensorManager
    private lateinit var proximitySensor: Sensor
    private lateinit var gyroscopeSensor: Sensor
    private lateinit var rotationVectorSensor: Sensor
    private lateinit var accelerometerSensor: Sensor
    private lateinit var proximitySensorListener: SensorEventListener
    private lateinit var gyroscopeSensorListener: SensorEventListener
    private lateinit var rvListener: SensorEventListener
    private lateinit var accelerometerListener: SensorEventListener
    private lateinit var contexto: Context
    private lateinit var yo: TecnicasActivity
    private lateinit var tonos: IntArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tecnicas)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        valorProximidad = findViewById(R.id.valor_proximidad)
        valoresGiroscopio = findViewById(R.id.valores_giroscopio)
        valoresRotacion = findViewById(R.id.valores_rotacion)
        valoresAcelerometro = findViewById(R.id.valores_acelerometro)
        vibrador = this.getSystemService(VIBRATOR_SERVICE) as Vibrator
        contexto = this
        yo = this
        tonos = intArrayOf()
        mp = MediaPlayer.create(this, R.raw.kame_charge)
        activo = true
        telefonoCerca = false
        try {
            dispCamara = Camera.open()
        } catch (e: Exception) {
            Toast.makeText(
                applicationContext,
                "No se ha podido acceder a la camara",
                Toast.LENGTH_LONG
            ).show()
        }
        //iniciarSensores();
        paso = 0
        Log.d("VALOR", "" + intent.getIntExtra("ID_TECNICA", -1))
    }

    override fun onPause() {
        if (mp.isPlaying) mp.stop()
        cancelarSensores()
        finish()
        super.onPause()
    }

    override fun onStop() {
        if (mp.isPlaying) mp.stop()
        cancelarSensores()
        finish()
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        paso = 0
        iniciarSensores()
    }

    private fun cancelarSensores() {
        switchFlashLight(false)
        sensorManager.unregisterListener(proximitySensorListener)
        sensorManager.unregisterListener(gyroscopeSensorListener)
        sensorManager.unregisterListener(rvListener)
        sensorManager.unregisterListener(accelerometerListener)
    }

    private fun iniciarSensores() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)!!
        proximitySensorListener = object : SensorEventListener {
            override fun onSensorChanged(sensorEvent: SensorEvent) {
                coordenadasProximadad = "P=" + sensorEvent.values[0]
                Log.d("Coordenadaa", coordenadasProximadad)
                valorProximidad.text = coordenadasProximadad
                if (sensorEvent.values[0] < proximitySensor.maximumRange) {
                    // Detected something nearby
                    window.decorView.setBackgroundColor(Color.DKGRAY)
                    //switchFlashLight(true);
                    telefonoCerca = true
                } else {
                    // Nothing is nearby
                    window.decorView.setBackgroundColor(Color.BLACK)
                    //switchFlashLight(false);
                    telefonoCerca = false
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, i: Int) {
            }
        }
        sensorManager.registerListener(
            proximitySensorListener,
            proximitySensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)!!
        gyroscopeSensorListener = object : SensorEventListener {
            override fun onSensorChanged(sensorEvent: SensorEvent) {
                coordenadasGirocopio = """
                    X=${sensorEvent.values[SensorManager.DATA_X]}
                    Y=${sensorEvent.values[SensorManager.DATA_Y]}
                    Z=${sensorEvent.values[SensorManager.DATA_Z]}
                    """.trimIndent()
                Log.d("Coordenadaa", coordenadasGirocopio)
                valoresGiroscopio.text = coordenadasGirocopio
            }

            override fun onAccuracyChanged(sensor: Sensor, i: Int) {
            }
        }
        sensorManager.registerListener(
            gyroscopeSensorListener,
            gyroscopeSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)!!
        rvListener = object : SensorEventListener {
            override fun onSensorChanged(sensorEvent: SensorEvent) {
                coordenadasRotacion = """
                    X=${sensorEvent.values[SensorManager.AXIS_X]}
                    Y=${sensorEvent.values[SensorManager.AXIS_Y]}
                    Z=${sensorEvent.values[SensorManager.AXIS_Z]}
                    """.trimIndent()
                Log.d("Coordenadaa", coordenadasRotacion)
                valoresRotacion.text = coordenadasRotacion
            }

            override fun onAccuracyChanged(sensor: Sensor, i: Int) {
            }
        }
        sensorManager.registerListener(
            rvListener,
            rotationVectorSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!
        accelerometerListener = object : SensorEventListener {
            override fun onSensorChanged(sensorEvent: SensorEvent) {
                coordenadasAcelerometro = """
                    X=${sensorEvent.values[SensorManager.DATA_X]}
                    Y=${sensorEvent.values[SensorManager.DATA_Y]}
                    Z=${sensorEvent.values[SensorManager.DATA_Z]}
                    """.trimIndent()
                Log.d("Coordenadaa", coordenadasAcelerometro)
                valoresAcelerometro.text = coordenadasAcelerometro

                when (intent.getIntExtra("ID_TECNICA", -1)) {
                    0 -> {
                        if (paso == 0 && sensorEvent.values[SensorManager.DATA_Y] <= 0 && sensorEvent.values[SensorManager.DATA_Y] >= -5 && sensorEvent.values[SensorManager.DATA_Z] <= 14 && sensorEvent.values[SensorManager.DATA_Z] >= 10.5) {
                            //kame.setText("");
                            //kame.setText(kame.getText() + "Kame jame ");
                            paso = 1
                            switchFlashLight(false)
                            tonos = IntArray(3)
                            tonos[0] = R.raw.kame_change0
                            tonos[1] = R.raw.kame_charge
                            tonos[2] = R.raw.kame_charge4
                            val pos =
                                floor(Math.random() * tonos.size).toInt()
                            mp = MediaPlayer.create(contexto, tonos[pos])
                            mp.setOnCompletionListener(null)
                            mp.start()
                            vibrador.vibrate(1000)
                        }
                        if (paso == 1 && sensorEvent.values[SensorManager.DATA_X] >= -3 && sensorEvent.values[SensorManager.DATA_X] <= 3 && sensorEvent.values[SensorManager.DATA_Y] >= 7 && sensorEvent.values[SensorManager.DATA_Y] <= 9) {
                            //kame.setText(kame.getText() + "JAAAA");
                            paso = 0
                            switchFlashLight(true)
                            tonos = IntArray(4)
                            tonos[0] = R.raw.kamehameha_fire
                            tonos[1] = R.raw.kamehameha_fire2
                            tonos[2] = R.raw.kamehameha_fire3
                            tonos[3] = R.raw.kamehameha_fire4
                            val pos =
                                floor(Math.random() * tonos.size).toInt()
                            mp = MediaPlayer.create(contexto, tonos[pos])
                            mp.setOnCompletionListener(OnCompletionListener {
                                switchFlashLight(false)
                            })
                            mp.start()
                            vibrador.vibrate(1000)
                        }
                    }

                    1 -> {}
                    2 -> {}
                    3 -> {
                        if (paso == 0 &&
                            (sensorEvent.values[SensorManager.DATA_Z] in 8.5..10.0
                                    ||
                                    sensorEvent.values[SensorManager.DATA_Z] <= -8.5
                                    && sensorEvent.values[SensorManager.DATA_Z] >= -10) && sensorEvent.values[SensorManager.DATA_Y] >= -2 && sensorEvent.values[SensorManager.DATA_Y] <= 2 && sensorEvent.values[SensorManager.DATA_X] >= -2 && sensorEvent.values[SensorManager.DATA_X] <= 2
                        ) {
                            paso = 1
                            switchFlashLight(false)
                            tonos = IntArray(2)
                            tonos[0] = R.raw.destructodisc_charge1
                            tonos[1] = R.raw.miscdisc_charge
                            val pos =
                                floor(Math.random() * tonos.size).toInt()
                            mp = MediaPlayer.create(contexto, tonos[pos])
                            mp.setOnCompletionListener(null)
                            mp.start()
                            vibrador.vibrate(1000)
                        }
                        if (paso == 1 && sensorEvent.values[SensorManager.DATA_X] >= -2 && sensorEvent.values[SensorManager.DATA_X] <= 2 && sensorEvent.values[SensorManager.DATA_Z] >= -2 && sensorEvent.values[SensorManager.DATA_Z] <= 2 &&
                            (sensorEvent.values[SensorManager.DATA_Y] in 8.5..10.0)
                            ||
                            sensorEvent.values[SensorManager.DATA_Y] <= -8.5
                            && sensorEvent.values[SensorManager.DATA_Y] >= -10
                        ) {
                            //kame.setText(kame.getText() + "JAAAA");
                            paso = 0
                            switchFlashLight(true)
                            tonos = IntArray(1)
                            tonos[0] = R.raw.disc_fire
                            val pos =
                                floor(Math.random() * tonos.size).toInt()
                            mp = MediaPlayer.create(contexto, tonos[pos])
                            mp.setOnCompletionListener {
                                switchFlashLight(false)
                            }
                            mp.start()
                            vibrador.vibrate(1000)
                        }
                    }

                    4 -> if ((sensorEvent.values[SensorManager.DATA_Z] >= 14 || sensorEvent.values[SensorManager.DATA_Y] >= 14 || sensorEvent.values[SensorManager.DATA_X] >= 14 || sensorEvent.values[SensorManager.DATA_Z] <= -14 || sensorEvent.values[SensorManager.DATA_Y] <= -14 || sensorEvent.values[SensorManager.DATA_X] <= -14)
                        && !mp.isPlaying
                    ) {
                        tonos = IntArray(4)
                        tonos[0] = R.raw.meleemiss1
                        tonos[1] = R.raw.meleemiss2
                        tonos[2] = R.raw.meleemiss3
                        tonos[3] = R.raw.weakpunch
                        val pos =
                            floor(Math.random() * tonos.size).toInt()
                        mp = MediaPlayer.create(contexto, tonos[pos])
                        mp.start()
                        vibrador.vibrate(500)
                    }


                    5 -> if ((sensorEvent.values[SensorManager.DATA_Z] >= 14 || sensorEvent.values[SensorManager.DATA_Y] >= 14 || sensorEvent.values[SensorManager.DATA_X] >= 14 || sensorEvent.values[SensorManager.DATA_Z] <= -14 || sensorEvent.values[SensorManager.DATA_Y] <= -14 || sensorEvent.values[SensorManager.DATA_X] <= -14)
                        && !mp.isPlaying
                    ) {
                        tonos = IntArray(2)
                        tonos[0] = R.raw.swordhit
                        tonos[1] = R.raw.swordkill
                        val pos =
                            floor(Math.random() * tonos.size).toInt()
                        mp = MediaPlayer.create(contexto, tonos[pos])
                        mp.start()
                        vibrador.vibrate(500)
                    }


                    6 -> if (sensorEvent.values[SensorManager.DATA_Y] in 8.0..11.0 && sensorEvent.values[SensorManager.DATA_Z] >= -5 && sensorEvent.values[SensorManager.DATA_Z] <= 0 && telefonoCerca) {
                        //Toast.makeText(getApplicationContext(),"Brillo de Pantalla = " + this.getWindow().getAttributes().screenBrightness
                        //+"\n"+event.values[0]
                        //+"\n"+event.values[1]
                        //+"\n"+event.values[2]
                        //+"\n"+event.values.length
                        //,Toast.LENGTH_SHORT).show();
                        tonos = IntArray(4)
                        tonos[0] = R.raw.teleport00
                        tonos[1] = R.raw.teleport02
                        tonos[2] = R.raw.teleport3
                        tonos[3] = R.raw.teleport5
                        val pos =
                            floor(Math.random() * tonos.size).toInt()
                        mp = MediaPlayer.create(contexto, tonos[pos])
                        mp.start()
                        vibrador.vibrate(1000)
                        telefonoCerca = false

                        switchFlashLight(true)
                        switchFlashLight(false)
                        switchFlashLight(true)
                        switchFlashLight(false)
                        switchFlashLight(true)
                        switchFlashLight(false)
                        switchFlashLight(true)
                        switchFlashLight(false)
                    }

                    7 -> {
                        if (paso == 0 && sensorEvent.values[SensorManager.DATA_X] >= -3 && sensorEvent.values[SensorManager.DATA_X] <= 3 && sensorEvent.values[SensorManager.DATA_Y] >= 7 && sensorEvent.values[SensorManager.DATA_Y] <= 10) {
                            paso = 1
                            switchFlashLight(false)
                            vibrador.vibrate(1000)
                            mp.stop()
                        }
                        if (paso == 1 && sensorEvent.values[SensorManager.DATA_X] >= -5 && sensorEvent.values[SensorManager.DATA_X] <= 5 && sensorEvent.values[SensorManager.DATA_Y] >= -10 && sensorEvent.values[SensorManager.DATA_Y] <= -7) {
                            //kame.setText(kame.getText() + "JAAAA");
                            paso = 0
                            switchFlashLight(true)
                            tonos = IntArray(3)
                            tonos[0] = R.raw.tier
                            tonos[1] = R.raw.powerup02
                            tonos[2] = R.raw.powerup
                            val pos =
                                floor(Math.random() * tonos.size).toInt()
                            mp = MediaPlayer.create(contexto, tonos[pos])
                            mp.start()
                            vibrador.vibrate(1000)
                        }
                    }

                    8 -> {}
                    else -> {}
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, i: Int) {
            }
        }
        sensorManager.registerListener(
            accelerometerListener,
            accelerometerSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    fun switchFlashLight(status: Boolean) {
        if (applicationContext.packageManager
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
        ) {
            mCameraManager = getSystemService(CAMERA_SERVICE) as CameraManager

            try {
                mCameraId = mCameraManager.cameraIdList[0]
            } catch (e: CameraAccessException) {
                e.printStackTrace()
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
            }
            try {
                mCameraManager.setTorchMode(mCameraId, status)
                /*if (status)
                Toast.makeText(getApplicationContext(), "FLASH ENCENDIDO", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getApplicationContext(), "FLASH APAGADO", Toast.LENGTH_LONG).show();*/
            } catch (e: CameraAccessException) {
                e.printStackTrace()
                //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            //Toast.makeText(getApplicationContext(), "No hay FEATURE_CAMERA_FLASH", Toast.LENGTH_LONG).show();
        }
    }
}