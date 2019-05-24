package thread;

/**
 * 线程池接口定义
 */
public interface ThreadPool <Job extends Runnable>{
    // 执行一个Job，需要实现runnable接口
    void execute(Job job);
    // 关闭线程池
    void shutdown();
    // 增加工作线程
    void addWorkers(int num);
    // 减少工作线程
    void removeWorkers(int num);
    // 得到正在等待执行的任务数量
    int getJobsize();
}
