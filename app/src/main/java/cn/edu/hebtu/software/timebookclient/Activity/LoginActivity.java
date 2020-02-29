package cn.edu.hebtu.software.timebookclient.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cn.edu.hebtu.software.timebookclient.Bean.User;
import cn.edu.hebtu.software.timebookclient.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//登录页面
public class LoginActivity extends AppCompatActivity {

    private EditText etEmail,etPassword;
    private Button btnLogin,btnRegister;
    private TextView tvForget;
    private User user;
    private String serverPath;
    private Gson userGson;
    private LoginHandler loginHandler = new LoginHandler();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //获取布局文件
        FindView();

        //获取服务前端的地址
        serverPath = getResources().getString(R.string.server_path);

        //点击登录
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strEmail = etEmail.getText().toString();
                String strPassword = etPassword.getText().toString();
                //判断登录的密码和登录是否为空
                if("".equals(strEmail) ||"".equals(strPassword)){
                    //弹出提示框
                    Toast loginToast = Toast.makeText(LoginActivity.this,"邮箱或密码为空",Toast.LENGTH_SHORT);
                    loginToast.setGravity(Gravity.TOP,0,400);
                    loginToast.show();
                }
                else{
                    user = new User();
                    user.setEmail(strEmail);
                    user.setPassword(strPassword);
                    //利用GSON将user对象转化为json数据
                    userGson = new GsonBuilder().serializeNulls().create();
                    String userJsonStr = userGson.toJson(user);
                    //OkHttp异步请求 核验登录信息是否正确
                    MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
                    RequestBody requestBody = RequestBody.create(mediaType,userJsonStr);
                    final Request request = new Request.Builder()
                            .url(serverPath+"user/login")
                            .post(requestBody)
                            .build();
                    OkHttpClient loginClient = new OkHttpClient();
                    Call loginCall = loginClient.newCall(request);
                    loginCall.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            //访问失败
                            Log.e("LoginActivity","网络响应时间过长");
                            Message internetMessage = new Message();
                            internetMessage.what = -1;
                            loginHandler.sendMessage(internetMessage);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            //读取响应消息
                            InputStream in = response.body().byteStream();
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                            String result = bufferedReader.readLine();
                            if(result.equals("error")){
                                Log.e("LoginActivity",result);
                                Message errorMessage = new Message();
                                errorMessage.what = 1;
                                loginHandler.sendMessage(errorMessage);
                            }else {
                                //登录成功
                                Log.e("LoginActivity","登录成功");
                                //将返回的userId传回
                                Long userId = Long.valueOf(Integer.parseInt(result));
                                user.setId(userId);
                                Message successMessage = new Message();
                                successMessage.what = 0;
                                loginHandler.sendMessage(successMessage);

                            }
                        }
                    });
                }
            }
        });

        //点击忘记密码
        tvForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到忘记密码页面
                Intent forgetIntent = new Intent(LoginActivity.this,ForgetActivity.class);
                startActivity(forgetIntent);
            }
        });

        //点击注册按钮
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到注册页面
                Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

    }

    //获取布局控件
    public void FindView(){
        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        tvForget = findViewById(R.id.tv_forget);
    }


    //登录中异步连接时需要的响应
    private class LoginHandler extends Handler{

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case -1:
                    //表示网络连接出现问题
                    Toast internetToast = Toast.makeText(LoginActivity.this,"网络响应时间过长",Toast.LENGTH_SHORT);
                    internetToast.setGravity(Gravity.TOP,0,400);
                    internetToast.show();
                    break;
                case 0:
                    //表示登录成功 设置SharedPreference
                    SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putLong("userId",user.getId());
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    editor.commit();
                    break;
                case 1:
                    //表示登录失败，弹出提示框
                    Toast errorToast = Toast.makeText( LoginActivity.this, "邮箱或密码错误", Toast.LENGTH_SHORT);
                    errorToast.setGravity(Gravity.TOP,0,400);
                    errorToast.show();
                    break;
            }
        }
    }

}
