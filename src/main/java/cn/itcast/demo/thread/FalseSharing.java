package cn.itcast.demo.thread;

import sun.misc.Contended;

/**
 * 避免伪共享
 *
 * 空间换时间,增大变量所在类的空间,避免伪共享;
 *
 * ValuePadding结果:
 *   Thread num 1 duration = 375
 *   Thread num 2 duration = 702
 *   Thread num 3 duration = 795
 *   Thread num 4 duration = 734
 *   Thread num 5 duration = 1123
 *   Thread num 6 duration = 1170
 *   Thread num 7 duration = 1341
 *   Thread num 8 duration = 1451
 *   Thread num 9 duration = 1653
 *
 *
 * ValueNoPadding打印结果
 *   Thread num 1 duration = 359
 *   Thread num 2 duration = 1092
 *   Thread num 3 duration = 1311
 *   Thread num 4 duration = 2480
 *   Thread num 5 duration = 3370
 *   Thread num 6 duration = 3697
 *   Thread num 7 duration = 3572
 *   Thread num 8 duration = 3666
 *   Thread num 9 duration = 2262
 *
 *
 * 加入@Contended注解打印结果
 *   Thread num 1 duration = 405
 *   Thread num 2 duration = 1170
 *   Thread num 3 duration = 1560
 *   Thread num 4 duration = 842
 *   Thread num 5 duration = 3261
 *   Thread num 6 duration = 3853
 *   Thread num 7 duration = 3837
 *   Thread num 8 duration = 3245
 *   Thread num 9 duration = 2371
 *
 */
public class FalseSharing implements Runnable {
    public final static long ITERATIONS = 500L * 1000L * 100L;
    private int arrayIndex = 0;

    private static ValueNoPadding[] longs;
    public FalseSharing(final int arrayIndex) {
        this.arrayIndex = arrayIndex;
    }

    public static void main(final String[] args) throws Exception {
        for(int i=1;i<10;i++){
            System.gc();
            final long start = System.currentTimeMillis();
            runTest(i);
            System.out.println("Thread num "+i+" duration = " + (System.currentTimeMillis() - start));
        }

    }

    private static void runTest(int NUM_THREADS) throws InterruptedException {
        //创建NUM_THREADS数量的线程数组threads
        Thread[] threads = new Thread[NUM_THREADS];
        //创建NUM_THREADS长度的ValuePadding|ValueNoPadding数组longs
        longs = new ValueNoPadding[NUM_THREADS];
        //初始化longs数组
        for (int i = 0; i < longs.length; i++) {
            longs[i] = new ValueNoPadding();
        }
        //初始化线程数组
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new FalseSharing(i));
        }

        //启动数组中每个线程
        for (Thread t : threads) {
            t.start();
        }

        //等待threads数组中每个线程执行完毕,才返回
        for (Thread t : threads) {
            t.join();
        }
    }

    //线程任务
    public void run() {
        long i = ITERATIONS + 1;
        while (0 != --i) {
            longs[arrayIndex].value = 0L;
        }
    }

    public final static class ValuePadding {
        protected long p1, p2, p3, p4, p5, p6, p7;
        protected volatile long value = 0L;
        protected long p9, p10, p11, p12, p13, p14;
        protected long p15;
    }

    @Contended
    public final static class ValueNoPadding {
        // protected long p1, p2, p3, p4, p5, p6, p7;
        protected volatile long value = 0L;
        // protected long p9, p10, p11, p12, p13, p14, p15;
    }
}
