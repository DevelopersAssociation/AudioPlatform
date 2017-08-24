package com.fruitbasket.audioplatform.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioFormat;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.fruitbasket.audioplatform.AppCondition;
import com.fruitbasket.audioplatform.AudioService;
import com.fruitbasket.audioplatform.R;
import com.fruitbasket.audioplatform.play.Player;
import com.fruitbasket.audioplatform.play.WavePlayer;

final public class MainActivity extends Activity {
    private static final String TAG=".MainActivity";

    private RadioGroup channelOutRG;
    private ToggleButton waveProducerTB;
    private SeekBar waveRateSB;
    private TextView waveRateTV;

    private RadioGroup channelInRG;
    private ToggleButton recorderTB;
    private Button createDirB;

    private ToggleButton testTB;

    private int channelOut= Player.CHANNEL_OUT_BOTH;
    private int channelIn= AudioFormat.CHANNEL_IN_MONO;
    private int waveRate;//声波的频率


    private Intent intent;
    private AudioService audioService;
    private ServiceConnection serviceConnection=new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.d(TAG,"ServiceConnection.onServiceConnection()");
            audioService =((AudioService.AudioServiceBinder)binder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG,"ServiceConnection.onServiceDisConnection()");
            audioService =null;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreate()");
        setContentView(R.layout.activity_main);
        initializeViews();
        intent=new Intent(this,AudioService.class);
        if(audioService ==null) {
            Log.i(TAG,"begin to bind service");
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            //do not execute startService()
        }

        ///测试
        /*File dir=new File(AppCondition.getAppExternalDir());
        if(dir.exists()){
            Log.d(TAG,"dir.exists()==true : "+AppCondition.getAppExternalDir()+"is exists");
            String [] names=dir.list();
            if(names!=null){
                StringBuilder stringBuilder=new StringBuilder("the file in "+dir.getAbsolutePath()+": \n");
                for(String name:names){
                    stringBuilder.append(name+'\n');
                }
                Log.d(TAG,stringBuilder.toString());
            }
            else{
                Log.d(TAG,"names==null: there is not file in "+dir.getAbsolutePath());
            }
        }
        else{
            Log.d(TAG,"dir.exists()==false : "+AppCondition.getAppExternalDir()+"is not exists");
        }*/
        ///

        ///测试：查看数据程序数据文件夹
		/*Log.i(TAG,"the name of data files dir : getFilesDir().getName()="+MyApp.getContext().getFilesDir().getName());
		StringBuffer stringBuffer=new StringBuffer("All the files in the data files dir : \\n");
		String[] fileNames=MyApp.getContext().fileList();
		for(String name:fileNames){
			stringBuffer.append(name+'\n');
		}
		Log.i(TAG,stringBuffer.toString());*/
        ///
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.i(TAG,"onStart()");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.i(TAG,"onResume()");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.i(TAG,"onPause()");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.i(TAG,"onStop()");
    }

    @Override
    protected void onDestroy(){
        Log.i(TAG,"onDestroy()");
        //在断开声音服务前，释放播放器资源
        if(audioService!=null){
            audioService.releasePlayer();
        }
        unbindService(serviceConnection);
        stopService(intent);//must stop the Service
        super.onDestroy();
    }

    private void initializeViews(){
        ToggleCheckedChangeListener tcListener=new ToggleCheckedChangeListener();

        channelOutRG =(RadioGroup)findViewById(R.id.channel_out_rg);
        channelOutRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.channel_out_left_rb:
                        channelOut= WavePlayer.CHANNEL_OUT_LEFT;
                        break;
                    case R.id.channel_out_right_rb:
                        channelOut= WavePlayer.CHANNEL_OUT_RIGHT;
                        break;
                    default:
                        Log.w(TAG,"initializeViews() : channelOut error");
                        channelOut=WavePlayer.CHANNEL_OUT_BOTH;
                }
            }
        });

        waveProducerTB =(ToggleButton)findViewById(R.id.wave_player_tb);
        waveProducerTB.setOnCheckedChangeListener(tcListener);

        waveRateTV =(TextView)findViewById(R.id.waverate_tv);

        waveRateSB =(SeekBar)findViewById(R.id.waverate_sb);
        waveRateSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                waveRate =progress*1000;
                waveRateTV.setText(getResources().getString(R.string.frequency,progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        waveRate = waveRateSB.getProgress()*1000;
        waveRateTV.setText(getResources().getString(R.string.frequency,waveRateSB.getProgress()));

        channelInRG=(RadioGroup)findViewById(R.id.channel_in_rg);
        channelInRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.channel_in_mono_rb:
                        channelIn=AudioFormat.CHANNEL_IN_MONO;
                        break;
                    case R.id.channel_in_stereo_rb:
                        channelIn=AudioFormat.CHANNEL_IN_STEREO;
                        break;
                    default:
                        Log.w(TAG,"initializeViews() : channelIn error");
                        channelIn=AudioFormat.CHANNEL_IN_MONO;
                }
            }
        });
        recorderTB=(ToggleButton)findViewById(R.id.recorder_tb);
        recorderTB.setOnCheckedChangeListener(tcListener);

        createDirB=(Button)findViewById(R.id.create_dir_b);
        createDirB.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.i(TAG,"onClick()");
                if(audioService!=null){
                    audioService.updateSubDir();
                }
                else{
                    Log.w(TAG,"audioService==null");
                }
                Toast.makeText(MainActivity.this,"dir created", Toast.LENGTH_SHORT).show();
            }
        });

        testTB=(ToggleButton)findViewById(R.id.test_tb);
        testTB.setOnCheckedChangeListener(tcListener);
    }

    private class ToggleCheckedChangeListener implements CompoundButton.OnCheckedChangeListener{
        private static final String TAG="...TCCListener";

        @Override
        public void onCheckedChanged(CompoundButton button,boolean isChecked){
            switch (button.getId()) {
                case R.id.wave_player_tb:
                    if (isChecked) {
                        startPlayWav();
                    } else {
                        stopPlay();
                    }
                    break;
                case R.id.recorder_tb:
                    if (isChecked) {
                        startRecordWav();
                    } else {
                        stopRecord();
                    }
                    break;
                case R.id.test_tb:
                    if (isChecked) {
                        startTest();
                    }
                    else{
                        stopTest();
                    }
                default:
                    Log.w(TAG,"onClick(): id error");
            }
        }

        private void startPlayWav(){
            Log.i(TAG,"starPlayWav()");
            if(audioService !=null){
                audioService.startPlayWav(channelOut,waveRate);
            }
            else{
                Log.w(TAG,"audioService==null");
            }
        }

        private void stopPlay(){
            Log.i(TAG,"stopPlay()");
            if(audioService !=null){
                audioService.stopPlay();
            }
            else{
                Log.w(TAG,"audioService==null");
            }
        }

        private void startRecordWav(){
            Log.i(TAG,"startRecordWav()");
            if(audioService!=null){
                audioService.startRecordWav(
                        channelIn,
                        AppCondition.DEFAULE_SIMPLE_RATE,
                        AudioFormat.ENCODING_PCM_16BIT
                );
            }
            else{
                Log.w(TAG,"audioService==null");
            }
        }

        private void stopRecord(){
            Log.i(TAG,"stopRecord()");
            if(audioService!=null){
                audioService.stopRecord();
            }
            else{
                Log.w(TAG,"audioService==null");
            }
        }

        private void startTest(){
            Log.i(TAG,"startTest()");
            if(audioService!=null){
                audioService.startRecordTest();
            }
            else{
                Log.w(TAG,"audioService==null");
            }
        }

        private void stopTest(){
            Log.i(TAG,"stopTest()");
            if(audioService!=null){
                audioService.stopRecord();
            }
            else{
                Log.w(TAG,"audioService==null");
            }
        }
    }
}
