package cn.itcast.demo.thread;

import java.util.concurrent.TimeUnit;

/**
 * 合理的中断线程
 * Created by fudingcheng on 2018-11-29.
 */
public class Shutdown {

    public static void main(String[] args) throws InterruptedException {
        Runner one = new Runner();
        Thread oneThread = new Thread(one,"oneThread");

        Runner two = new Runner();
        Thread twoThread = new Thread(two,"towThread");

        oneThread.start();
        twoThread.start();

        TimeUnit.SECONDS.sleep(3);

        oneThread.interrupt();
        two.cancel();

    }

    private static class Runner implements Runnable{
        //打印变量的值
        private long i;
        //控制线程是否执行的开关,volatile保证变量在多个线程间可见
        private volatile  boolean on = true;

        @Override
        public void run() {
            //如果on不为false并且当前线程没有中断,线程就一直执行
            while(on && !Thread.currentThread().isInterrupted()){
                i++;
            }

            System.out.println("i:"+i);
        }

        //取消线程
        public void cancel(){
            on=false;
        }
    }
}
