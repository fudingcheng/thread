package cn.itcast.demo.thread;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;

/**
 * 管道输入/输出流
 * Created by fudingcheng on 2018-12-01.
 */
public class Piped {

    public static void main(String[] args) throws IOException {
        PipedWriter out = new PipedWriter();
        PipedReader in = new PipedReader();

        //将输出流和输入流对接
        out.connect(in);

        Thread printThread = new Thread(new Print(in),"printThread");
        printThread.start();

        int receive = 0;
        try {
            while ((receive=System.in.read())!=-1){
                out.write(receive);
            }
        } finally {
            out.close();
        }
    }


    static class Print implements Runnable{

        private PipedReader in;

        public Print(PipedReader in) {
            this.in = in;
        }

        @Override
        public void run() {
            int receive = 0;
            try {
                while ((receive=in.read())!=-1){
                    System.out.print((char)receive);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
