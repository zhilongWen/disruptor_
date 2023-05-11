package com.at.disruptor._04_netty_disruptor.client;

import com.at.disruptor._04_netty_disruptor.common.TranslatorData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * @author zero
 * @create 2023-05-11
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            TranslatorData response = (TranslatorData) msg;
            System.out.println("Client 端接收 server 端回传的信息：= " + response.toString());
        } finally {
            //一定要注意 用完了缓存 要进行释放
            ReferenceCountUtil.release(msg);
        }
    }
}
