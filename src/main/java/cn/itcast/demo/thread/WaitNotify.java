package cn.itcast.demo.thread;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 线程间的通信:等待/通知的经典范式
 * Created by fudingcheng on 2018-11-30.
 */
public class WaitNotify {

    static boolean flag = true;

    static Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread waitThread = new Thread(new Wait(),"waitThread");
        Thread notifyThread = new Thread(new Notify(),"notifyThread");

        waitThread.start();
        TimeUnit.SECONDS.sleep(1);
        notifyThread.start();
    }


    static class Wait implements Runnable{

        @Override
        public void run() {
            synchronized (lock){
                while(flag){
                    try {
                        System.out.println(Thread.currentThread()+": flag is true. wait@"+new SimpleDateFormat("HH:mm:ss").format(new Date()));
                        lock.wait();        //wait线程等待,释放锁
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //线程被唤醒,继续执行
                System.out.println(Thread.currentThread()+": flag is false. running@"+new SimpleDateFormat("HH:mm:ss").format(new Date()));
            }
        }
    }

    static class Notify implements Runnable{

        @Override
        public void run() {
            synchronized (lock){
                System.out.println(Thread.currentThread()+": hold lock. notify@"+new SimpleDateFormat("HH:mm:ss").format(new Date()));
                lock.notifyAll();       //唤醒锁上等待的线程,wait线程从等待队列移除并加入同步队列,wait线程阻塞
                flag=false;
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }       //释放锁,wait线程开始竞争锁

            synchronized (lock){        //notify线程再次竞争锁
                System.out.println(Thread.currentThread()+": hold lock again. sleep@"+new SimpleDateFormat("HH:mm:ss").format(new Date()));
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
