package connection;

import java.sql.Connection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionPoolTest {
    static ConnectionPool pool = new ConnectionPool(10);

    // 保证所有ConnectionRunner能同时开始
    static CountDownLatch start = new CountDownLatch(1);
    // main线程会等所有的ConnectionRunner结束后才继续执行
    static CountDownLatch end;

    public static void main(String[] args) throws  Exception{
        // 线程数量
        int threadCount = 10;
        end = new CountDownLatch(threadCount);
        int count = 20;
        AtomicInteger got = new AtomicInteger();
        AtomicInteger notGot = new AtomicInteger();

        for (int i = 0; i < threadCount; i++){
            Thread thread = new Thread(new ConnectionRunner(count, got, notGot), "ConnectionRunnerThread");
            thread.start();
        }

        start.countDown();
        end.await();
        System.out.println("total invoke: " + (threadCount * count));
        System.out.println("got connection: " + got);
        System.out.println("not got connection " + notGot);
    }


    static class ConnectionRunner implements Runnable{
        int count;
        AtomicInteger got;
        AtomicInteger notGot;

        public ConnectionRunner(int count, AtomicInteger got, AtomicInteger notGot) {
            this.count = count;
            this.got = got;
            this.notGot = notGot;
        }

        @Override
        public void run() {
            try {
                start.await();
            }catch (Exception e){
            }

            while (count > 0){
                try {
                    // 从线程池中获取连接，超时未获取到返回null
                    // 用got notGot记录获取的数量和未获取的数量
                    Connection connection = pool.fetchConnection(1000);
                    if (null != connection){
                        try{
                            connection.createStatement();
                            connection.commit();
                        }finally {
                            pool.realseConnection(connection);
                            got.incrementAndGet();
                        }
                    }else {
                        notGot.incrementAndGet();
                    }
                }catch (Exception e){
                }finally {
                    count--;
                }
            }
            end.countDown();
        }
    }
}
