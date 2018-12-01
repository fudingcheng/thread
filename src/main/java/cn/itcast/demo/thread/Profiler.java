package cn.itcast.demo.thread;

import java.util.concurrent.TimeUnit;

/**
 * 使用ThreadLocal记录方法执行的时间
 * Created by fudingcheng on 2018-12-02.
 */
public class Profiler {
    private static ThreadLocal<Long> TIME_THREADLOCAL = new ThreadLocal<Long>();

    protected Long initialValue(){
        return System.currentTimeMillis();
    }

    public static final void  begin(){
        TIME_THREADLOCAL.set(System.currentTimeMillis());
    }

    public static final long end(){
        return System.currentTimeMillis()-TIME_THREADLOCAL.get();
    }

    public static void main(String[] args) throws InterruptedException {
        Profiler.begin();
        TimeUnit.SECONDS.sleep(1);
        System.out.println(Profiler.end());;
    }
}
