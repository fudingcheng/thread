package cn.itcast.demo.thread;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

/**
 * 线程join()方法的的使用
 * Created by fudingcheng on 2018-12-01.
 * 结果：
 *   main终止...
 *   0终止...
 *   1终止...
 *   2终止...
 *   3终止...
 *   4终止...
 *   5终止...
 *   6终止...
 *   7终止...
 *   8终止...
 *   9终止...
 */
public class Join {


    public static void main(String[] args) throws InterruptedException {
        Thread previous = Thread.currentThread();

        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(new Domino(previous),String.valueOf(i));
            thread.start();
            previous = thread;
        }

        TimeUnit.SECONDS.sleep(2);
        System.err.println(Thread.currentThread().getName()+"终止...");
    }

    static class Domino implements Runnable{

        private Thread thread;

        public Domino(Thread thread) {
            this.thread = thread;
        }

        @Override
        public void run() {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName()+"终止...");
        }
    }
}
