package cn.itcast.demo.thread.DisruptorQueue;

import com.lmax.disruptor.EventFactory;

/**
 * 事件工厂
 */
public class LongEventFactory implements EventFactory{
    @Override
    public LongEvent newInstance() {
        return new LongEvent();
    }
}
