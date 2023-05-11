package com.at.disruptor._04_netty_disruptor.client;

import com.at.disruptor._04_netty_disruptor.common.entity.TranslatorData;
import com.at.disruptor._04_netty_disruptor.common.disruptor.MessageConsumer;
import com.at.disruptor._04_netty_disruptor.common.entity.TranslatorDataWapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

/**
 * @create 2023-05-12
 */
public class MessageConsumerImplClient extends MessageConsumer {

    public MessageConsumerImplClient(String consumerId) {
        super(consumerId);
    }

    @Override
    public void onEvent(TranslatorDataWapper event) throws Exception {
        TranslatorData response = event.getData();
        ChannelHandlerContext ctx = event.getCtx();
        //业务逻辑处理:
        try {
            System.err.println("Client端: id= " + response.getId()
                    + ", name= " + response.getName()
                    + ", message= " + response.getMessage());
        } finally {
            ReferenceCountUtil.release(response);
        }
    }
}
