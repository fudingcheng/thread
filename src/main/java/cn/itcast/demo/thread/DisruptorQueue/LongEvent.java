package cn.itcast.demo.thread.DisruptorQueue;

//事件
public class LongEvent {
    private Long value;

    public void setValue(Long value) {
        this.value = value;
    }

    public Long getValue() {
        return value;
    }
}
