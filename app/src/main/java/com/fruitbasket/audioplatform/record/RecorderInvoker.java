package com.fruitbasket.audioplatform.record;

/**
 * Created by FruitBasket on 2017/5/31.
 */

public class RecorderInvoker {
    private RecordCommand recordCommand;

    public void setCommand(RecordCommand recordCommand){
        this.recordCommand = recordCommand;
    }

    public void start(){
        recordCommand.start();
    }

    public void stop(){
        recordCommand.stop();
    }

}
