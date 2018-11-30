package cn.itcast.demo.thread;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * main()方法运行的时候会同时启动多个线程
 * Created by fudingcheng on 2018-12-01.
 */
public class MutiThread {
    public static void main(String[] args) {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(false, false);
        for (ThreadInfo t:threadInfos) {
            System.out.println(t.getThreadId()+":"+t.getThreadName());
        }
    }
}
