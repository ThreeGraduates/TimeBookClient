package cn.edu.hebtu.software.timebookclient.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;

import cn.edu.hebtu.software.timebookclient.Bean.User;
import cn.edu.hebtu.software.timebookclient.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PersonalCenterActivity extends AppCompatActivity {
    private Gson gson;
    private User user;
    private ImageView avatar;
    private ImageView returnImg;
    private TextView tvName;
    private TextView tvEmail;
    private LinearLayout llAvatar;
    private LinearLayout llName;
    private LinearLayout llPassword;
    private OkHttpClient okHttpClient;
    private String path;

    private final int TAKE_RESULT_CODE=8888;
    private final int ALBUM_RESULT_CODE=6666;
    private String imageFilePath;
    private UploadPhotoListener listener;
    private LinearLayout llRoot;
    private PopupWindow popupWindow;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.activity_personal_center);
        Intent intent=getIntent();
        user= (User) intent.getSerializableExtra("user");
        path=getResources().getString(R.string.sever_path);
        GsonBuilder builder=new GsonBuilder();
        gson=builder.serializeNulls().create();
        okHttpClient=new OkHttpClient();
        listener=new UploadPhotoListener();
        avatar=findViewById(R.id.avatar);
        returnImg=findViewById(R.id.returnImg);
        tvName=findViewById(R.id.tv_name);
        tvEmail=findViewById(R.id.tv_email);
        llAvatar=findViewById(R.id.ll_avatar);
        llName=findViewById(R.id.ll_name);
        llPassword=findViewById(R.id.ll_password);
        llRoot=findViewById(R.id.root);
        tvName.setText(user.getUsername());
        tvEmail.setText(user.getEmail());
        RequestOptions options = new RequestOptions().circleCrop();
        Glide.with(PersonalCenterActivity.this)
                .load(path+user.getImage()+"?key=" + Math.random())
                .dontAnimate()
                .apply(options)
                .into(avatar);
        //返回
        returnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(PersonalCenterActivity.this, SetupActivity.class);
                intent1.putExtra("currentUserId",user.getId());
                startActivity(intent1);
            }
        });

        //上传头像
        llAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //手机相册选择图片上传
                showUploadAvatarPopupWindow();
            }
        });
        //修改用户名
        llName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlterUsernamePopupWindow();
            }
        });
        //修改密码
        llPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlterPasswordPopupWindow();
            }
        });

        //退出登录
        TextView logout=findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
                SharedPreferences.Editor ed = sharedPreferences.edit();
                ed.putLong("userId",0);
                ed.commit();
                Intent intent=new Intent(PersonalCenterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }
    /**
     *  展示修改密码pop
     */
    private void showAlterPasswordPopupWindow(){
        popupWindow=new PopupWindow(this);
        popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        final View view=getLayoutInflater().inflate(R.layout.popupwindow_password,null);

        TextView deter=view.findViewById(R.id.tv_deter);
        deter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText currentPwd=view.findViewById(R.id.et_currentPwd);
                TextView curText=view.findViewById(R.id.tv_curText);
                EditText newPwd=view.findViewById(R.id.et_newPwd);
                TextView newText=view.findViewById(R.id.tv_newText);
                EditText deterPwd=view.findViewById(R.id.et_deterPwd);
                TextView deterText=view.findViewById(R.id.tv_deterText);
                currentPwd.setOnFocusChangeListener(new EditTextFocusListener(curText));
                newPwd.setOnFocusChangeListener(new EditTextFocusListener(newText));
                deterPwd.setOnFocusChangeListener(new EditTextFocusListener(deterText));
                int length=newPwd.getText().toString().length();
                if(currentPwd.getText().toString().length()==0){
                    curText.setText("当前密码不能为空");
                    newText.setText("");
                    deterText.setText("");
                }
                if(length==0){
                    newText.setText("新密码不能为空");
                    deterText.setText("");
                    curText.setText("");
                }
                if(deterPwd.getText().toString().length()==0){
                    deterText.setText("确认密码不能为空");
                    newText.setText("");
                    curText.setText("");
                }
                if(!user.getPassword().equals(currentPwd.getText().toString())){
                    curText.setText("当前密码输入错误");
                    newText.setText("");
                    deterText.setText("");
                }else if(length<6||length>18){
                    newText.setText("密码必须包含6~18位字符");
                    deterText.setText("");
                    curText.setText("");
                }else if(!deterPwd.getText().toString().equals(newPwd.getText().toString())){
                    deterText.setText("两次输入的密码不一致");
                    newText.setText("");
                    curText.setText("");
                }else{
                    user.setPassword(deterPwd.getText().toString());
                    AlterUserTask alterPasswordTask=new AlterUserTask("password");
                    alterPasswordTask.execute();
                    popupWindow.dismiss();
                    Toast.makeText(PersonalCenterActivity.this,"密码修改成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
        TextView cancel=view.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupWindow.setContentView(view);
        backgroundAlpha(0.9f);
        //添加pop窗口关闭事件
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
        popupWindow.showAtLocation(llRoot, Gravity.CENTER,0,0);
    }

    class EditTextFocusListener implements View.OnFocusChangeListener{
        private TextView textView;

        public EditTextFocusListener(TextView textView) {
            this.textView = textView;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                textView.setText("");
            }
        }
    }

    /**
     *  展示修改用户名pop
     */
    private void showAlterUsernamePopupWindow(){
        popupWindow=new PopupWindow(this);
        popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        View view=getLayoutInflater().inflate(R.layout.popupwindow_username,null);
        TextView deter=view.findViewById(R.id.tv_deter);
        final EditText editText=view.findViewById(R.id.et_username);
        deter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setUsername(editText.getText().toString());
                AlterUserTask alterUsernameTask=new AlterUserTask("username");
                alterUsernameTask.execute();
                popupWindow.dismiss();
            }
        });

        TextView cancel=view.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupWindow.setContentView(view);
        backgroundAlpha(0.9f);
        //添加pop窗口关闭事件
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
        popupWindow.showAtLocation(llRoot, Gravity.CENTER,0,0);
    }

    /**
     *  展示上传头像pop
     */
    private void showUploadAvatarPopupWindow(){
        popupWindow=new PopupWindow(this);
        popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        View view=getLayoutInflater().inflate(R.layout.popupwindow_avatar,null);
        Button button1=view.findViewById(R.id.btnTakePhoto);
        button1.setOnClickListener(listener);
        Button button2=view.findViewById(R.id.btnAlbum);
        button2.setOnClickListener(listener);
        Button button3=view.findViewById(R.id.btnCancel);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupWindow.setContentView(view);
        backgroundAlpha(0.9f);
        //添加pop窗口关闭事件
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
        popupWindow.showAtLocation(llRoot, Gravity.NO_GRAVITY,0,0);
    }

    /**
     * 设置添加屏幕的背景透明度
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    /**
     *  拍照上传监听器
     */
    class UploadPhotoListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnTakePhoto:  //拍照上传图片
                    //设置图片的保存路径,作为全局变量
                    imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+user.getId()+".jpg";
                    File temp = new File(imageFilePath);
                    Uri imageFileUri = Uri.fromFile(temp);
                    Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //告诉相机拍摄完毕输出图片到指定的Uri
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
                    startActivityForResult(intent,TAKE_RESULT_CODE);
                    break;
                case R.id.btnAlbum://手机相册选择图片上传
                    Intent albumIntent=new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(albumIntent,ALBUM_RESULT_CODE);
                    break;
            }
            popupWindow.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String imagePath="";
        if (requestCode==TAKE_RESULT_CODE&resultCode==RESULT_OK){
            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
            avatar.setImageBitmap(bitmap);
            imagePath=imageFilePath;

        }else if(requestCode==ALBUM_RESULT_CODE&resultCode==RESULT_OK){
            Uri selectedImg=data.getData();
            String[] filePathColumn={MediaStore.Images.Media.DATA};
            Cursor cursor=getContentResolver().query(selectedImg,filePathColumn,null,null,null);
            cursor.moveToFirst();
            int columnIndex=cursor.getColumnIndex(filePathColumn[0]);
            String path=cursor.getString(columnIndex);
            imagePath=path;
            cursor.close();
            Bitmap bitmap= BitmapFactory.decodeFile(path);
            avatar.setImageBitmap(bitmap);
        }
        UploadImageTask uploadImageTask=new UploadImageTask(path,imagePath);
        uploadImageTask.execute();
    }

    /**
     *  向服务器端传送图片
     */
    class UploadImageTask extends AsyncTask {
        private String path;
        private String imagePath;

        public UploadImageTask(String path, String imagePath) {
            this.path = path;
            this.imagePath = imagePath;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            MediaType mediaType=MediaType.parse("application/jpeg");//明确指定上传的图片为jpg格式
            RequestBody requestBody=RequestBody.create(mediaType,new File(imagePath));
            Request request=new Request.Builder()
                    .url(path+"user/uploadUserImage/"+user.getId())
                    .post(requestBody)
                    .build();
            Call call=okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i("111","上传图片失败");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.i("111",response.body().string());
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
        }
    }

    /**
     *  修改用户名
     */
    class AlterUserTask extends AsyncTask{
        private String flag; //username 或 password

        public AlterUserTask(String flag) {
            this.flag = flag;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            String jsonStr=gson.toJson(user);
            MediaType type=MediaType.parse("application/json;charset=utf-8");
            RequestBody body=RequestBody.create(type,jsonStr);
            Request request=new Request.Builder()
                    .url(path+"user/alterUser/"+flag)
                    .post(body)
                    .build();
            Call call=okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i("result","请求失败");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.i("result",response.body().string());
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            tvName.setText(user.getUsername());
        }
    }
}


