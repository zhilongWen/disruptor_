package com.at.disruptor._04_netty_disruptor.server;

import com.at.disruptor._04_netty_disruptor.common.codec.MarshallingCodeCFactory;
import com.at.disruptor._04_netty_disruptor.common.disruptor.MessageConsumer;
import com.at.disruptor._04_netty_disruptor.common.disruptor.RingBufferWorkerPoolFactory;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author zero
 * @create 2023-05-10
 */
public class NettyServer {

    public static final int PORT = 8792;

    public static void main(String[] args) {


        MessageConsumer[] conusmers = new MessageConsumer[4];
        for(int i =0; i < conusmers.length; i++) {
            MessageConsumer messageConsumer = new MessageConsumerImplServer("code:serverId:" + i);
            conusmers[i] = messageConsumer;
        }
        RingBufferWorkerPoolFactory.getInstance().initAndStart(ProducerType.MULTI,
                1024*1024,
                new YieldingWaitStrategy(),
//                new BlockingWaitStrategy(),
                conusmers);







        EventLoopGroup boosGroup = null;
        EventLoopGroup workGroup = null;

        try {

            boosGroup = new NioEventLoopGroup();
            workGroup = new NioEventLoopGroup();

            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap
                    .group(boosGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    // 设置 SYNC 与 Accept 队列大小
                    .option(ChannelOption.SO_BACKLOG, 1024)

                    // 设置缓存大小
                    // AdaptiveRecvByteBufAllocator.DEFAULT 缓存区自动调配，有一定的性能损耗，适用于数据变化不大的场景
                    .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)

                    // 缓冲区池
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)

                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(
                            new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel ch) throws Exception {

                                    ch.pipeline()
                                            .addLast(MarshallingCodeCFactory.buildMarshallingDecoder())
                                            .addLast(MarshallingCodeCFactory.buildMarshallingEncoder())
                                            .addLast(new ServerHandler());

                                }
                            }
                    );


            ChannelFuture channelFuture = serverBootstrap.bind(PORT).sync();

            System.out.println("Server Startup...");

            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        System.out.println("Server 监听 port = " + PORT + " success...");
                    }
                }
            });

            channelFuture.channel().closeFuture().sync();


        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();

            System.out.println("Server ShutDown...");
        }

    }

}
