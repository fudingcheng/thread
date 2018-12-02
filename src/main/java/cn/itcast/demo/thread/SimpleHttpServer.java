package cn.itcast.demo.thread;

import cn.itcast.demo.thread.CustomerThreadPool.DefaultThreadPool;
import cn.itcast.demo.thread.CustomerThreadPool.Job;
import cn.itcast.demo.thread.CustomerThreadPool.ThreadPool;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 模拟Http服务器
 * Created by fudingcheng on 2018-12-02.
 */
public class SimpleHttpServer {
    static ThreadPool<Job> threadPool = new DefaultThreadPool(10);

    //设置跟路径
    static String baseDir;

    public static void setBaseDir(String baseDir) {
        if(baseDir!=null && new File(baseDir).exists() && new File(baseDir).isDirectory()){
            SimpleHttpServer.baseDir = baseDir;
        }
    }

    //设置端口号
    static int port = 8080;

    public static void setPort(int port) {
        SimpleHttpServer.port = port;
    }

    static ServerSocket serverSocket = null;
    
    //启动线程
    public static void start() throws IOException {
        serverSocket = new ServerSocket(port);
        Socket socket = null;
        while ((socket=serverSocket.accept())!=null){
            threadPool.execute(new HttpRequestHandler(socket));
        }
        serverSocket.close();
    }

    private static class HttpRequestHandler implements Job {
        private Socket socket;
        public HttpRequestHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            String line = null;
            BufferedReader br = null;
            BufferedReader reader = null;
            PrintWriter out = null;
            InputStream in = null;

            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String header = reader.readLine();

                if(header==null){
                    return;
                }

                //由相对路径计算出绝对路径
                String filePath = baseDir+header.split(" ")[1];

                //socket输出流
                out = new PrintWriter(socket.getOutputStream());
                //相应头


                //如果请求文件
                if(filePath.endsWith("jpg")){
                    in = new FileInputStream(filePath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int i = 0;
                    while((i=in.read())!=-1){
                        baos.write(i);
                    }
                    //响应内容
                    byte[] bytes = baos.toByteArray();

                    out.println("HTTP/1.1 200 OK");
                    out.println("Server: Molly");
                    out.println("Content-Type: image/jpeg");
                    out.println("Content-Length: "+bytes.length);
                    out.println("");

                    //输出内容
                    socket.getOutputStream().write(bytes,0,bytes.length);
                }else{  //文本内容
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
                    out.println("HTTP/1.1 200 OK");
                    out.println("Server: Molly");
                    out.println("Content-Type: text/html; charset=UTF-8");
                    out.println("");
                    while ((line=br.readLine())!=null){
                        out.print(line);
                    }
                }
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
                out.println("HTTP/1.1 500");
                out.println("");
                out.flush();
            }finally {
                close(br,in,reader,out,socket);
            }
        }

        //管理流
        private void close(Closeable...closeables) {
            if(closeables!=null){
                for (Closeable closeable:closeables){
                    try {
                        if(closeable!=null){
                            closeable.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        //设置服务器根目录
        SimpleHttpServer.setBaseDir("C:\\Users\\fudingcheng\\Desktop\\freemarker");

        //启动
        SimpleHttpServer.start();


        /**
         *通过Apache的ad进行压力测试,测试命令是:bin/ab -n 5000 -c 10  http://localhost:8080/test.html
         *
         *
         * 查看线程状态:
         *  1.通过jdk的jconsole查看线程状态
         *
         *  2.通过jps查看进行ID,再通过jstack [进程号]查看线程状态
         **/
    }
}
