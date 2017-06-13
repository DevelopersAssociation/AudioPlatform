package com.fruitbasket.audioplatform.play;

/**
 * Created by FruitBasket on 2017/6/4.
 */

public class Sin19kWavPlayCommand extends PlayCommand {

    private Sin19kWavPlayer sin19kWavPlayer;

    public Sin19kWavPlayCommand(Sin19kWavPlayer sin19kWavPlayer){
        this.sin19kWavPlayer=sin19kWavPlayer;
    }

    @Override
    public void play() {
        sin19kWavPlayer.play();
    }

    @Override
    public void stop() {
        sin19kWavPlayer.stop();
    }

    @Override
    public void release() {
        sin19kWavPlayer.release();
    }
}
