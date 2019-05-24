/**
 * 使用Thread.local()，获取方法的执行时间
 */
public class ThreadLocalTest {

    private static final ThreadLocal<Long> TIME_THREADLOCAL = new ThreadLocal<Long>(){
        protected Long initValue(){
            return System.currentTimeMillis();
        }
    };

    public static final void begin(){
        TIME_THREADLOCAL.set(System.currentTimeMillis());
    }

    public static final long end(){
        return System.currentTimeMillis() - TIME_THREADLOCAL.get();
    }

    public static void main(String[] args) {
        ThreadLocalTest.begin();
        try {
            Thread.sleep(1l);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        System.out.println("Cost: " + ThreadLocalTest.end() + " millis");
    }
}
