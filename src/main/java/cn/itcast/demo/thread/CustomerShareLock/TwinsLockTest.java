package cn.itcast.demo.thread.CustomerShareLock;

import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * 共享锁:同时允许2个线程获得锁
 * 获得锁------->睡眠1秒-------->打印当前线程名称----->睡眠1秒----->释放锁
 *
 * 结果:
 *  Thread-1
 *  Thread-0
 *
 *  Thread-3
 *  Thread-1
 *
 *  Thread-5
 *  Thread-4
 *
 *  Thread-4
 *  Thread-5
 *
 *  Thread-4
 *  Thread-2

 */
public class TwinsLockTest {

    @Test
    public void test() throws InterruptedException {
        final Lock lock = new TwinsLock();

        class Worker extends Thread{
            @Override
            public void run() {
                while (true){
                    //获得锁
                    lock.lock();
                    try {
                        //睡眠1秒
                        TimeUnit.SECONDS.sleep(1);
                        //打印线程名称
                        System.out.println(Thread.currentThread().getName());
                        //睡眠1秒
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                        //释放锁
                        lock.unlock();
                    }


                }
            }
        }

        //启动10个线程
        for (int i = 0; i < 10; i++) {
            Worker worker = new Worker();
            worker.setDaemon(true);
            worker.start();
        }

        //每隔1秒换行
        for (int i = 0; i < 10; i++) {
            TimeUnit.SECONDS.sleep(1);
            System.out.println();
        }

    }
}
