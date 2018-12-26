package cn.itcast.demo.thread.DisruptorQueue;

import com.lmax.disruptor.EventTranslatorOneArg;

/**
 * 生产者:将原始数据转换为event,放入ringBuffer中
 */
public class Publish implements EventTranslatorOneArg<LongEvent,String>{
    @Override
    public void translateTo(LongEvent event, long sequence, String value) {
        event.setValue(Long.parseLong(value));
    }
}
