package cn.itcast.demo.thread.DisruptorQueue;

/**
 * 事件:封装原始数据
 */
public class LongEvent {
    private Long value;

    public void setValue(Long value) {
        this.value = value;
    }

    public Long getValue() {
        return value;
    }
}
