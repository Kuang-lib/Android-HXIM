package cn.itcast.myim.controller.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import cn.itcast.myim.R;
import cn.itcast.myim.model.Model;
import cn.itcast.myim.model.bean.UserInfo;

public class LoginActivity extends Activity {

    private EditText et_login_name;
    private EditText et_login_pwd;
    private Button btn_login_regist;
    private Button btn_login_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
    }

    private void initView(){
        et_login_name = (EditText) findViewById(R.id.et_login_name);
        et_login_pwd = (EditText)findViewById(R.id.et_login_pwd);
        btn_login_regist = (Button) findViewById(R.id.btn_login_regist);
        btn_login_login = (Button)findViewById(R.id.btn_login_login);

        btn_login_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        btn_login_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regist();
            }
        });

    }

    //登录
    private void login() {
        // 获取输入的用户名和密码
        String loginName = et_login_name.getText().toString();
        String loginPwd = et_login_pwd.getText().toString();
        //检验输入的用户名和密码
        if (TextUtils.isEmpty(loginName) || TextUtils.isEmpty(loginPwd)){
            Toast.makeText(LoginActivity.this,"用户名或密码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        //去环信服务器登录
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                EMClient.getInstance().login(loginName, loginPwd, new EMCallBack() {
                    //登录成功
                    @Override
                    public void onSuccess() {
                        //对模型层数据进行处理
                        Model.getInstance().loginSuccess(new UserInfo(loginName));
                        //保存用户账号信息到本地数据库
                        Model.getInstance().getUserAccountDao().addAccount(new UserInfo(loginName));
                        //更新页面显示
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                    //登录失败
                    @Override
                    public void onError(int i, String s) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this,"登录失败"+s,Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    //登录过程中的处理
                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
            }
        });

    }

    //注册
    private void regist() {
        // 获取输入的用户名和密码
        String registName = et_login_name.getText().toString();
        String registPwd = et_login_pwd.getText().toString();
        //检验输入的用户名和密码
        if (TextUtils.isEmpty(registName) || TextUtils.isEmpty(registPwd)){
            Toast.makeText(LoginActivity.this,"用户名或密码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        //去环信服务器注册账号
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(registName,registPwd);

                    //更新页面显示（不能在子线程做）
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (HyphenateException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this,"注册失败"+e.toString(),Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }
            }
        });
    }
}
