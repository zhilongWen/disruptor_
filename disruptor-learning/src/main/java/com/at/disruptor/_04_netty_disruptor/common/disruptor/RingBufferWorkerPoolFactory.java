package com.at.disruptor._04_netty_disruptor.common.disruptor;

import com.at.disruptor._04_netty_disruptor.common.entity.TranslatorDataWapper;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 * @author zero
 * @create 2023-05-12
 */
public class RingBufferWorkerPoolFactory {

    private RingBufferWorkerPoolFactory(){}

    private static class SingletonHolder{
        static final RingBufferWorkerPoolFactory instance = new RingBufferWorkerPoolFactory();
    }

    public static RingBufferWorkerPoolFactory getInstance(){
        return SingletonHolder.instance;
    }

    private static Map<String, MessageConsumer> consumers = new ConcurrentHashMap<String, MessageConsumer>();
    private static Map<String, MessageProducer> producers = new ConcurrentHashMap<String, MessageProducer>();

    private RingBuffer<TranslatorDataWapper> ringBuffer;

    public void initAndStart(ProducerType producerType,int bufferSize, WaitStrategy waitStrategy,MessageConsumer[] messageConsumers){

        //1.构建 Ringbuffer
        this.ringBuffer = RingBuffer
                .create(
                        producerType,
                        new EventFactory<TranslatorDataWapper>() {
                            @Override
                            public TranslatorDataWapper newInstance() {
                                return new TranslatorDataWapper();
                            }
                        },
                        bufferSize,
                        waitStrategy
                );

        //2.设置序号栅栏
        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();

        //3.设置工作池
        WorkerPool<TranslatorDataWapper> workerPool = new WorkerPool<>(
                ringBuffer,
                sequenceBarrier,
                new EventExceptionHandler(),
                messageConsumers
        );

        //4 把所构建的消费者置入池中
        for (MessageConsumer consumer : messageConsumers) {
            consumers.put(consumer.consumerId, consumer);
        }

        //5 添加 sequences
        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());

        //6 启动我们的工作池
        workerPool.start(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 2));


    }

    public MessageProducer getMessageProducer(String producerId){

        MessageProducer messageProducer = producers.get(producerId);

        if (null == messageProducer){
            messageProducer = new MessageProducer(producerId,ringBuffer);
            producers.put(producerId,messageProducer);
        }

        return messageProducer;


    }

    /**
     * 异常静态类
     * @author Alienware
     *
     */
    static class EventExceptionHandler implements ExceptionHandler<TranslatorDataWapper> {
        @Override
        public void handleEventException(Throwable ex, long sequence, TranslatorDataWapper event) {
        }

        @Override
        public void handleOnStartException(Throwable ex) {
        }

        @Override
        public void handleOnShutdownException(Throwable ex) {
        }
    }

}
