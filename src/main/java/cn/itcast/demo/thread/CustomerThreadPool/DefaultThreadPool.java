package cn.itcast.demo.thread.CustomerThreadPool;


import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by fudingcheng on 2018-12-02.
 */
public class DefaultThreadPool implements ThreadPool<Job>{
    //线程池最大的数量
    private static final int MAX_WORKER_NUMBERS=10;

    //线程池默认的数量
    private static final int DEAAULT_WORKER_NUMBERS=5;

    //线程池最小的数量
    private static final int MIN_WORKER_NUMBERS=1;

    //工作列表,将会向里面插入工作
    private final LinkedList<Job> jobs = new LinkedList<Job>();

    //工作者列表
    private final List<Worker> workers = Collections.synchronizedList(new ArrayList<Worker>());

    //工作者线程数
    private int workerNum = DEAAULT_WORKER_NUMBERS;
    
    //线程编号生成
    private AtomicLong threadNum = new AtomicLong();
    
    public DefaultThreadPool(){
        initializeWorkers(workerNum);
    }

    public DefaultThreadPool(int num){
        workerNum=num>MAX_WORKER_NUMBERS?MAX_WORKER_NUMBERS:num<MIN_WORKER_NUMBERS?MIN_WORKER_NUMBERS:num;
        initializeWorkers(workerNum);
    }



    //初始化工作者线程
    private void initializeWorkers(int num) {
        for (int i = 0; i < num; i++) {
            Worker worker = new Worker();
            workers.add(worker);
            Thread thread = new Thread(worker,"ThreadPool-Worker-"+threadNum);
            threadNum.incrementAndGet();
            thread.start();
        }
    }

    @Override
    public void execute(Job job) {
        if(job!=null){
            synchronized (jobs){
                //添加一个工作线程,然后进行通知
                jobs.addLast(job);
                jobs.notify();
            }
        }
    }

    @Override
    public void shutDown() {
        for (Worker worker:workers) {
            worker.shutDown();
        }
    }

    @Override
    public void addWorkers(int num) {
        synchronized (jobs){
            //限制新增的worker数量不能超过最大值
            if(num+this.workerNum>MAX_WORKER_NUMBERS){
                num = MAX_WORKER_NUMBERS-this.workerNum;
            }
            initializeWorkers(num);
            this.workerNum+=num;
        }
    }

    @Override
    public void removeWorker(int num) {
        synchronized (jobs){
            if(num>=this.workerNum){
                throw new IllegalArgumentException("beyond workerNum");
            }

            //按照给的数量停止worker
            int count = 0;
            while (count<num){
                Worker worker = workers.get(count);
                if(workers.remove(worker)){
                    worker.shutDown();
                    count++;
                }
                this.workerNum-=count;
            }
        }
    }

    @Override
    public int getJobSize() {
        return jobs.size();
    }

    //工作者,负责消费任务
    class Worker implements Runnable{

        //是否工作
        private volatile boolean running = true;

        @Override
        public void run() {
            while(running){
                Job job = null;
                synchronized (jobs){
                    if(jobs.isEmpty()){
                        try {
                            jobs.wait();
                        } catch (InterruptedException e) {
                            //感知到外部对Workers的中断操作,返回
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    
                    //取出一个job
                    job = jobs.removeFirst();
                }
                if(job!=null){
                    job.run();
                }
                
            }
        }
        
        public void shutDown(){
            running= false;
        }
    }
}
