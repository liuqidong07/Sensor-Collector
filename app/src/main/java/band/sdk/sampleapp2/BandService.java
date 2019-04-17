package band.sdk.sampleapp2;

import android.app.ActivityManager;
import android.app.Service;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;
import android.text.format.Time;
import android.util.Log;
import com.microsoft.band.BandClient;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import band.sdk.sampleapp.R;
import java.util.Date;

public class BandService extends Service {
    public final String TAG = "BandService";
    public String Category = "walk";
    public String username = "NULL";
    public String sdcard = android.os.Environment.getExternalStorageDirectory().toString();
    String filepath_acc;
    String filepath_cal;
    String filepath_gyr;
    String filepath_ski;
    String filepath_uv;
    String filepath_dis;
    FileWriter fw_acc, fw_cal, fw_gyr, fw_ski, fw_uv, fw_dis;
    BufferedWriter bw_acc, bw_cal, bw_gyr, bw_ski, bw_uv, bw_dis;
    String tempString;
    private boolean touched = false;
    public boolean flag;
    private SensorManager mSensorManager;
    private MediaPlayer mediaPlayer01;
    public int num = 0;
    private String connected = "connecting";
    private PowerManager.WakeLock wakeLock=null;
    public BandService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager pm=(PowerManager)getSystemService(Context.POWER_SERVICE);
        wakeLock=pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"mywakelock");
        wakeLock.acquire();
        Log.d(TAG, "onCreate() executed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Category = intent.getStringExtra("Category");
        username = intent.getStringExtra("name");
        num = Integer.parseInt(intent.getStringExtra("num"));
        flag = true;
        Musicthread mthread = new Musicthread();
        mthread.start();
        Stopthread sthread = new Stopthread();
        sthread.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = num;
                while (i > 0 && flag) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i--;
                    Intent intent = new Intent();
                    intent.putExtra("timeleft", i);
                    intent.putExtra("state", connected);
                    intent.setAction("band.sdk.sampleapp2.BandService");
                    sendBroadcast(intent);
                    Log.d(TAG,String.valueOf(i));
                }
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        flag = false;
        Log.d(TAG, "service destory");
        if (mediaPlayer01 != null) {
            mediaPlayer01.release();
        }
        writeclose();
        if(wakeLock!=null){
            wakeLock.release();
            wakeLock=null;
        }
        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    private void player() {
        mediaPlayer01 = MediaPlayer.create(getBaseContext(), R.raw.ring);
        mediaPlayer01.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                                  public void onCompletion(MediaPlayer mediaPlayer) {
                                                      mediaPlayer.release();
                                                  }
                                              }
        );
        mediaPlayer01.start();
    }

    class PlayMusic extends Thread {
        public void run() {
            player();
        }
    }

    class Musicthread extends Thread {
        public void run() {
            try {
                sleep(1000);
                // player();
                fileinit();
                writeopen();
                touched = true;
                Time t = new Time();
                t.setToNow();
                try {
                    bw_acc.write(t.hour + " " + t.minute + " " + t.second + " begin" + "\r\n");
                    bw_acc.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                new task().start();
                new PlayMusic().start();
            } catch (InterruptedException e) {
                //	appendToUI("Sleeping counters error...\n");
            }
        }
    }

    class Stopthread extends Thread {
        public void run() {
            while (flag && (num > 0)) {
                try {
                    sleep(1000);
                    num--;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (flag) {
                Time t = new Time();
                t.setToNow();
                try {
                    bw_acc.write(t.hour + " " + t.minute + "  " + t.second + "stop");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                writeclose();
                new PlayMusic().start();
                touched = false;
                Log.d(TAG, "over");
                onDestroy();
            }
        }
    }
    class task extends Thread {
        public void run() {
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
            mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME);
            mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
            mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_GAME);
            mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_GAME);
            mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_GAME);
        }
    }

    private SensorEventListener mSensorListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            int index = sensorEvent.sensor.getType();
            tempString = sensorEvent.values[0] + " " + sensorEvent.values[1] + " " + sensorEvent.values[2] + " " + ((new Date()).getTime()+ (sensorEvent.timestamp - System.nanoTime()) / 1000000L) + " " + "\r\n";

            try {
                switch (index) {
                    case Sensor.TYPE_ACCELEROMETER:
                        fw_acc.write(tempString);
                        break;
                    case Sensor.TYPE_GYROSCOPE:
                        fw_cal.write(tempString);
                        break;
                    case Sensor.TYPE_GRAVITY:
                        fw_gyr.write(tempString);
                        break;
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        fw_ski.write(tempString);
                        break;
                    case Sensor.TYPE_LINEAR_ACCELERATION:
                        fw_uv.write(tempString);
                        break;
                    case Sensor.TYPE_ROTATION_VECTOR:
                        fw_dis.write(tempString);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    public boolean writeopen() {
        Time t = new Time();
        t.setToNow();
        String fileName_acc = filepath_acc + '/' + t.year + t.month + t.monthDay + t.hour + t.minute + t.second + ".txt";
        String fileName_cal = filepath_cal + '/' + t.year + t.month + t.monthDay + t.hour + t.minute + t.second + ".txt";
        String fileName_gyr = filepath_gyr + '/' + t.year + t.month + t.monthDay + t.hour + t.minute + t.second + ".txt";
        String fileName_ski = filepath_ski + '/' + t.year + t.month + t.monthDay + t.hour + t.minute + t.second + ".txt";
        String fileName_uv = filepath_uv + '/' + t.year + t.month + t.monthDay + t.hour + t.minute + t.second + ".txt";
        String fileName_dis = filepath_dis + '/' + t.year + t.month + t.monthDay + t.hour + t.minute + t.second + ".txt";
        try {
            create(filepath_acc);
            create(filepath_cal);
            create(filepath_gyr);
            create(filepath_ski);
            create(filepath_uv);
            create(filepath_dis);
            fw_acc = new FileWriter(fileName_acc);
            fw_cal = new FileWriter(fileName_cal);
            fw_gyr = new FileWriter(fileName_gyr);
            fw_ski = new FileWriter(fileName_ski);
            fw_uv = new FileWriter(fileName_uv);
            fw_dis = new FileWriter(fileName_dis);
            bw_acc = new BufferedWriter(fw_acc, 102400);
            bw_cal = new BufferedWriter(fw_cal, 102400);
            bw_gyr = new BufferedWriter(fw_gyr, 102400);
            bw_ski = new BufferedWriter(fw_ski, 102400);
            bw_uv = new BufferedWriter(fw_uv, 102400);
            bw_dis = new BufferedWriter(fw_dis, 102400);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return false;
        }

        return true;
    }

    public void create(String filepath) {
        File myfilePath = new File(filepath);
        if (!myfilePath.exists()) {
            myfilePath.mkdirs();
        }
    }

    public boolean fileinit() {
        filepath_acc = sdcard + "/101SensorData/" + username + "/" + Category + "/加速度";
        filepath_cal = sdcard + "/101SensorData/" + username + "/" + Category + "/陀螺仪";
        filepath_gyr = sdcard + "/101SensorData/" + username + "/" + Category + "/重力";
        filepath_ski = sdcard + "/101SensorData/" + username + "/" + Category + "/磁力计";
        filepath_uv = sdcard + "/101SensorData/" + username + "/" + Category + "/线性加速度";
        filepath_dis = sdcard + "/101SensorData/" + username + "/" + Category + "/rotation vector";
        return true;
    }

    public boolean writeclose() {
        mSensorManager.unregisterListener(mSensorListener);
            if (touched) {
                try {
                    bw_acc.close();
                    fw_acc.close();
                    bw_cal.close();
                    fw_cal.close();
                    bw_gyr.close();
                    fw_gyr.close();
                    bw_ski.close();
                    fw_ski.close();
                    bw_uv.close();
                    fw_uv.close();
                    bw_dis.close();
                    fw_dis.close();
                } catch (IOException e) {
                    return false;// appendToUI(e.getMessage());
                }
            }
        return true;
    }
}