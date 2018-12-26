package cn.itcast.demo.thread.DisruptorQueue;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class DisruptorQueue {
    //环形缓冲区大小,大小必须为2的倍数
    private int ringBufferSize=1024;
    //环形缓冲区
    private RingBuffer ringBuffer;
    //线程工厂
    private ThreadFactory factory = Executors.defaultThreadFactory();
    //disruptor队列对象
    private Disruptor<LongEvent> disruptor;



    /**
     * 单生产者,单消费者
     */
    public void init() {
        //创建disrupt队列对象 参数1:事件工厂; 参数2:缓冲区大小; 参数3:线程工厂
        disruptor = new Disruptor<LongEvent>(new LongEventFactory(), ringBufferSize, factory);
        //队列对象绑定消费者
        disruptor.handleEventsWith(new BroadcastConsumer("消费者1"));
        //启动队列
        disruptor.start();
        //初始化缓冲区
        ringBuffer = disruptor.getRingBuffer();
    }

    /**
     * 多生产者,单消费者
     */
    public void init2() {
        //创建disrupt队列对象 参数1:事件工厂; 参数2:缓冲区大小; 参数3:线程工厂; 参数4:生产者类型(多生产者); 参数5:线程阻塞策略
        disruptor = new Disruptor<LongEvent>(new LongEventFactory(), ringBufferSize, factory, ProducerType.MULTI, new BlockingWaitStrategy());
        //队列对象绑定消费者
        disruptor.handleEventsWith(new BroadcastConsumer("消费者1"));
        //队列启动
        disruptor.start();
        //初始化缓冲区
        ringBuffer = disruptor.getRingBuffer();
    }

    /**
     * 多生产者,多消费者(广播)
     * 消费者1和消费者2同时获得消息,消费完毕后,消费者3才能消费
     */
    public void init3() {
        disruptor = new Disruptor<LongEvent>(new LongEventFactory(), ringBufferSize, factory, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(new BroadcastConsumer("消费者1"), new BroadcastConsumer("消费者2")).then(new BroadcastConsumer("消费者3"));
        disruptor.start();
        ringBuffer = disruptor.getRingBuffer();
    }

    /**
     * 分组类型消费者,组内只能有1个消费者处理消息
     */
    public void init4() {
        disruptor = new Disruptor<LongEvent>(new LongEventFactory(), ringBufferSize, factory, ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWithWorkerPool(new GroupConsumer("消费者1"), new GroupConsumer("消费者2"), new GroupConsumer("消费者3"));
        disruptor.start();
        ringBuffer = disruptor.getRingBuffer();
    }

    /**
     * 测试场景:从1开始,循环产生增量数字,放入RingBuffer环形缓冲区,等待disruptor处理
     *
     * @throws InterruptedException
     */
    @Test
    public void createEvent() throws InterruptedException {
        /*************广播类型消费者,单生产者,单消费者**************/
        //init();
        /*************广播类型消费者,多生产者,消费者之间无关系**************/
        //init2();
        /*************广播类型消费者,多消费者,消费者之间有关系,1和2消费完毕,3才能消费**************/
        //init3();
        /*************分组类型消费者,组内只能有一个消费者消费消息**************/
        init4();
        int i = 1;
        for (;;) {
            ringBuffer.publishEvent(new Publish(), String.valueOf(i++));
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
