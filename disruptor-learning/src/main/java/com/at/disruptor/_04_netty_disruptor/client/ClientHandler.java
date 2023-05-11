package com.at.disruptor._04_netty_disruptor.client;

import com.at.disruptor._04_netty_disruptor.common.entity.TranslatorData;
import com.at.disruptor._04_netty_disruptor.common.disruptor.MessageProducer;
import com.at.disruptor._04_netty_disruptor.common.disruptor.RingBufferWorkerPoolFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author zero
 * @create 2023-05-11
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        try {
//            TranslatorData response = (TranslatorData) msg;
//            System.out.println("Client 端接收 server 端回传的信息：= " + response.toString());
//        } finally {
//            //一定要注意 用完了缓存 要进行释放
//            ReferenceCountUtil.release(msg);
//        }


        TranslatorData response = (TranslatorData)msg;
        String producerId = "code:seesionId:002";
        MessageProducer messageProducer = RingBufferWorkerPoolFactory.getInstance().getMessageProducer(producerId);
        messageProducer.put(response, ctx);

    }
}
