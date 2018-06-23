package com.robin.karaoke;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

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


public class KaraokeManager {

    private static String TAG = "KaraokeManager";

    private boolean mRunning = false;

    private FifoQueue mDataQueue = new FifoQueue();

    /**
     * 读取录音数据立即写入播放缓存，返听延迟比开两个线程要小很多。
     */
    private void audioCaptureAndPlayback() {
        int captureBuffsize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        AudioRecord mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100,
                AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, captureBuffsize);

        int playbackbuffsize = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        AudioTrack mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, playbackbuffsize, AudioTrack.MODE_STREAM);

        if (null != mAudioRecord && null != mAudioTrack) {
            int readBuffersize = 1024;
            short buffer[] = new short[readBuffersize];
            int avail = 0;
            mRunning = true;
            mAudioRecord.startRecording();
            mAudioTrack.play();
            while (mRunning) {
                Log.d(TAG, "capture playback running!");
                avail = mAudioRecord.read(buffer, 0, readBuffersize);
                mAudioTrack.write(buffer, 0, avail);
            }
            mAudioRecord.stop();
            mAudioRecord.release();
            mAudioTrack.stop();
            mAudioTrack.release();
        }
    }

    /**
     * 录音
     */
    private void capture() {
        int buffsize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        AudioRecord mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100,
                AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, buffsize);


        if (null != mAudioRecord) {
            int captureBufferSize = 1024;
            short buffer[] = new short[captureBufferSize];
            mAudioRecord.startRecording();
            int avail;
            mRunning = true;
            while (mRunning) {
                Log.d(TAG, " capture running!");
                avail = mAudioRecord.read(buffer, 0, captureBufferSize);
                if(0 < avail){
                    mDataQueue.inData(buffer, avail);
                }
            }
            mAudioRecord.stop();
            mAudioRecord.release();
        }
    }

    /**
     * 播放
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void playback() {
        int buffsize = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        AudioTrack mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, buffsize, AudioTrack.MODE_STREAM);

        if (null != mAudioTrack) {
            int playbackBufferSize = 1024;
            short buffer[] = new short[playbackBufferSize];
            int availLen = 0;
            mRunning = true;
            mAudioTrack.play();
            while (mRunning) {
                Log.d(TAG, "playback running");
                availLen = mDataQueue.outData(buffer, playbackBufferSize);
                if (0 < availLen) {
                    mAudioTrack.write(buffer, 0, availLen);
                }
            }
            mAudioTrack.stop();
            mAudioTrack.release();
        }
    }

    public boolean isRunning(){
        return mRunning;
    }

    public void stop() {
        mRunning = false;
    }

    public void StartAudioCaptureAndPlayback() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                audioCaptureAndPlayback();
            }
        }).start();
    }

    public void startPlayback() {
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                playback();
            }
        }).start();
    }


    public void startCapture() {
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                capture();
            }
        }).start();
    }
}
