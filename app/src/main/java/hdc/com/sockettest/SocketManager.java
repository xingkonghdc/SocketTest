package hdc.com.sockettest;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by Administrator on 2018/2/9.
 * url
 */

public class SocketManager {
    private static SocketManager socketManager;
    private String host="";
    private int port=9001;
    private Socket socket;
    private BufferedReader bufferedReader = null;
    private BufferedWriter bufferedWriter = null;
    private SocketManager(){

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
                    socket =new Socket(host,port);
                    bufferedReader =new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    while (true) {//死循环守护，监控服务器发来的消息
                        if (!socket.isClosed()) {//如果服务器没有关闭
                            if (socket.isConnected()) {//连接正常
                                if (!socket.isInputShutdown()) {//如果输入流没有断开
                                    String getLine;
                                    if ((getLine = bufferedReader.readLine()) != null) {//读取接收的信息
                                        getLine += "\n";
                                    } else {
                                        Message message=new Message();
                                        message.obj=getLine;
                                        message.what=MainActivity.SOCKET_MSG;
                                        handler.sendMessage(message);//通知UI更新
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

    public void sendMessage(String msg) {
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
