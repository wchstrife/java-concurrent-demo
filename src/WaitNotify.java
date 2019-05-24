import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 线程通信：等待/通知机制
 * wait线程wait后释放锁，进入等待队列，等待被notice
 * notice获得lock后，notice wait线程，等执行完成之后，释放锁，wait线程进入同步队列
 * wait线程阻塞状态，获得lock重新变为running
 */
public class WaitNotify {
    static boolean flag = true;
    static Object lock = new Object();

    public static void main(String[] srgs) throws Exception{
        Thread waitThread = new Thread(new Wait(), "WaitThread");
        waitThread.start();
        try{
            Thread.sleep(5l);
        }catch (InterruptedException e){
        }
        Thread notifyThread = new Thread(new Notify(), "noticeThread");
        notifyThread.start();
    }

    static class Wait implements Runnable {
        @Override
        public void run() {
            // 加锁，获得lock的monitor
            synchronized (lock){
                // 条件不满足时，继续wait，同时释放lock的锁
                while (flag){
                    try{
                        System.out.println(Thread.currentThread() + "flag is true.Wait @ " + new SimpleDateFormat("HH::mm::ss" ).format(new Date()));
                        lock.wait();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                // 条件满足时，输出完成
                System.out.println(Thread.currentThread() + "flag is false.running @ " + new SimpleDateFormat("HH::mm::ss" ).format(new Date()));
            }
        }
    }


    static class Notify implements Runnable{
        @Override
        public void run() {
            // 加锁，获得lock的monitor
            synchronized (lock){
                // 获取lock的锁，然后进行通知，通知时不会释放lock的锁
                // 知道线程释放了lock后，WaitThread才会从wait方法返回
                System.out.println(Thread.currentThread() + "hold lock. notify @ " + new SimpleDateFormat("HH::mm::ss" ).format(new Date()));
                lock.notifyAll();
                flag = false;
                try{
                    Thread.sleep(5l);
                }catch (InterruptedException e){
                }

                // 再次加锁
                synchronized (lock){
                    System.out.println(Thread.currentThread() + "hold lock again. sleep @ " + new SimpleDateFormat("HH::mm::ss" ).format(new Date()));
                    try{
                        Thread.sleep(5l);
                    }catch (InterruptedException e){
                    }
                }
            }
        }
    }

}
