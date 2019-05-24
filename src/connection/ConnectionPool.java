package connection;

import java.sql.Connection;
import java.util.LinkedList;

public class ConnectionPool {
    private LinkedList<Connection> pool = new LinkedList<Connection>();

    public ConnectionPool(int initialSize){
        if (initialSize > 0){
            for (int i = 0; i < initialSize; i++){
                pool.addLast(ConnectionDriver.createConnection());
            }
        }
    }

    public void realseConnection(Connection connection){
        if (null != connection){
            synchronized (pool){
                // 连接释放后要进行通知，让其他消费者能感受到了连接池中释放了一个连接
                pool.addLast(connection);
                pool.notifyAll();
            }
        }
    }

    /**
     * 超时未获取到连接，则返回NULL
     * @param mills
     * @return
     * @throws InterruptedException
     */
    public Connection fetchConnection(long mills) throws InterruptedException{
        synchronized (pool){
            // 完全超时
            if (mills < 0){
                while (pool.isEmpty()){
                    pool.wait();
                }

                return pool.removeFirst();
            }else {
                long future = System.currentTimeMillis() + mills;
                long remaining = mills;
                while (pool.isEmpty() && remaining > 0){
                    pool.wait(remaining);
                    remaining = future - System.currentTimeMillis();
                }
                Connection result = null;
                if (!pool.isEmpty()){
                    result = pool.removeFirst();
                }

                return result;
            }
        }
    }
}
