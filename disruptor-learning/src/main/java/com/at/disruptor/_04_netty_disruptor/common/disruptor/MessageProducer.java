package com.at.disruptor._04_netty_disruptor.common.disruptor;

import com.at.disruptor._04_netty_disruptor.common.entity.TranslatorData;
import com.at.disruptor._04_netty_disruptor.common.entity.TranslatorDataWapper;
import com.lmax.disruptor.RingBuffer;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author zero
 * @create 2023-05-12
 */
public class MessageProducer {

    private String producerId;

    private RingBuffer<TranslatorDataWapper> ringBuffer;

    public MessageProducer(String producerId, RingBuffer<TranslatorDataWapper> ringBuffer) {
        this.producerId = producerId;
        this.ringBuffer = ringBuffer;
    }

    public void put(TranslatorData event, ChannelHandlerContext ctx){

        long sequence = ringBuffer.next();

        try {

            TranslatorDataWapper wapper = ringBuffer.get(sequence);

            wapper.setCtx(ctx);
            wapper.setData(event);

        }finally {
            ringBuffer.publish(sequence);
        }

    }


}
