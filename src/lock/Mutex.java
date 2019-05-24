package lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 聚合同步器实现自定义互斥锁
 */
public class Mutex implements Lock {

    // 静态内部类，自定义同步器
    private static class Sync extends AbstractQueuedSynchronizer{

        // 是否处于独占状态
        @Override
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        // 状态为 0 的时候可以获取锁
        @Override
        protected boolean tryAcquire(int arg) {
            if (compareAndSetState(0, 1)){
                setExclusiveOwnerThread(Thread.currentThread());
                return  true;
            }
            return false;
        }

        // 释放锁，将锁状态设置为0
        @Override
        protected boolean tryRelease(int arg) {
            if (getState() == 0){
                throw new IllegalMonitorStateException();
            }

            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        // 非重写AQS的方法
        // 返回一个condition，每个condition都包含一个condition队列
        Condition newCondition(){
            return new ConditionObject();
        }
    }

    // 实现Lock接口中的实现时，需要代理到内部聚合的同步器Sync的方法上，或者间接的模板方法调用重写的方法
    private final Sync sync = new Sync();

    @Override
    public void lock() {
        sync.acquire(1);    // AQS模板方法，底层调用重写的tryAcquire(int arg)
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {   // AQS模板方法，底层在lock的基础上会响应中断
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);  // 直接调用重写的tryAcquire(int arg)
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));     // AQS模板方法
    }

    @Override
    public void unlock() {
        sync.release(1); // AQS模板类，底层调用tryRelease
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }
}
