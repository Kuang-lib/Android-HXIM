package cn.itcast.myim.controller.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.hyphenate.chat.EMClient;

import cn.itcast.myim.R;
import cn.itcast.myim.model.Model;
import cn.itcast.myim.model.bean.UserInfo;


public class WelcomeActivity extends AppCompatActivity {


    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            //如果当前activity已经退出，则不处理handler中的消息
            if(isFinishing()){
                return;
            }
            //判断进入主页面还是登录页面
            toMainOrLogin();
        }
    };

    //判断进入主页面还是登录页面
    private void toMainOrLogin(){

        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //判断当前账号是否登录过
                if(EMClient.getInstance().isLoggedInBefore()){ //登录过
                    //获取当前登录用户信息
                    UserInfo account = Model.getInstance().getUserAccountDao().getAccountByHxId(EMClient.getInstance().getCurrentUser());
                    //Log.i("login",account.toString());
                    if (account == null){
                        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }else{
                        // 登录成功后的方法
                        Model.getInstance().loginSuccess(account);
                        //跳转到主界面
                        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                }else { //没登录过
                    //跳转到登录界面
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // 发送2s的延时
        handler.sendMessageDelayed(Message.obtain(),2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁消息
        handler.removeCallbacksAndMessages(null);
    }
}
