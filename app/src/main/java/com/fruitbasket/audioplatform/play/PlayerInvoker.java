package com.fruitbasket.audioplatform.play;

/**
 * Created by FruitBasket on 2017/5/26.
 */

public class PlayerInvoker {
    private PlayCommand playCommand;

    public void setCommand(PlayCommand playCommand){
        this.playCommand = playCommand;
    }

    public void play(){
        if(playCommand !=null){
            playCommand.play();
        }
    }

    public void stop(){
        if(playCommand !=null){
            playCommand.stop();
        }
    }

    public void release(){
        if(playCommand !=null){
            playCommand.release();
        }
    }
}
