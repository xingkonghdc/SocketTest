package hdc.com.sockettest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static final int SOCKET_MSG=10;

    private Button socket_server_start_btn,socket_conn_btn,socket_disconnect_btn,socket_send_msg_btn;
    private static TextView socket_msg_tv;
    private EditText socket_send_msg_et;
    private String mIpAddress;

    @SuppressLint("HandlerLeak")
    public static Handler mHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SOCKET_MSG:
                    String s =socket_msg_tv.getText().toString()+"\n"+msg.obj;
                    socket_msg_tv.setText(s);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
        socket_server_start_btn =findViewById(R.id.socket_server_start_btn);
        socket_conn_btn =findViewById(R.id.socket_conn_btn);
        socket_disconnect_btn =findViewById(R.id.socket_disconnect_btn);
        socket_msg_tv =findViewById(R.id.socket_msg_tv);

        socket_send_msg_btn =findViewById(R.id.socket_send_msg_btn);
        socket_send_msg_et =findViewById(R.id.socket_send_msg_et);

        socket_server_start_btn.setOnClickListener(this);
        socket_conn_btn.setOnClickListener(this);
        socket_disconnect_btn.setOnClickListener(this);
        socket_send_msg_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.socket_server_start_btn://--启动服务器，即启动一个Service，创建SocketServer
                Intent intent =new Intent(this,SocketServerService.class);
                startService(intent);
                break;
            case R.id.socket_conn_btn://--创建间客户端Socket，建立连接
                SocketManager.getInstance().connectSocket(this,mHandler);
                break;
            case R.id.socket_disconnect_btn://--关闭连接，通过发送quit消息通知服务器关闭
                SocketManager.getInstance().sendMessage("quit");
                try {
                    SocketManager.getInstance().closeSocket();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.socket_send_msg_btn://--客户端向服务器发送消息
                String msg =socket_send_msg_et.getText().toString();
                Log.i("socket_hdc"," btn send "+msg);
                SocketManager.getInstance().sendMessage(msg);
                break;
        }
    }
}
