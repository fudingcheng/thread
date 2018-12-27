[TOC]

# 1. Disruptor背景

Disruptor是英国外汇交易公司LMAX开发的一个高性能队列，研发的初衷是解决内存队列的延迟问题。与Kafka、RabbitMQ等消息队列框架用于**服务间**的消息队列不同，disruptor一般用于**线程间**消息的传递。2011年它还获得了Oracle官方的Duke大奖。

# 2. 性能

Disruptor的作用与ArrayBlockingQueue有相似之处，都可以实现线程间的消息传递，但是disruptor从功能、性能都远好于ArrayBlockingQueue，当多个线程之间传递大量数据或对性能要求较高时，可以考虑使用disruptor作为ArrayBlockingQueue的替代者。

吞吐量测试（单位：秒）P:代表生产者  C:代表消费者

![Unicast: 1P – 1C](.\01.png)

![](.\02.png)

![](.\03.png)

![](.\04.png)

![](.\05.png)

|                    | Array Blocking Queue | Disruptor  |
| ------------------ | -------------------- | ---------- |
| Unicast: 1P – 1C   | 5,339,256            | 25,998,336 |
| Pipeline: 1P – 3C  | 2,128,918            | 16,806,157 |
| Sequencer: 3P – 1C | 5,539,531            | 13,403,268 |
| Multicast: 1P – 3C | 1,077,384            | 9,377,871  |
| Diamond: 1P – 3C   | 2,113,941            | 16,143,613 |

# 3. 如何使用

## 3.1 准备工作

### 3.1.1 导入依赖

```xml
<dependency>
    <groupId>com.lmax</groupId>
    <artifactId>disruptor</artifactId>
    <version>3.3.4</version>
</dependency>
```

### 3.1.2 事件类

事件类的作用是封装待处理的原始数据

```java
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
```

### 3.1.3 事件工厂

用于产生事件对象

```java
public class LongEventFactory implements EventFactory{
    @Override
    public LongEvent newInstance() {
        return new LongEvent();
    }
}
```

### 3.1.4 生产者类

生产者,顾名思义就是生产数据,该数据会被封装到Event中

```java
import com.lmax.disruptor.EventTranslatorOneArg;

/**
 * 生产者:将原始数据封装为Event,以便放入RingBuffer缓冲区中
 */
public class Publish implements EventTranslatorOneArg<LongEvent,String>{
    @Override
    public void translateTo(LongEvent event, long sequence, String value) {
        event.setValue(Long.parseLong(value));
    }
}
```

### 3.1.5 消费者类

消费者,用于处理生产者产生的数据

* 广播类型消费者:每个消费对象都可以获得消息进行消费

```java
import com.lmax.disruptor.EventHandler;

public class BroadcastConsumer implements EventHandler<LongEvent>{
	
    //消费者名称
    private String consumerName;

    public BroadcastConsumer(String consumerName) {
        this.consumerName = consumerName;
    }
	
    /**
     * 当有事件产生时触发
     */
    @Override
    public void onEvent(LongEvent event, long sequence, boolean endOfBatch) throws Exception {
        Long value = event.getValue();
        System.out.println(consumerName+"处理:"+value);
    }
}
```

* 分组类型消费者:同一个组只能有一个消费者获得消息进行消费

```java
import com.lmax.disruptor.WorkHandler;

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
```

## 3.2 案例

### 3.2.1 初始化对象

```java
//环形缓冲区大小,大小必须为2的倍数
private int ringBufferSize=1024;
//环形缓冲区
private RingBuffer ringBuffer;
//线程工厂
private ThreadFactory factory = Executors.defaultThreadFactory();
//disruptor队列对象
private Disruptor<LongEvent> disruptor;
```

### 3.2.2 测试场景

```java
/**
 * 测试场景:从1开始,循环产生增量数字,放入RingBuffer环形缓冲区,等待disruptor处理
 * @throws InterruptedException
 */
@Test
public void createEvent() throws InterruptedException {
 /*************广播类型消费者,单生产者,单消费者****************************/
 //init();
 /*************广播类型消费者,多生产者,消费者之间无关系**********************/
 //init2();
 /*************广播类型消费者,多消费者,消费者之间有关系,1和2消费完毕,3才能消费***/
 //init3();
 /*************分组类型消费者,组内只能有一个消费者消费消息********************/
 //init4();
    int i = 1;
    for (;;) {
        ringBuffer.publishEvent(new Publish(), String.valueOf(i++));
        TimeUnit.SECONDS.sleep(1);
    }
}
```

#### 3.2.2.1 单生产者，单消费者(广播)

* 代码

```java
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
```

* 结果

```tex
消费者1处理:1
消费者1处理:2
消费者1处理:3
消费者1处理:4
消费者1处理:5
消费者1处理:6
消费者1处理:7
消费者1处理:8
消费者1处理:9
...
```

#### 3.2.2.2  多生产者,单消费者(广播)

* 代码

```java
public void init2() {
    //创建disrupt队列对象 
    disruptor = new Disruptor<LongEvent>(
        new LongEventFactory(), 	//参数1:事件工厂;
        ringBufferSize, 			//参数2:缓冲区大小;
        factory, 					//参数3:线程工厂
        ProducerType.MULTI, 		//参数4:生产者类型(多生产者)
        new BlockingWaitStrategy()	//参数5:线程阻塞策略
    );
    //队列对象绑定消费者
    disruptor.handleEventsWith(new BroadcastConsumer("消费者1"));
    //队列启动
    disruptor.start();
    //初始化缓冲区
    ringBuffer = disruptor.getRingBuffer();
}
```

* 结果

```tex
消费者1处理:1
消费者1处理:2
消费者1处理:3
消费者1处理:4
消费者1处理:5
消费者1处理:6
消费者1处理:7
消费者1处理:8
消费者1处理:9
...
```

#### 3.2.2.3 多生产者,多消费者(广播)

* 代码

```java
public void init3() {
    //创建disrupt队列对象 
    disruptor = new Disruptor<LongEvent>(
        new LongEventFactory(), 	//参数1:事件工厂;
        ringBufferSize, 			//参数2:缓冲区大小;
        factory, 					//参数3:线程工厂
        ProducerType.MULTI, 		//参数4:生产者类型(多生产者)
        new BlockingWaitStrategy()	//参数5:线程阻塞策略
    );
    //队列对象绑定消费者,消费者1和2处理完毕后,消费者3才能消费
    disruptor.handleEventsWith(
        new BroadcastConsumer("消费者1"), 
        new BroadcastConsumer("消费者2")
    ).then(new BroadcastConsumer("消费者3"));
    //队列启动
    disruptor.start();
    //初始化缓冲区
    ringBuffer = disruptor.getRingBuffer();
}
```

* 结果

```tex
消费者1处理:1
消费者2处理:1

消费者3处理:1

消费者1处理:2
消费者2处理:2

消费者3处理:2

消费者1处理:3
消费者2处理:3

消费者3处理:3
...
```

#### 3.2.2.4 多生产者,多消费者(组内)

* 代码

```java
public void init4() {
    //创建disrupt队列对象 
    disruptor = new Disruptor<LongEvent>(
        new LongEventFactory(), 	//参数1:事件工厂;
        ringBufferSize, 			//参数2:缓冲区大小;
        factory, 					//参数3:线程工厂
        ProducerType.MULTI, 		//参数4:生产者类型(多生产者)
        new BlockingWaitStrategy()	//参数5:线程阻塞策略
    );
   //队列对象绑定消费者,消费者1和2处理完毕后,消费者3才能消费
   disruptor.handleEventsWithWorkerPool(
       new GroupConsumer("消费者1"), 
       new GroupConsumer("消费者2"), 
       new GroupConsumer("消费者3")
   );
   //队列启动
   disruptor.start();
   //初始化缓冲区
   ringBuffer = disruptor.getRingBuffer();
}
```

* 结果

```tex
消费者2处理:1
消费者2处理:2
消费者1处理:3
消费者3处理:4
消费者2处理:5
消费者1处理:6
消费者3处理:7
消费者2处理:8
...
```

