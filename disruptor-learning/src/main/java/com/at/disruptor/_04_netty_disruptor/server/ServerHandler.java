package com.at.disruptor._04_netty_disruptor.server;

import com.at.disruptor._04_netty_disruptor.common.entity.TranslatorData;
import com.at.disruptor._04_netty_disruptor.common.disruptor.MessageProducer;
import com.at.disruptor._04_netty_disruptor.common.disruptor.RingBufferWorkerPoolFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


/**
 * @author zero
 * @create 2023-05-11
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

//        TranslatorData request = (TranslatorData) msg;
//
//        System.out.println("Server 端接收到的信息：" + request.toString());
//
//        TranslatorData response = new TranslatorData();
//        response.setId("resp: " + request.getId());
//        response.setName("resp: " + request.getName());
//        response.setMessage("resp: " + request.getMessage());
//
//        // 写出response响应信息:
//        // 使用 writeAndFlush 会自动释放缓存：ReferenceCountUtil.release(msg)
//        ctx.writeAndFlush(response);

        TranslatorData request = (TranslatorData) msg;

        // 生成一个 ID
        String producerId = "code:sessionId:001";

        MessageProducer messageProducer = RingBufferWorkerPoolFactory.getInstance().getMessageProducer(producerId);

        messageProducer.put(request,ctx);


    }
}
