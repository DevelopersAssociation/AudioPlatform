package com.fruitbasket.audioplatform.play;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.fruitbasket.audioplatform.AppCondition;
import com.fruitbasket.audioplatform.WaveProducer;

/**
 * Created by FruitBasket on 2017/5/26.
 */

public class WavePlayer extends Player {
    private final static String TAG=".play.WavePlayer";

    private int waveRate;//声波的频率
    @WaveProducer.WaveType private int waveType;//声波的类型///此变量暂时使用
    private int sampleRate;//设备实际的发声频率

    private AudioTrack audioTrack;

    public WavePlayer(int channelOut,int waveRate){
        this(channelOut,waveRate,WaveProducer.SIN, AppCondition.DEFAULE_SIMPLE_RATE);
    }

    public WavePlayer(int channelOut, int waveRate, @WaveProducer.WaveType int waveType, int sampleRate){
        super(channelOut);
        this.waveType=waveType;
        this.waveRate=waveRate;
        this.sampleRate=sampleRate;
    }

    @Override
    public void play() {
        final int bufferSize = 3* AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        Log.i(TAG, "play() : bufferSize==" + bufferSize);

        audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM);

        switch (channelOut) {
            case CHANNEL_OUT_LEFT:
                audioTrack.setStereoVolume(0.5f, 0.0f);
                break;
            case CHANNEL_OUT_RIGHT:
                audioTrack.setStereoVolume(0.0f, 0.5f);
                break;
            case CHANNEL_OUT_BOTH:
                audioTrack.setVolume(0.5f);
                break;
            default:
                audioTrack.setVolume(0.5f);
                Log.w(TAG,"play() : channel error");
        }

        audioTrack.play();

        new Thread(new Runnable() {
            @Override
            public void run() {

                int writeBytes;

                //放声方法一stream
                double sampleCountInWave = sampleRate / (double) waveRate;//每一个波中，包含的样本点数量
                short[] wave = new short[bufferSize];
                int index = 0;
                while (audioTrack != null && audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {

                    for (int i = 0; i < wave.length; ++i, ++index) {
                        wave[i] = (short) (Short.MAX_VALUE/2 *
                                Math.sin(2.0 * Math.PI * index / sampleCountInWave)
                        );
                    }

                    writeBytes=audioTrack.write(wave, 0, wave.length);
                    Log.d(TAG,"play() : writeBytes="+writeBytes);
                }

                //发声方法二static
                /*short[] wave= WaveProducer.getWave(waveType,waveRate,sampleRate,bufferSize);

                while (audioTrack != null && audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                    writeBytes=audioTrack.write(wave, 0, wave.length);
                    Log.d(TAG,"play() : writeBytes="+writeBytes);
                }*/

            }
        }).start();

    }

    @Override
    public void stop() {
        if(audioTrack!=null){
            audioTrack.stop();
        }
    }

    @Override
    public void release() {
        if(audioTrack !=null){
            audioTrack.release();
            audioTrack =null;
        }
    }

}
