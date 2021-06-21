package com.dji.uxsdkdemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.lang.reflect.Constructor;

import dji.common.camera.ResolutionAndFrameRate;
import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.products.Aircraft;
import dji.sdk.products.HandHeld;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.camera.Camera;

import static com.dji.uxsdkdemo.DemoApplication.getProductInstance;

public class MainActivity extends AppCompatActivity {

    private Button liveBtn;
    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DJISampleApplication.getEventBus().register(this);

        // When the compile and target version is higher than 22, please request the
        // following permissions at runtime to ensure the
        // SDK work well.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.VIBRATE,
                            Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.WAKE_LOCK, Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SYSTEM_ALERT_WINDOW,
                            Manifest.permission.READ_PHONE_STATE,
                    }
                    , 1);
        }

        setContentView(R.layout.activity_main);
        liveBtn = (Button) findViewById(R.id.Live);
        //Camera camera = getCameraInstance();

//        ResolutionAndFrameRate low = new ResolutionAndFrameRate(SettingsDefinitions.VideoResolution.RESOLUTION_1280x720, SettingsDefinitions.VideoFrameRate.FRAME_RATE_24_FPS);
//        camera.setVideoResolutionAndFrameRate(low, new CommonCallbacks.CompletionCallback() {
//            @Override
//            public void onResult(DJIError djiError) {
//                if (djiError != null) {
//                    ToastUtils.setResultToToast("can't change mode of camera, error:"+djiError.getDescription());
//                }
//            }
//        });

    }

    public void transition(View v) {

        ToastUtils.setResultToToast("Start Live Show");
        Toast.makeText(MainActivity.this,"start live show", Toast.LENGTH_SHORT).show();
        String liveShowUrl = "rtmp://54.157.140.28/livestream/drone";
        if (DJISDKManager.getInstance().getLiveStreamManager().isStreaming()) {
            ToastUtils.setResultToToast("already started!");
            return;
        }
        new Thread() {
            @Override
            public void run() {
                DJISDKManager.getInstance().getLiveStreamManager().setLiveUrl(liveShowUrl);
                int result = DJISDKManager.getInstance().getLiveStreamManager().startStream();
                DJISDKManager.getInstance().getLiveStreamManager().setStartTime();
                ToastUtils.setResultToToast("startLive:" + result +
                        "\n isVideoStreamSpeedConfigurable:" + DJISDKManager.getInstance().getLiveStreamManager().isVideoStreamSpeedConfigurable() +
                        "\n isLiveAudioEnabled:" + DJISDKManager.getInstance().getLiveStreamManager().isLiveAudioEnabled());
            }
        }.start();


    }
    public static synchronized Camera getCameraInstance() {

        if (getProductInstance() == null) return null;

        Camera camera = null;

        if (getProductInstance() instanceof Aircraft){
            camera = ((Aircraft) getProductInstance()).getCamera();

        } else if (getProductInstance() instanceof HandHeld) {
            camera = ((HandHeld) getProductInstance()).getCamera();
        }

        return camera;
    }

}

