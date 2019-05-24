/**
 * 双重检查式单例方法
 * 线程同步、效率高
 */
public class SingletonDCL {

    private static volatile SingletonDCL singleton;

    private SingletonDCL(){}

    public static SingletonDCL getInstance(){
        if (null == singleton){
            synchronized (SingletonDCL.class){
                if (null == singleton){
                    singleton = new SingletonDCL();
                }
            }
        }

        return singleton;
    }
}
