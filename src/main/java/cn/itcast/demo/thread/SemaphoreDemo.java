package cn.itcast.demo.thread;


import java.util.concurrent.*;

/**
 * 描述：适用于管理信号量，构造参数传递的是可供管理的信号量的数值。
 * 作用：控制并发的代码，执行前先获得信号（acquire方法），执行后归还信号（release方法），每次获得信号后，总信号量就会-1，如果没有可用的信号，acquire就会阻塞，知道有线程执行release方法释放信号量。
 */
public class SemaphoreDemo {

    public static void main(String[] args) {
        int count = 3;
        Semaphore semaphore = new Semaphore(count);
        ExecutorService threadPool = Executors.newFixedThreadPool(count);
        for (int i = 0; i < 10; i++) {
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        semaphore.acquire();
                        System.out.println(Thread.currentThread().getName()+"   say Hello...");
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    semaphore.release();
                }
            });
        }

        threadPool.shutdown();



    }
}
