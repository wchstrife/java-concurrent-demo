/**
 * 懒汉式单例方法
 * 线程安全，同步方法，效率不高
 */
public class SingletonLazy {

    private static SingletonLazy singleton;

    private SingletonLazy(){}

    public static synchronized SingletonLazy getInstance(){
        if (null == singleton){
            singleton = new SingletonLazy();
        }

        return singleton;
    }
}
