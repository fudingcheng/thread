package cn.itcast.demo.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;

/**
 * 描述：交换。
 * 作用：用于在两个线程之间进行数据的交换。线程会阻塞在exchange方法上，知道另外一个线程也达到exchange方法时，两者交换数据，然后各自线程再继续执行自身的代码。
 * 结果：
 *      thread1:[3, 4]
 *      thread2:[1, 2]
 */
public class ExchangeTest {


    public static void main(String[] args) {

        Exchanger<List<Integer>> exchanger =new Exchanger<List<Integer>>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Integer> l = new ArrayList<Integer>();
                l.add(1);
                l.add(2);
                try {
                    l = exchanger.exchange(l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName()+":"+l);
            }
        },"thread1").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Integer> l = new ArrayList<Integer>();
                l.add(3);
                l.add(4);
                try {
                    l = exchanger.exchange(l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName()+":"+l);
            }
        },"thread2").start();
    }

}
