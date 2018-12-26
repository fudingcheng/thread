package cn.itcast.demo.thread.CustomerShareLock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class TwinsLock implements Lock {

    private final Sync sync = new Sync(2);

    //自定义同步器
    private static final class Sync extends AbstractQueuedSynchronizer{

        //构造器
        Sync(int count){
            //count必须大于0
            if(count<=0){
                throw new IllegalArgumentException("count must large than zero");
            }
            //设置同步状态
            setState(count);
        }

        //共享式获得锁
        @Override
        protected int tryAcquireShared(int reduceCount) {
            for(;;){
                int current = getState();
                int newCount = current-reduceCount;

                if(newCount<0 || compareAndSetState(current,newCount)){
                    return newCount;
                }
            }
        }

        //共享式释放锁
        @Override
        protected boolean tryReleaseShared(int returnCount) {
            for(;;){
                int current = getState();
                int newCount = current+returnCount;
                if(compareAndSetState(current,newCount)){
                    return true;
                }
            }
        }

        //返回同步队列
        protected Condition newCondition(){
            return new ConditionObject();
        }
    }

    //获得锁
    @Override
    public void lock() {
        sync.acquireShared(1);
    }

    //释放锁
    @Override
    public void unlock() {
        sync.releaseShared(1);
    }


    //可中断获得锁
    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    //非阻塞获得锁
    @Override
    public boolean tryLock() {
        return sync.tryAcquireShared(1)>=0;
    }

    //超时获得锁
    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireSharedNanos(1,unit.toNanos(time));
    }

    //同步队列
    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }
}
