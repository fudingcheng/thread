package cn.itcast.demo.thread.CustomerConnectionPool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.concurrent.TimeUnit;

/**
 * 通过JDK的动态代理创建Connection的代理对象
 * Created by fudingcheng on 2018-11-26.
 */
public class ConnectionDriver {
    public static Connection createConnection(){

        return (Connection) Proxy.newProxyInstance(ConnectionDriver.class.getClassLoader(),new Class[]{Connection.class},new ConnectionHandler());
    }

    static class ConnectionHandler implements InvocationHandler{

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if("commit".equals(method.getName())){
                TimeUnit.MILLISECONDS.sleep(100);
            }
            return null;
        }
    }
}
