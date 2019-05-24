package thread;



import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultThreadPool<Job extends Runnable> implements ThreadPool<Job>{
    // 线程池最大限制数
    private static final int MAX_WORKER_NUMBERS = 10;
    // 线程池默认的数量
    private static final int DEFAULT_WORKDER_NUMBERS = 5;
    // 线程池最小的数量
    private static final int MIN_WORKED_NUMBERS = 1;
    // 工作列表
    private final LinkedList<Job> jobs = new LinkedList<Job>();
    // 工作者列表
    private final List<Worker> workers = Collections.synchronizedList(new ArrayList<Worker>());
    // 工作者线程的数量
    private int workerNum = DEFAULT_WORKDER_NUMBERS;
    // 线程编号生成
    private AtomicLong threadNum = new AtomicLong();

    public DefaultThreadPool(){
        initializeWorkers(DEFAULT_WORKDER_NUMBERS);
    }

    public DefaultThreadPool(int num){
        workerNum = num > MAX_WORKER_NUMBERS ? MAX_WORKER_NUMBERS : num < MIN_WORKED_NUMBERS ? MIN_WORKED_NUMBERS : num;
        initializeWorkers(workerNum);
    }

    public void execute(Job job){
        if (null != job){
            synchronized (jobs){
                jobs.addLast(job);
                jobs.notify();
            }
        }
    }

    public void shutdown(){
        for (Worker worker : workers){
            worker.shutdown();
        }
    }

    public void addWorkers(int num){
        synchronized (jobs){
            // 限制新增的Worker数量不能超过最大值
            if (num + this.workerNum > MAX_WORKER_NUMBERS){
                num = MAX_WORKER_NUMBERS - this.workerNum;
            }
            initializeWorkers(num);
            this.workerNum += num;
        }
    }

    // 移除工作者
    public void removeWorkers(int num){
        synchronized (jobs){
            if (num > this.workerNum){
                throw new IllegalArgumentException("beyond workNum");
            }
            // 按照给定数量停止Worker
            int count = 0;
            while (count < num){
                Worker worker = workers.get(count);
                if (workers.remove(worker)){
                    worker.shutdown();
                    count++;
                }
            }
            this.workerNum -= count;
        }
    }

    // 获得工作的数量
    public int getJobsize(){

        return jobs.size();
    }

    // 初始化线程工作者
    private void initializeWorkers(int num){
        for (int i = 0; i < num; i++){
            Worker worker = new Worker();
            workers.add(worker);
            Thread thread = new Thread(worker, "ThreadPool-Worker-" + threadNum.incrementAndGet());
            thread.start();
        }
    }


    /**
     * 消费者进程
     * 从Jobs的list中取出一个job进行run()
     */
    class Worker implements Runnable{
        // 是否工作
        private volatile boolean running = true;

        @Override
        public void run() {
            while (running){
                Job job = null;
                synchronized (jobs){
                    // 如果工作者列表是空的，就wait
                    while (jobs.isEmpty()){
                        try {
                            jobs.wait();
                        }catch (InterruptedException e){
                            // 感受到外部中断WorkedThread的中断操作，返回
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    // 取出一个Job
                    job = jobs.removeFirst();
                }
                if (job != null){
                    try {
                        job.run();
                    }catch (Exception ex){

                    }
                }
            }
        }

        public void shutdown(){
            running = false;
        }
    }
}
