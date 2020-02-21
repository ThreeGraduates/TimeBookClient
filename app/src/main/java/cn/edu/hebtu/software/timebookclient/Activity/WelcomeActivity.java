package cn.edu.hebtu.software.timebookclient.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import cn.edu.hebtu.software.timebookclient.R;

public class WelcomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_welcome,null);
        setContentView(rootView);


        //初始化渐变动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f,0.1f);
        alphaAnimation.setDuration(3000);
        alphaAnimation.setFillEnabled(true);
        alphaAnimation.setFillAfter(true);
        rootView.startAnimation(alphaAnimation);
        //设置动画监听器
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //当监听到动画结束时，判断之前是否登录过
                SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
                Long id = sharedPreferences.getLong("userId",0);
                Intent intent;
                if(id == 0)
                    //表明未登录过
                    intent = new Intent(WelcomeActivity.this,LoginActivity.class);
                else
                    //表明登录过
                    intent = new Intent(WelcomeActivity.this,MainActivity.class);
                startActivity(intent);
                WelcomeActivity.this.finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
}
