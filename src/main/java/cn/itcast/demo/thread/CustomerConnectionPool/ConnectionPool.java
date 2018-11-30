package cn.itcast.demo.thread.CustomerConnectionPool;

import java.sql.Connection;
import java.util.LinkedList;

/**
 * 简单线程池实现
 * Created by fudingcheng on 2018-11-26.
 */
public class ConnectionPool {
    private LinkedList<Connection> pool = new LinkedList<Connection>();

    //初始化连接池
    public ConnectionPool(int initialSize){
        if(initialSize>0){
            for (int i = 0; i < initialSize; i++) {
                pool.add(ConnectionDriver.createConnection());
            }
        }
    }


    //非阻塞获得连接
    public Connection fetchConnection(long mills) throws InterruptedException {
        synchronized (pool){        //获得线程池对象锁
            if(mills<=0){           //超时时间<=0,线程则一直阻塞
                while(pool.isEmpty()){ //如果连接池的连接用完,就一直等待
                    pool.wait(0);   //线程阻塞
                }
                return pool.removeFirst();
            }else{                  //线程在指定时间内尝试获得连接
                long feture = System.currentTimeMillis()+mills; //终止时间
                long remaining= mills;  //记录剩余时间
                while (pool.isEmpty()&&remaining>=0){      //剩余时间内,如果连接池没有连接则一直等待
                    pool.wait(remaining);
                    remaining = feture-System.currentTimeMillis();  //更新剩余时间
                }
                Connection result = null;
                if(!pool.isEmpty()){        //连接池连接不为空,获得连接并返回
                    result = pool.removeFirst();
                }
                return result;
            }
        }
    }

    //释放连接
    public void releaseConnection(Connection connection){
        if(connection!=null){
           synchronized (pool){     //释放连接,通知等待的线程重新获取连接.
               pool.addLast(connection);
               pool.notifyAll();
           }
        }
    }
}
