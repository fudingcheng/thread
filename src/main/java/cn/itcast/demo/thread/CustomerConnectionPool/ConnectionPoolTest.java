package cn.itcast.demo.thread.CustomerConnectionPool;

import java.sql.Connection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 使用线程间的通信模拟数据库连接池
 * Created by fudingcheng on 2018-11-26.
 */
public class ConnectionPoolTest {

    //创建线程池对象
    static ConnectionPool pool = new ConnectionPool(10);

    //保证所有的ConnectionRunner同时开始
    static CountDownLatch start = new CountDownLatch(1);

    //main线程等待所有的ConnectionRunner结束后才能继续执行
    static CountDownLatch end;

    public static void main(String[] args) throws InterruptedException {
        int threadCount = 20;                   //线程总数量
        end = new CountDownLatch(threadCount);
        int count=20;
        AtomicInteger got = new AtomicInteger();
        AtomicInteger notGot = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(new ConnectionRunner(count,got,notGot),"ConnectionRunnerThread");
            thread.start();
        }

        start.countDown();         //所有的ConnectionRunner线程创建完毕,所有ConnectionRunner线程同时运行
        end.await();               //等待end为0后,主线程开始往下执行.

        System.out.println("total invoke:"+(threadCount*count));
        System.out.println("got connection:"+got);
        System.out.println("not got connection:"+notGot);

    }

    static class ConnectionRunner implements  Runnable{
        int count;          //每个线程获得Connection的次数
        AtomicInteger got;  //获得Connection的线程数量
        AtomicInteger notGot;   //没有获得connection的数量

        public ConnectionRunner(int count, AtomicInteger got, AtomicInteger notGot) {
            this.count = count;
            this.got = got;
            this.notGot = notGot;
        }

        @Override
        public void run() {
            try {
                start.await();      //   ConnectionRunner持有begin变量,等到所有的ConnectionRunner创建完毕才开始执行
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (count>0){    //每个线程循环执行count次
                try {
                    //获得连接
                    Connection connection = pool.fetchConnection(1000);
                    //连接不为空,执行操作
                    if(connection!=null){
                        try{
                            connection.createStatement();
                            connection.commit();
                        }finally {
                            //释放连接
                            pool.releaseConnection(connection);
                            //got++
                            got.incrementAndGet();
                        }
                    }else{
                        //没有获得连接,notGot++
                        notGot.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    count--;
                }
            }
            end.countDown();          //当前ConnectionRunner执行完毕后,将主线程拥有的count-1
        }
    }
}
