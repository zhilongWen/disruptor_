package com.at.disruptor._04_netty_disruptor.client;

import com.at.disruptor._04_netty_disruptor.common.codec.MarshallingCodeCFactory;
import com.at.disruptor._04_netty_disruptor.common.disruptor.MessageConsumer;
import com.at.disruptor._04_netty_disruptor.common.disruptor.RingBufferWorkerPoolFactory;
import com.at.disruptor._04_netty_disruptor.common.entity.TranslatorData;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author zero
 * @create 2023-05-10
 */
public class NettyClient {

    public static final String HOST = "127.0.0.1";
    public static final int PORT = 8792;



    public static void main(String[] args) {



        MessageConsumer[] conusmers = new MessageConsumer[4];
        for(int i =0; i < conusmers.length; i++) {
            MessageConsumer messageConsumer = new MessageConsumerImplClient("code:clientId:" + i);
            conusmers[i] = messageConsumer;
        }
        RingBufferWorkerPoolFactory.getInstance().initAndStart(ProducerType.MULTI,
                1024*1024,
                new YieldingWaitStrategy(),
//                new BlockingWaitStrategy(),
                conusmers);









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
