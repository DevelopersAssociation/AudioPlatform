package com.fruitbasket.audioplatform;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fruitbasket.audioplatform.play.PlayCommand;
import com.fruitbasket.audioplatform.play.PlayerInvoker;
import com.fruitbasket.audioplatform.play.Sin19kWavPlayCommand;
import com.fruitbasket.audioplatform.play.Sin19kWavPlayer;
import com.fruitbasket.audioplatform.play.WavePlayCommand;
import com.fruitbasket.audioplatform.play.WavePlayer;
import com.fruitbasket.audioplatform.record.RecordCommand;
import com.fruitbasket.audioplatform.record.RecorderInvoker;
import com.fruitbasket.audioplatform.record.WavRecordCommand;
import com.fruitbasket.audioplatform.record.WavRecorder;

/**
 * Created by FruitBasket on 2017/5/26.
 */

final public class AudioService extends Service {
    private final static String TAG=".AudioService";

    private PlayerInvoker playerInvoker=new PlayerInvoker();
    private RecorderInvoker recorderInvoker=new RecorderInvoker();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG,"onBind()");
        return new AudioServiceBinder();
    }

    @Override
    public boolean onUnbind(Intent intent){
        Log.i(TAG,"onUnbind()");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy(){
        Log.i(TAG,"onDestroy()");
        super.onDestroy();
    }

    /**
     * 开始播放声波
     * @param channelOut
     * @param waveRate
     */
    public void startPlayWav(int channelOut,int waveRate){
        Log.i(TAG,"startPlayWav");
        PlayCommand wavePlayCommand =new WavePlayCommand(
                new WavePlayer(
                        channelOut,
                        waveRate
                )
        );
        playerInvoker.setCommand(wavePlayCommand);
        playerInvoker.play();
    }

    public void startPlayWav(int channelOut, int waveRate, int waveType, int sampleRate){
        PlayCommand wavePlayCommand =new WavePlayCommand(
                new WavePlayer(
                        channelOut,
                        waveRate,
                        waveType,
                        sampleRate
                )
        );
        playerInvoker.setCommand(wavePlayCommand);
        playerInvoker.play();
    }

    public void startPlaySin19kWav(){
        PlayCommand sin19kWavPlayCommand=new Sin19kWavPlayCommand(
                new Sin19kWavPlayer()
        );
        playerInvoker.setCommand(sin19kWavPlayCommand);
        playerInvoker.play();
    }

    /**
     * 停止播放声波
     */
    public void stopPlay(){
        playerInvoker.stop();
    }

    /**
     * 释放播放器资源
     */
    public void releasePlayer(){
        playerInvoker.release();
    }

    /**
     * 开始录制wav格式的音频
     */
    public void startRecordWav(){
        Log.i(TAG,"startRecordWav()");
        RecordCommand recordCommand=new WavRecordCommand(
                new WavRecorder()
        );
        recorderInvoker.setCommand(recordCommand);
        recorderInvoker.start();
    }

    /**
     * 停止录制音频
     */
    public void stopRecord(){
        Log.i(TAG,"stopRecord()");
        recorderInvoker.stop();
    }

    public class AudioServiceBinder extends Binder {
        public AudioService getService(){
            return AudioService.this;
        }
    }
}
