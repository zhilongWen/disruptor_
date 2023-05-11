package com.at.disruptor._04_netty_disruptor.server;

import com.at.disruptor._04_netty_disruptor.common.entity.TranslatorData;
import com.at.disruptor._04_netty_disruptor.common.disruptor.MessageConsumer;
import com.at.disruptor._04_netty_disruptor.common.entity.TranslatorDataWapper;
import io.netty.channel.ChannelHandlerContext;

/**
 * @create 2023-05-12
 */
public class MessageConsumerImplServer extends MessageConsumer {

    public MessageConsumerImplServer(String consumerId) {
        super(consumerId);
    }

    @Override
    public void onEvent(TranslatorDataWapper event) throws Exception {
        TranslatorData request = event.getData();
        ChannelHandlerContext ctx = event.getCtx();
        //1.业务处理逻辑:
        System.err.println("Sever端: id= " + request.getId()
                + ", name= " + request.getName()
                + ", message= " + request.getMessage());

        //2.回送响应信息:
        TranslatorData response = new TranslatorData();
        response.setId("resp: " + request.getId());
        response.setName("resp: " + request.getName());
        response.setMessage("resp: " + request.getMessage());
        //写出response响应信息:
        ctx.writeAndFlush(response);
    }
}
