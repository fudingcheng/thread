package cn.itcast.demo.thread;

import java.util.concurrent.TimeUnit;

/**
 * 守护线程:守护线程随着主线程的结束而结束,也不会执行finaly块中的代码
 * Created by fudingcheng on 2018-12-02.
 */
public class Daemon {

    public static void main(String[] args) throws InterruptedException {
        Thread daemonThread =new Thread(new DaemonRunner(),"daemonThread");
        daemonThread.setDaemon(true);
        daemonThread.start();

        TimeUnit.SECONDS.sleep(2);
    }

    static class DaemonRunner implements Runnable{

        @Override
        public void run() {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                System.out.println("DaemonThread finally run...");
            }
        }
    }
}
