package cn.itcast.demo.thread;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 描述：循环屏障
 * 作用：可以协作多个线程，让多个线程在这个屏障前等待，知道所有线程都到达这个屏障时，才一起继续执行后续的代码
 * 结果：
 *      pool-1-thread-1到达...
 *      pool-1-thread-2到达...
 *      pool-1-thread-3到达...
 *      所有线程均已到达，gogogo...
 */
public class CyclicBarrierDemo {

    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        int count = 3;  //等待线程的数量
        ExecutorService threadPool = Executors.newFixedThreadPool(count);
        CyclicBarrier barrier = new CyclicBarrier(count+1);
        for (int i = 0; i < count; i++) {
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName()+"到达...");
                    try {
                        barrier.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        barrier.await();
        System.out.println("所有线程均已到达，gogogo...");
        threadPool.shutdown();
    }

}
