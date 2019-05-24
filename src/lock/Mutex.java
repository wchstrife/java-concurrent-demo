package lock;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

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

    // 将Lock接口中的方法实现，代理到内部聚合的同步器Sync的方法上
    //TODO
}
