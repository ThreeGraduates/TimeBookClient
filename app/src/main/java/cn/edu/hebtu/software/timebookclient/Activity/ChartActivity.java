package cn.edu.hebtu.software.timebookclient.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.edu.hebtu.software.timebookclient.R;
import cn.edu.hebtu.software.timebookclient.Service.TimedTaskService;
import cn.edu.hebtu.software.timebookclient.Service.PhoneTimeService;
import cn.edu.hebtu.software.timebookclient.fragment.AppFragment;
import cn.edu.hebtu.software.timebookclient.fragment.DataFragment;

public class ChartActivity extends AppCompatActivity {
    private AppFragment appFragment=new AppFragment();
    private DataFragment dataFragment=new DataFragment();
    private FragmentManager fragmentManager;
    private LinearLayout dataTab;
    private LinearLayout appTab;
    private Fragment currentFragment=new Fragment();

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        fragmentManager=getSupportFragmentManager();
        showFragment(dataFragment);
        dataTab=findViewById(R.id.tab1);
        appTab=findViewById(R.id.tab2);

        dataTab.setBackground(getResources().getDrawable(R.drawable.ll_border_shape));
        appTab.setBackgroundColor(Color.WHITE);
        dataTab.setOnClickListener(new TabClickListener());
        appTab.setOnClickListener(new TabClickListener());
        //返回上一级
        ImageView ivReturn=findViewById(R.id.chart_return);
        ivReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChartActivity.this,MainActivity.class));
                finish();
            }
        });
    }

    private void showFragment(Fragment fragment){
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        if (fragment!=currentFragment){
            transaction.hide(currentFragment);
            if(!fragment.isAdded()) {
                transaction.add(R.id.content, fragment);
            }
            transaction.show(fragment);
            transaction.commit();
            currentFragment=fragment;
        }
    }

    private class TabClickListener implements View.OnClickListener{
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tab1:
                    dataTab.setBackground(getResources().getDrawable(R.drawable.ll_border_shape));
                    appTab.setBackgroundColor(Color.WHITE);
                    showFragment(dataFragment);
                    break;
                case R.id.tab2:
                    appTab.setBackground(getResources().getDrawable(R.drawable.ll_border_shape));
                    dataTab.setBackgroundColor(Color.WHITE);
                    showFragment(appFragment);
                    break;
            }
        }
    }
}
