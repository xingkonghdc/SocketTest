package hdc.com.sockettest;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static final int SOCKET_MSG=10;

    private Button socket_server_start_btn,socket_conn_btn,socket_disconnect_btn,socket_send_msg_btn;
    private TextView socket_msg_tv;
    private EditText socket_send_msg_et;

    @SuppressLint("HandlerLeak")
    public static Handler mHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SOCKET_MSG:

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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.socket_server_start_btn:

                break;
            case R.id.socket_conn_btn:

                break;
            case R.id.socket_disconnect_btn:

                break;
            case R.id.socket_send_msg_btn:

                break;
        }
    }
}
