package cn.itcast.myim.controller.activity;


import android.app.Activity;
import android.os.Bundle;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import cn.itcast.myim.R;
import cn.itcast.myim.controller.fragment.ChatFragment;
import cn.itcast.myim.controller.fragment.ContactListFragment;
import cn.itcast.myim.controller.fragment.SettingFragment;

public class MainActivity extends FragmentActivity {

    private RadioGroup rg_main;
    private ChatFragment chatFragment;
    private ContactListFragment contactListFragment;
    private SettingFragment settingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initView();
    }

    private void initView() {
        rg_main = findViewById(R.id.rg_main);

        //创建三个fragment对象
        chatFragment = new ChatFragment();
        contactListFragment = new ContactListFragment();
        settingFragment = new SettingFragment();

        //监听RudioGroup选择事件
        rg_main.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Fragment fragment =null;
                switch (i){
                    // 会话列表页面
                    case R.id.rb_main_chat:
                        fragment = chatFragment;
                        break;

                    // 联系人列表页面
                    case R.id.rb_main_contact:
                        fragment = contactListFragment;
                        break;

                    // 设置页面
                    case R.id.rb_main_setting:
                        fragment = settingFragment;
                        break;
                }
                // 实现fragment切换
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fl_main,fragment).commit();
            }
        });
        // 默认选择会话列表页面
        rg_main.check(R.id.rb_main_chat);
    }
}
