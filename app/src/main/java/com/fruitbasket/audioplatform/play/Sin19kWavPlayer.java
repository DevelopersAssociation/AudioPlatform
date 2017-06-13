package com.fruitbasket.audioplatform.play;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;


import com.fruitbasket.audioplatform.MyApp;
import java.io.IOException;

/**
 * Created by FruitBasket on 2017/6/4.
 */

public class Sin19kWavPlayer extends Player {
    private static final String TAG="..Sin19kWavPlayer";

    private MediaPlayer mediaPlayer;

    public Sin19kWavPlayer(){
        super();
        initialize();
    }

    public Sin19kWavPlayer(int channelOut){
        super(channelOut);
        initialize();
    }

    @Override
    public void play() {
        switch (channelOut) {
            case CHANNEL_OUT_LEFT:
                mediaPlayer.setVolume(0.5f, 0.0f);
                break;
            case CHANNEL_OUT_RIGHT:
                mediaPlayer.setVolume(0.0f, 0.5f);
                break;
            case CHANNEL_OUT_BOTH:
                mediaPlayer.setVolume(0.5f, 0.5f);
                break;
            default:
                Log.w(TAG,"play() : channel error");
        }
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    @Override
    public void stop() {
        mediaPlayer.stop();
    }

    @Override
    public void release() {
        mediaPlayer.release();
    }

    private void initialize(){
        mediaPlayer=new MediaPlayer();
        try {
            AssetFileDescriptor descriptor= MyApp.getContext().getAssets().openFd("sin_19k_1s.wav");
            mediaPlayer.setDataSource(descriptor.getFileDescriptor());
            mediaPlayer.prepare();///目前这里会出错
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
