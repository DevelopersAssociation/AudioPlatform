package com.fruitbasket.audioplatform.record;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.fruitbasket.audioplatform.WavHeader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @deprecated 
 * Created by FruitBasket on 2017/5/31.
 */

public class WavRecorderOld extends Recorder {
    private static final String TAG = ".record.WavRecorderOld";

    private String audioFullPath = null;//录音文件的存放路径
    private boolean isRecording;

    public WavRecorderOld() {
        super();
        this.audioFullPath = getRecordedFileName("pcm");
    }

    public WavRecorderOld(int channelIn, int sampleRate, int encoding, String audioFullPath) {
        super(channelIn, sampleRate, encoding);
        this.audioFullPath = audioFullPath;
    }

    @Override
    public boolean start() {
        Log.i(TAG, "start()");
        //使用异步的方法录制音频
        new Thread(new Runnable() {
            @Override
            public void run() {
                File audioFile = new File(audioFullPath);

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
                    DataOutputStream output = new DataOutputStream(
                            new BufferedOutputStream(
                                    new FileOutputStream(audioFile)
                            )
                    );
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

                    Log.i(TAG, "make wav file");
                    //制作wav文件
                    ///这里先将原始音频保存起来，在改装成wav文件，这不是一个好做法
                    FileInputStream fis = new FileInputStream(audioFile);
                    BufferedInputStream inputStream = new BufferedInputStream(fis);

                    BufferedOutputStream outputStream = new BufferedOutputStream(
                            new FileOutputStream(audioFullPath + ".wav")
                    );
                    byte[] readBuffer = new byte[1024];

                    int length = (int) fis.getChannel().size();

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