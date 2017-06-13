package com.fruitbasket.audioplatform.record;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.fruitbasket.audioplatform.AppCondition;
import com.fruitbasket.audioplatform.MyApp;
import com.fruitbasket.audioplatform.WavHeader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by FruitBasket on 2017/6/5.
 */

public class WavRecorder extends Recorder {
    private static final String TAG="..WavRecorder";

    private boolean isRecording;
    private String audioName;//录音文件的名字

    public WavRecorder(){
        super();
        audioName =getRecordedFileName("pcm");
    }

    public WavRecorder(int channelIn, int sampleRate, int encoding){
        super(channelIn,sampleRate,encoding);
        audioName =getRecordedFileName("pcm");
    }

    @Override
    public boolean start() {
        //使用异步的方法录制音频
        new Thread(new Runnable() {
            @Override
            public void run() {

                int bufferSize = AudioRecord.getMinBufferSize(
                        sampleRate,
                        channelIn,
                        AudioFormat.ENCODING_PCM_16BIT);
                if (bufferSize == AudioRecord.ERROR_BAD_VALUE) {
                    Log.e(TAG, "recordingBufferSize==AudioRecord.ERROR_BAD_VALUE");
                    return;
                } else if (bufferSize == AudioRecord.ERROR) {
                    Log.e(TAG, "recordingBufferSize==AudioRecord.ERROR");
                    return;
                }
                byte[] buffer = new byte[bufferSize];

                try {
                    File audioFile;
                    DataOutputStream output;

                    if (Environment.getExternalStorageState()
                            .equals(android.os.Environment.MEDIA_MOUNTED)){
                        Log.i(TAG,"make1: if the device has got a external storage");
                        // 如果外存存在
                        audioFile=new File(AppCondition.getAppExternalDir()+File.separator+audioName);

                        output= new DataOutputStream(
                                new BufferedOutputStream(
                                        new FileOutputStream(audioFile)
                                )
                        );
                    }
                    else{
                        Log.i(TAG,"mark2: the device has not got a external storage");
                        //否则
                        output= new DataOutputStream(
                                new BufferedOutputStream(
                                        MyApp.getContext().openFileOutput(audioName, Context.MODE_PRIVATE)
                                )
                        );
                        audioFile=MyApp.getContext().getFileStreamPath(audioName);
                    }

                    AudioRecord audioRecord = new AudioRecord(
                            MediaRecorder.AudioSource.MIC,
                            sampleRate,
                            channelIn,
                            encoding,
                            bufferSize);
                    audioRecord.startRecording();

                    isRecording = true;
                    while (isRecording) {
                        int readResult = audioRecord.read(buffer, 0, bufferSize);
                        if (readResult == AudioRecord.ERROR_INVALID_OPERATION) {
                            Log.e(TAG, "readState==AudioRecord.ERROR_INVALID_OPERATION");
                            return;
                        } else if (readResult == AudioRecord.ERROR_BAD_VALUE) {
                            Log.e(TAG, "readState==AudioRecord.ERROR_BAD_VALUE");
                            return;
                        } else {
                            for (int i = 0; i < readResult; i++) {
                                output.writeByte(buffer[i]);
                            }
                        }
                    }

                    //结束以上循环后就停止播放并释放资源
                    audioRecord.stop();
                    output.flush();
                    output.close();
                    audioRecord.release();
                    audioRecord = null;

                    Log.i(TAG, "begin to make wav file");
                    //制作wav文件
                    ///这里先将原始音频保存起来，在改装成wav文件，这不是一个好做法
                    BufferedInputStream inputStream;
                    BufferedOutputStream outputStream;
                    int length;
                    if(Environment.getExternalStorageState()
                            .equals(android.os.Environment.MEDIA_MOUNTED)){
                        Log.i(TAG,"if the device has got a external storage");
                        //如果外存存在
                        FileInputStream fis = new FileInputStream(audioFile);
                        inputStream= new BufferedInputStream(fis);

                        outputStream = new BufferedOutputStream(
                                new FileOutputStream(AppCondition.getAppExternalDir()+File.separator+audioName + ".wav")
                        );
                        length= (int) fis.getChannel().size();
                    }
                    else{
                        //否则
                        FileInputStream fis=MyApp.getContext().openFileInput(audioName);
                        inputStream= new BufferedInputStream(fis);

                        outputStream=new BufferedOutputStream(
                                MyApp.getContext().openFileOutput(audioName+".wav",Context.MODE_PRIVATE)
                        );
                        length=(int)fis.getChannel().size();

                    }

                    byte[] readBuffer = new byte[1024];

                    Log.i(TAG, "create a wav file header");
                    WavHeader wavHeader = new WavHeader();
                    wavHeader.setAdjustFileLength(length - 8);
                    wavHeader.setAudioDataLength(length - 44);
                    wavHeader.setBlockAlign(channelIn, encoding);
                    wavHeader.setByteRate(channelIn, sampleRate, encoding);
                    wavHeader.setChannelCount(channelIn);
                    wavHeader.setEncodingBit(encoding);
                    wavHeader.setSampleRate(sampleRate);
                    wavHeader.setWaveFormatPcm(WavHeader.WAV_FORMAT_PCM);

                    outputStream.write(wavHeader.getHeader());
                    while (inputStream.read(readBuffer) != -1) {
                        outputStream.write(readBuffer);
                    }
                    inputStream.close();
                    outputStream.close();
                    audioFile.delete();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }).start();
        return true;
    }

    @Override
    public boolean stop() {
        isRecording = false;
        return true;
    }
}
