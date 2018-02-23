package hdc.com.sockettest;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2018/2/9.
 * url
 */

public class SocketManager {
    private static SocketManager socketManager;
    private int port=9002;
    private Socket socket;
    private ExecutorService executorService;
    private BufferedReader bufferedReader = null;
    private BufferedWriter bufferedWriter = null;
    private SocketManager(){
        executorService= Executors.newCachedThreadPool();
    }
    public static SocketManager getInstance(){
        if(socketManager==null){
            synchronized (SocketManager.class){
                if(socketManager==null){
                    socketManager=new SocketManager();
                }
            }
        }
        return socketManager;
    }
    public void connectSocket(Context context, final Handler handler){
        Thread thread =new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i("socket_hdc","connect start");
                    socket =new Socket(DeviceInfoUtil.getIpAddress(),port);

                    Log.i("socket_hdc"," socket "+(socket==null));

                    bufferedReader =new BufferedReader(new InputStreamReader(socket.getInputStream()));// 创建输入流对象
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));// 创建输出流对象
                    while (true) {//死循环守护，监控服务器发来的消息
                        if (!socket.isClosed()) {//如果服务器没有关闭
                            if (socket.isConnected()) {//连接正常
                                if (!socket.isInputShutdown()) {//如果输入流没有断开
                                    String getLine;
                                    if ((getLine = bufferedReader.readLine()) != null) {//读取接收的信息
                                        getLine += "\n";
                                        Message message=new Message();
                                        message.obj=getLine;
                                        Log.i("socket_hdc",getLine);
                                        message.what=MainActivity.SOCKET_MSG;
                                        handler.sendMessage(message);//通知UI更新
                                    } else {

                                    }
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void sendMessage(final String msg) {
        if(executorService!=null&&socket!=null){
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.i("socket_hdc"," send msg "+msg);
                        bufferedWriter.write(msg);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void closeSocket() throws IOException {
        bufferedReader.close();
        bufferedWriter.close();
        if(socket!=null){
            socket.close();
        }
    }
}
