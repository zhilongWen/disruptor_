package com.at.disruptor._04_netty_disruptor.client;

import com.at.disruptor._04_netty_disruptor.common.MarshallingCodeCFactory;
import com.at.disruptor._04_netty_disruptor.common.TranslatorData;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.Optional;

/**
 * @author zero
 * @create 2023-05-10
 */
public class NettyClient {

    public static final String HOST = "127.0.0.1";
    public static final int PORT = 8792;



    public static void main(String[] args) {

        EventLoopGroup group = null;

        try {

            group = new NioEventLoopGroup();

            Bootstrap bootstrap = new Bootstrap();

            bootstrap
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(MarshallingCodeCFactory.buildMarshallingDecoder())
                                    .addLast(MarshallingCodeCFactory.buildMarshallingEncoder())
                                    .addLast(new ClientHandler());
                        }
                    });

            // 绑定端口，同步等等请求连接
            ChannelFuture channelFuture = bootstrap.connect(HOST, PORT).sync();
            System.out.println("Client connected...");

            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()){
                        System.out.println("Client connect host = " + HOST +" port = " + PORT + " success...");
                    }
                }
            });

            Channel channel = channelFuture.channel();
            send(channel);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
//            group.shutdownGracefully();
//            System.out.println("Client ShutDown...");
        }

    }


    public static void send(Channel ch){
        for(int i =0; i <10; i++){
            TranslatorData request = new TranslatorData();
            request.setId("" + i);
            request.setName("请求消息名称 " + i);
            request.setMessage("请求消息内容 " + i);
            ch.writeAndFlush(request);
        }
    }

}
