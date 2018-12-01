package cn.itcast.demo.thread;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

/**
 * 中断线程正在运行的线程和休眠的线程
 * Created by fudingcheng on 2018-11-29.
 */
public class InterruptedDemo {
    public static void main(String[] args) throws Exception {
        //睡眠线程
        Thread sleepThread = new Thread(new SleepRunner(),"sleepThread");
        sleepThread.setDaemon(true);

        //工作线程
        Thread busyThread = new Thread(new BusyRunner(),"busyThread");
        busyThread.setDaemon(true);

        //线程启动
        sleepThread.start();
        busyThread.start();

        //主线程休眠5秒,让两个线程充分运行
        TimeUnit.SECONDS.sleep(5);

        //终止线程
        sleepThread.interrupt();
        busyThread.interrupt();

        //打印结果
        System.out.println("sleepThread:"+sleepThread.isInterrupted());     //false
        System.out.println("busyThread:"+busyThread.isInterrupted());       //true

    }

    static class SleepRunner implements Runnable{

        @Override
        public void run() {
            while (true){
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    static class BusyRunner implements Runnable{

        @Override
        public void run() {
            while (true){

            }
        }
    }

}
