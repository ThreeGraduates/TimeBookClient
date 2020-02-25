package cn.edu.hebtu.software.timebookclient.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import cn.edu.hebtu.software.timebookclient.Adapter.TaskListManagerAdapter;
import cn.edu.hebtu.software.timebookclient.Bean.TaskList;
import cn.edu.hebtu.software.timebookclient.R;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TaskListManagerActivity extends AppCompatActivity {
    private ListView listView;
    private TaskListManagerAdapter taskListManagerAdapter;
    private List<TaskList> taskLists;
    private int userId;
    private String path;
    private OkHttpClient okHttpClient;
    private Gson gson;
    private LinearLayout root;
    private PopupWindow popupWindow;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasklist_manager);
        path=getResources().getString(R.string.sever_path);
        okHttpClient=new OkHttpClient();
        GsonBuilder builder=new GsonBuilder();
        gson=builder.serializeNulls().create();
        root=findViewById(R.id.ll_root);
        ImageView ivReturn=findViewById(R.id.taskList_return);
        ivReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(TaskListManagerActivity.this, SetupActivity.class);;
                startActivity(intent1);
            }
        });

        sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        Long id = sharedPreferences.getLong("userId",0);
        userId = id.intValue();

        listView=findViewById(R.id.lv_list);

        GetTaskListsTask getTaskListsTask=new GetTaskListsTask();
        getTaskListsTask.execute();

    }


    /**
     *  根据用户Id,获取所有清单
     */
    class GetTaskListsTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            Request request=new Request.Builder()
                    .url(path+"taskList/getTaskListsTaskByUserId?userId="+userId)
                    .build();
            Call call=okHttpClient.newCall(request);
            try {
                Response response=call.execute();
                String jsonData=response.body().string();
                taskLists = gson.fromJson(jsonData, new TypeToken<List<TaskList>>(){}.getType());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            Log.e("taskLists",taskLists.toString());
            taskListManagerAdapter=new TaskListManagerAdapter(taskLists,TaskListManagerActivity.this,root);
            listView.setAdapter(taskListManagerAdapter);
        }
    }

    /**
     * 删除清单pop
     */
    public void showDeletePopupWindow(final Long listId,String title){
        popupWindow=new PopupWindow(this);
        popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        View view=getLayoutInflater().inflate(R.layout.popupwindow_deletetasklist,null);
        TextView tvTitle=view.findViewById(R.id.tv_title);
        tvTitle.setText("确认要删除“"+title+"”？");
        TextView deter=view.findViewById(R.id.tv_deter);
        deter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteTaskListById deleteTaskListById=new DeleteTaskListById(listId);
                deleteTaskListById.execute();
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
        setBackgroundAlpha(0.9f);
        //添加pop窗口关闭事件
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1f);
            }
        });
        popupWindow.showAtLocation(root, Gravity.CENTER,0,0);
    }

    /**
     *  根据用户Id,获取所有清单
     */
    class DeleteTaskListById extends AsyncTask {
        private Long listId;

        public DeleteTaskListById(Long id) {
            this.listId = id;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            Request request=new Request.Builder()
                    .url(path+"taskList/deleteTaskListById?id="+listId)
                    .build();
            Call call=okHttpClient.newCall(request);
            try {
                Response response=call.execute();
                String jsonData=response.body().string();
                Log.e("DeleteTaskListById",jsonData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            Log.e("DeleteTaskListById","onPostExecute");
            GetTaskListsTask getTaskListsTask=new GetTaskListsTask();
            getTaskListsTask.execute();
        }
    }

    /**
     * 设置添加屏幕的背景透明度
     */
    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }
}

