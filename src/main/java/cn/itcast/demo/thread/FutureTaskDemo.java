package cn.itcast.demo.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * FutureTask可用于异步获取执行结果或取消执行任务的场景。通过传入Runnable或者Callable的任务给FutureTask，直接调用其run方法或者放入线程池执行，之后可以在外部通过FutureTask的get方法异步获取执行结果，因此，FutureTask非常适合用于耗时的计算，主线程可以在完成自己的任务后，再去获取结果。另外，FutureTask还可以确保即使调用了多次run方法，它都只会执行一次Runnable或者Callable任务，或者通过cancel取消FutureTask的执行等。
 * FutureTask是Future的实现类，帮助实现了具体的任务以及与Future接口的get等方法的关联。
 * get方法：异步获得线程返回的结果
 */
public class FutureTaskDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        //任务集合
        List<FutureTask<String>> taskList = new ArrayList<FutureTask<String>>();
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        System.out.println(Thread.currentThread().getName()+": start task...");
        for (int i = 0; i < 10; i++) {
            //创建任务
            FutureTask futureTask = new FutureTask(new MyCallable());
            taskList.add(futureTask);
            threadPool.submit(futureTask);
        }

        System.out.println(Thread.currentThread().getName()+": gogogo...");

        for (int i = 0; i <taskList.size() ; i++) {
            System.out.println(taskList.get(i).get());
        }

        threadPool.shutdown();
    }

    private static class MyCallable implements java.util.concurrent.Callable {
        @Override
        public String call() throws Exception {
            String name = Thread.currentThread().getName()+":  "+"执行...";
            //线程阻塞5秒钟
            TimeUnit.SECONDS.sleep(5);
            return name;
        }
    }
}
