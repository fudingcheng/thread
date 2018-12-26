package cn.itcast.demo.thread.DisruptorQueue;

import com.lmax.disruptor.WorkHandler;

/**
 * 消费者逻辑:获取event进行处理
 * 分组类型消费者:同一个组只能有一个消费者获得消息进行消费
 */
public class GroupConsumer implements WorkHandler<LongEvent>{

    private String customerName;

    public GroupConsumer(String customerName) {
        this.customerName = customerName;
    }

    @Override
    public void onEvent(LongEvent event) throws Exception {
        System.out.println(customerName+"处理:"+event.getValue());
    }
}
