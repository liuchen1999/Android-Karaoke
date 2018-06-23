package com.robin.karaoke;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * @License:
 *
 * @Version:
 *
 * @Author: robin
 *
 * @Email: 752070569@qq.com
 *
 * @Description: 录音返听实现
 *
 * @Date:2018/6/23
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = "MainActivity";

    KaraokeManager mKaraokeManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.record).setOnClickListener(this);
        findViewById(R.id.stop).setOnClickListener(this);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mKaraokeManager = new KaraokeManager();
        } else {
            if (checkPermission(Manifest.permission.RECORD_AUDIO, Binder.getCallingPid(),
                    Binder.getCallingUid()) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1);
            } else {
                mKaraokeManager = new KaraokeManager();
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.record:
                if (null != mKaraokeManager && !mKaraokeManager.isRunning()) {
                    //双线程读写数据，延迟太大
//                    mKaraokeManager.startCapture();
//                    mKaraokeManager.startPlayback();
                    mKaraokeManager.StartAudioCaptureAndPlayback();
                }
                break;
            case R.id.stop:
                if (null != mKaraokeManager && mKaraokeManager.isRunning()) {
                    mKaraokeManager.stop();
                }
                break;

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mKaraokeManager = new KaraokeManager();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != mKaraokeManager && mKaraokeManager.isRunning()) {
            mKaraokeManager.stop();
        }
    }
}
