package hdc.com.sockettest;


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
    private boolean isConnect=true;
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
            serverSocket = new ServerSocket(9001);
            mExecutors = Executors.newCachedThreadPool(); // 创建线程池
            System.out.println("服务器已启动，等待客户端连接...");
            Socket client = null;
            /*
             * 用死循环等待多个客户端的连接，连接一个就启动一个线程进行管理
             */
            while (true) {
                client = serverSocket.accept();
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
        private String message = "";

        public SocketHandle(Socket socket) {
            this.socket = socket;
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));// 获得输入流对象
                // 客户端只要一连到服务器，便发送连接成功的信息
                message = "服务器地址：" + this.socket.getInetAddress();
                this.sendMessage(message);
                message = "当前连接总数:" + mClientList.size();
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
                        message = socket.getInetAddress() + ":" + message;
                        this.sendMessage(message);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 关闭客户端
         *
         * @throws IOException
         */
        public void closeSocket() throws IOException {
            mClientList.remove(socket);
            bufferedReader.close();
            message = "主机:" + socket.getInetAddress() + "关闭连接\n目前在线:"
                    + mClientList.size();
            socket.close();
            this.sendMessage(message);
        }

        /**
         * 将接收的消息转发给每一个客户端
         *
         * @param msg
         */

        public void sendMessage(String msg) {
            BufferedWriter bufferedWriter = null;
            try {
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));// 创建输出流对象
                bufferedWriter.write(msg);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopScoketServer() {

    }
}
