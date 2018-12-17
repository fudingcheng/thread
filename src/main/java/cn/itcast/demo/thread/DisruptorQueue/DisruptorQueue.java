package cn.itcast.demo.thread.DisruptorQueue;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DisruptorQueue {
    //ringBuffer的大小
    private int ringBufferSize;
    //ringBuffer
    private RingBuffer ringBuffer;
    //线程池
    private Executor executor;
    //初始化disruptor
    private Disruptor<LongEvent> disruptor;

    /**
     * 单生产者
     */
    public void init() {
        ringBufferSize = 1024;
        executor = Executors.newFixedThreadPool(8);
        disruptor = new Disruptor<LongEvent>(new LongEventFactory(), ringBufferSize, executor);
        disruptor.handleEventsWith(new BroadcastConsumer("消费者1"));
        disruptor.start();
        ringBuffer = disruptor.getRingBuffer();
    }

    /**
     * 广播类型消费者
     * 单消费者
     */
    public void init2() {
        ringBufferSize = 1024;
        executor = Executors.newFixedThreadPool(8);
        disruptor = new Disruptor<LongEvent>(new LongEventFactory(), ringBufferSize, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(new BroadcastConsumer("消费者1"));
        disruptor.start();
        ringBuffer = disruptor.getRingBuffer();
    }

    /**
     * 广播类型消费者
     * 多消费者
     * 消费者1和消费者2同时获得消息,消费完毕后,消费者3才能消费
     */
    public void init3() {
        ringBufferSize = 1024;
        executor = Executors.newFixedThreadPool(8);
        disruptor = new Disruptor<LongEvent>(new LongEventFactory(), ringBufferSize, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(new BroadcastConsumer("消费者1"), new BroadcastConsumer("消费者2"), new BroadcastConsumer("消费者3"));
        disruptor.start();
        ringBuffer = disruptor.getRingBuffer();
    }

    /**
     * 分组类型消费者
     * 多消费者
     * 消费者1和消费者2同时获得消息,消费完毕后,消费者3才能消费
     */
    public void init4() {
        ringBufferSize = 1024;
        executor = Executors.newFixedThreadPool(8);
        disruptor = new Disruptor<LongEvent>(new LongEventFactory(), ringBufferSize, executor, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWithWorkerPool(new GroupConsumer("消费者1"), new GroupConsumer("消费者2"), new GroupConsumer("消费者3"));
        disruptor.start();
        ringBuffer = disruptor.getRingBuffer();
    }

    /**
     * 测试方法
     *
     * @throws InterruptedException
     */
    @Test
    public void createEvent() throws InterruptedException {
        //init();       //广播类型,单生产者
        //init2();      //广播类型,多生产者,消费者之间无关系
        //init3();      //广播类型,多消费者,消费者之间有关系,1和2消费完毕,3才能消费
        init4();        //分组类型消费者,组内只能有一个消费者消费消息
        int i = 1;
        for (; ; ) {
            ringBuffer.publishEvent(new Publish(), String.valueOf(i++));
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
