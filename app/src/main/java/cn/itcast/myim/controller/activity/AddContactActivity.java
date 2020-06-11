package cn.itcast.myim.controller.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import cn.itcast.myim.R;
import cn.itcast.myim.model.Model;
import cn.itcast.myim.model.bean.UserInfo;

//添加联系人页面
public class AddContactActivity extends Activity {
    private Button btn_add_find;
    private EditText et_add_name;
    private RelativeLayout rl_add;
    private TextView tv_add_name;
    private Button btn_add_add;
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        initView();
    }

    private void initView() {
        btn_add_find = findViewById(R.id.btn_add_find);
        btn_add_add =  findViewById(R.id.btn_add_add);
        et_add_name =  findViewById(R.id.et_add_name);
        rl_add = findViewById(R.id.rl_add);
        tv_add_name = findViewById(R.id.tv_add_name);


        //查找按钮点击事件监听
        btn_add_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                find();
            }
        });
        //添加按钮点击事件监听
        btn_add_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });

    }

    //查找好友
    private void find() {
        String name = et_add_name.getText().toString();
        if (TextUtils.isEmpty(name)){
            Toast.makeText(AddContactActivity.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        // 去服务器中查找是否存在
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                // 服务器中判断当前查找的用户是否存在
                //不能直接从环信服务器中判断用户是否存在吗？
                userInfo = new UserInfo(name);  //假设存在
                //更新页面（将查找信息显示）
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rl_add.setVisibility(View.VISIBLE);
                        tv_add_name.setText(name);
                    }
                });
            }
        });
    }
    //添加好友
    private void add() {
        // 去环信服务器添加好友
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().addContact(userInfo.getName(),"添加好友");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddContactActivity.this,"已发送好友请求",Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddContactActivity.this,"好友请求失败"+e.toString(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
    }

}
