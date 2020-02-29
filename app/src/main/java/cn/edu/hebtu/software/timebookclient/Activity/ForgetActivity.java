package cn.edu.hebtu.software.timebookclient.Activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cn.edu.hebtu.software.timebookclient.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//忘记密码页面
public class ForgetActivity extends AppCompatActivity {

    private Button btnBack;
    private EditText etEmail;
    private Button btnReset;
    private String serverPath;
    private ForgetHandler forgetHandler = new ForgetHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        //获取布局控件
        findView();
        //获取服务器端地址
        serverPath = getResources().getString(R.string.server_path);

        //为返回按钮绑定事件监听器
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //为重置按钮绑定事件监听器
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strEmail = etEmail.getText().toString();
                //判断邮箱的格式是否正确
                if(!isEmail(strEmail)){//邮箱格式不正确时
                    Toast failToast = Toast.makeText(ForgetActivity.this,"邮箱格式不正确",Toast.LENGTH_SHORT);
                    failToast.setGravity(Gravity.TOP,0,400);
                    failToast.show();
                }else{
                    String email = etEmail.getText().toString();
                    final Request request = new Request.Builder()
                            .url(serverPath+"user/sendEmail?email="+email)
                            .build();
                    OkHttpClient forgetClient = new OkHttpClient();
                    Call forgetCall = forgetClient.newCall(request);
                    forgetCall.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Message failMessage = new Message();
                            failMessage.what = -1;
                            forgetHandler.sendMessage(failMessage);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            InputStream in = response.body().byteStream();
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                            String result = bufferedReader.readLine();
                            Log.e("ForgetActivity",result);
                            //根据结果判断
                            if("success".equals(result)){
                                Message successMsg = new Message();
                                successMsg.what = 0;
                                forgetHandler.sendMessage(successMsg);
                            }else if("noExist".equals(result)){
                                //当前邮箱未被注册过
                                Message noExistMsg = new Message();
                                noExistMsg.what = 1;
                                forgetHandler.sendMessage(noExistMsg);
                            }else{
                                //发送邮箱失败
                                Message errorMessage = new Message();
                                errorMessage.what = 2;
                                forgetHandler.sendMessage(errorMessage);
                            }
                        }
                    });
                }
            }
        });

    }

    //获取布局控件
    private void findView(){
        btnBack = findViewById(R.id.btn_back);
        etEmail = findViewById(R.id.email);
        btnReset = findViewById(R.id.btn_reset);
    }

    //判断邮箱的格式是否正确
    public static boolean isEmail(String strEmail) {
        String strPattern = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        if (TextUtils.isEmpty(strPattern)) {
            return false;
        } else {
            return strEmail.matches(strPattern);
        }
    }

    private class ForgetHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case -1:
                    //网络响应失败 弹出提示
                    Toast failToast = Toast.makeText(ForgetActivity.this,"网络响应时间过长",Toast.LENGTH_SHORT);
                    failToast.setGravity(Gravity.TOP,0,400);
                    failToast.show();
                    break;
                case 0:
                    //发送邮件成功跳转至登录页面
                    Intent intent = new Intent(ForgetActivity.this,LoginActivity.class);
                    startActivity(intent);
                    break;
                case 1:
                    //该邮箱未被注册过 弹出提示框
                    Toast noToast = Toast.makeText(ForgetActivity.this,"该邮箱未被注册",Toast.LENGTH_SHORT);
                    noToast.setGravity(Gravity.TOP,0,400);
                    noToast.show();
                    break;
                case 2:
                    //发送邮箱失败
                    Toast errorToast = Toast.makeText(ForgetActivity.this,"发送邮件失败",Toast.LENGTH_SHORT);
                    errorToast.setGravity(Gravity.TOP,0,400);
                    errorToast.show();
                    break;
            }
        }
    }
}
