package hdc.com.sockettest;


import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2018/2/9.
 * url
 */

public class SocketServerManager {
    private static SocketServerManager socketServerManager;
    private ServerSocket serverSocket;
    private List<Socket> mClientList = new ArrayList<Socket>();
    private ExecutorService mExecutors = null; // 线程池对象
    private SocketServerManager(){

    }
    public static SocketServerManager getInstance(){
        if(socketServerManager==null){
           synchronized (SocketServerManager.class){
               if(socketServerManager==null){
                   socketServerManager=new SocketServerManager();
               }
           }
        }
        return socketServerManager;
    }

    public void startSocketServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                startServerSync();
            }
        }).start();
    }

    private void startServerSync(){
        try {
            Log.i("socket_hdc "," server start");
            serverSocket = new ServerSocket(9002);
            serverSocket.getInetAddress().getHostAddress();
            mExecutors = Executors.newCachedThreadPool(); // 创建线程池
            Socket client = null;
            /*
             * 用死循环等待多个客户端的连接，连接一个就启动一个线程进行管理
             */
            while (true) {
                client = serverSocket.accept();
                Log.i("socket_hdc "," server get socket");
                // 把客户端放入集合中
                mClientList.add(client);
                mExecutors.execute(new SocketHandle(client)); // 启动一个线程，用以守候从客户端发来的消息
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class SocketHandle implements Runnable {
        private Socket socket;
        private BufferedReader bufferedReader = null;
        private BufferedWriter bufferedWriter = null;
        private String message = "";

        public SocketHandle(Socket socket) {
            this.socket = socket;
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));// 获得输入流对象
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));// 创建输出流对象
                // 客户端只要一连到服务器，便发送连接成功的信息
                message = "服务器地址：" + this.socket.getInetAddress();
                this.sendMessage(message);
                message = "当前连接的客户端总数:" + mClientList.size();
                this.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void run() {
            try {
                while (true) {
                    if ((message = bufferedReader.readLine()) != null) {
                        if(message.equals("quit")){
                            closeSocket();
                            break;
                        }
                        // 接收客户端发过来的信息message，然后转发给客户端。
                        message = "服务器收到 " + ":" + message;
                        this.sendMessage(message);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void closeSocket() throws IOException {
            mClientList.remove(socket);
            bufferedReader.close();
            bufferedWriter.close();
            message = "主机:" + socket.getInetAddress() + "关闭连接\n目前在线:" + mClientList.size();
            socket.close();
            this.sendMessage(message);
        }


        public void sendMessage(String msg) {
            try {
                Log.i("socket_hdc"," server send msg "+msg);

                bufferedWriter.write(msg);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
