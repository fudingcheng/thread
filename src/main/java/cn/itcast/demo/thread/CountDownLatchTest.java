package cn.itcast.demo.thread;

import java.util.concurrent.*;

/**
 * 描述：CountDownLatch是java.util.concurrent并发包下的类
 * 作用：提供的机制是当多个线程到达预期状态或者完成预期工作时触发事件，其他线程等待这个事件来触发自己后续的工作。
 * 结果：
 *      pool-1-thread-1到达...
 *      pool-1-thread-2到达...
 *      pool-1-thread-3到达...
 *      所有线程均已到达，gogogo...
 */
public class CountDownLatchTest {

    public static void main(String[] args) {
        int count = 3; //触发事件的线程数量
        ExecutorService threadPool = Executors.newFixedThreadPool(count);
        CountDownLatch latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName()+"到达...");
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("所有线程均已到达，"+Thread.currentThread().getName()+"：gogogo...");

    }

}
