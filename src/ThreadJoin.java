/**
 * 使用Thread.join()多个线程按顺序输出0-9的值
 */
public class ThreadJoin {

    public static void main(String[] args) {
        Thread previous = Thread.currentThread();
        for (int i = 0; i < 10; i++){
            // 使用join,每一个线程拥有前一个线程的引用，需要等待前一个线程终止，才能从等待中返回。
            Thread thread = new Thread(new Domino(previous), String.valueOf(i));
            thread.start();
            previous = thread;
            try{
                Thread.sleep(5l);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " terminate");
        }
    }

    static class Domino implements Runnable{
        private Thread thread;

        public Domino(Thread thread){
            this.thread = thread;
        }

        @Override
        public void run() {
            try{
                thread.join();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " terminate.");
        }
    }
}
