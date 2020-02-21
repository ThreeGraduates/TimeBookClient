package cn.edu.hebtu.software.timebookclient.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class RegisterActivity extends AppCompatActivity {

    private Button btnBack;
    private EditText etEmail,etPassword;
    private Button btnRegister;
    private User user;
    private String serverPath;
    private Handler registerHandler = new RegisterHandler();


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //获取布局控件
        findView();
        serverPath = getResources().getString(R.string.sever_path);
        user = new User();
        //为返回按钮绑定点击监听事件 结束当前页面
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //为注册按钮绑定监听事件
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strEmail = etEmail.getText().toString();
                String strPassword = etPassword.getText().toString();
                //判断邮箱和密码的格式是否正确 若不正确则弹出提示
                if(!isEmail(strEmail)||!isPassword(strPassword)){
                    Toast registerToast = Toast.makeText(RegisterActivity.this, "邮箱或密码格式错误", Toast.LENGTH_SHORT);
                    registerToast.setGravity(Gravity.TOP,0,400);
                    registerToast.show();
                }else{
                    user.setEmail(strEmail);
                    user.setPassword(strPassword);
                    //利用GSON将user对象转化为json数据
                    Gson userGson = new GsonBuilder().serializeNulls().create();
                    String userJsonStr = userGson.toJson(user);
                    //OkHttp异步请求
                    MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
                    RequestBody requestBody = RequestBody.create(mediaType,userJsonStr);
                    Request request = new Request.Builder()
                            .url(serverPath+"user/saveUser")
                            .post(requestBody)
                            .build();
                    OkHttpClient registerClient = new OkHttpClient();
                    Call registerCall = registerClient.newCall(request);
                    registerCall.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            //网络连接错误
                            Log.e("RegisterActivity","网络响应时间过长");
                            Message failMessage = new Message();
                            failMessage.what = -1;
                            registerHandler.sendMessage(failMessage);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            //读取响应信息
                            InputStream in = response.body().byteStream();
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                            String result = bufferedReader.readLine();
                            Log.e("RegisterActivity",result);
                            if("exist".equals(result)){
                                //注册的邮箱已存在时 弹出提示框
                                Message existMessage = new Message();
                                existMessage.what = 0;
                                registerHandler.sendMessage(existMessage);

                            }else if("error".equals(result)){
                                //注册失败
                                Message errorMessage = new Message();
                                errorMessage.what = 1;
                                registerHandler.sendMessage(errorMessage);
                            }else{
                                //注册成功
                                Long id = Long.valueOf(Integer.parseInt(result));
                                user.setId(id);
                                Message successMessage = new Message();
                                successMessage.what = 2;
                                registerHandler.sendMessage(successMessage);
                            }
                        }
                    });
                }

            }
        });


    }

    private void findView(){
        btnBack = findViewById(R.id.btn_back);
        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        btnRegister = findViewById(R.id.btn_register);
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
    
    //判断密码的长度是否在6~18位内
    public  static boolean isPassword(String strPassword){
        if(strPassword.length()>=6&&strPassword.length()<=18)
            return true;
        else
            return false;
    }

    //网络异步请求后 根据响应结果做出响应
    private class RegisterHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case -1:
                    //网络连接错误 响应时间过长 弹出提示框
                    Toast failToast = Toast.makeText(RegisterActivity.this,"网络响应时间过长",Toast.LENGTH_SHORT);
                    failToast.setGravity(Gravity.TOP,0,400);
                    failToast.show();
                    break;
                case 0:
                    //邮箱被注册后弹出提示框
                    Toast existToast = Toast.makeText(RegisterActivity.this,"该邮箱已被注册",Toast.LENGTH_SHORT);
                    existToast.setGravity(Gravity.TOP,0,400);
                    existToast.show();
                    break;
                case 1:
                    //注册失败后弹出提示框
                    Toast errorToast = Toast.makeText(RegisterActivity.this,"注册失败",Toast.LENGTH_SHORT);
                    errorToast.setGravity(Gravity.TOP,0,400);
                    errorToast.show();
                    break;
                case 2:
                    //注册成功 将userId存入sharedPreferences 并跳转到主页
                    SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putLong("userId",user.getId());
                    editor.commit();
                    Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }
}
