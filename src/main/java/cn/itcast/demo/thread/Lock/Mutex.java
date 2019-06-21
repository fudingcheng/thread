package cn.itcast.demo.thread.Lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedLongSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 自定义Lock
 * Created by fudingcheng on 2018-12-05.
 */
public class Mutex implements Lock{

    //自定义同步器
    private static class Sync extends AbstractQueuedLongSynchronizer{

        //独占式获取锁
        @Override
        protected boolean tryAcquire(long arg) {
            if(compareAndSetState(0,arg)){
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }


        //独占式释放锁
        @Override
        protected boolean tryRelease(long arg) {
            if(getState()==0){
                throw new IllegalMonitorStateException();
            }
            setExclusiveOwnerThread(null);

            return true;
        }

        //判断锁是否被当前线程独占
        @Override
        protected boolean isHeldExclusively() {
            return getState()==1;
        }

        //返回Condition,其中包含一个同步队列
        Condition newCondition(){
            return new ConditionObject();
        }
    }

    private final Sync sync = new Sync();


    @Override
    public void lock() {
        sync.acquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.tryAcquire(1);
    }

    @Override
    public boolean tryLock(){
        return sync.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(time,unit.toNanos(time));
    }

    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }



}
