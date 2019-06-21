package cn.itcast.demo.thread.DisruptorQueue;

import com.lmax.disruptor.EventHandler;

/**
 * 消费者逻辑:获取event进行处理
 * 广播类型消费者:每个消费者对象都能获得消息进行消费
 */
public class BroadcastConsumer implements EventHandler<LongEvent>{

    private String consumerName;


    public BroadcastConsumer(String consumerName) {
        this.consumerName = consumerName;
    }

    @Override
    public void onEvent(LongEvent event, long sequence, boolean endOfBatch) throws Exception {
        Long value = event.getValue();
        System.out.println(consumerName+"处理:"+value);
    }
}
