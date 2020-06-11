package cn.itcast.myim.controller.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.exceptions.HyphenateException;

import java.nio.charset.CharsetEncoder;

import cn.itcast.myim.R;
import cn.itcast.myim.model.Model;

public class NewGroupActivity extends AppCompatActivity {

    private EditText et_newgroup_name;
    private EditText et_newgroup_desc;
    private CheckBox cb_newgroup_public;
    private CheckBox cb_newgroup_invite;
    private Button btn_newgroup_create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        initView();
    }

    private void initView() {
        et_newgroup_name = findViewById(R.id.et_newgroup_name);
        et_newgroup_desc = findViewById(R.id.et_newgroup_desc);
        cb_newgroup_public = findViewById(R.id.cb_newgroup_public);
        cb_newgroup_invite = findViewById(R.id.cb_newgroup_invite);
        btn_newgroup_create = findViewById(R.id.btn_newgroup_create);

        // "创建群组"按钮点击事件
        btn_newgroup_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到选择联系人页面，需要返回参数
                Intent intent = new Intent(NewGroupActivity.this, PickContactActivity.class);
                startActivityForResult(intent, 1);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //成功获取联系人
        if(resultCode == RESULT_OK){
            //创建群
            createGroup(data.getStringArrayExtra("members"));
        }
    }

    //创建群
    private void createGroup(String[] members) {
        String groupName = et_newgroup_name.getText().toString();   //群名称
        String groupDecs = et_newgroup_desc.getText().toString();   //群描述
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {

                EMGroupOptions options = new EMGroupOptions();
                options.maxUsers = 100;     //群成员人数限制
                //根据是否公开和是否开放群邀请，来设置群类型style
                if (cb_newgroup_public.isChecked()){        //公开
                    if (cb_newgroup_invite.isChecked()){    //开放邀请
                        options.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
                    }else {
                        options.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;
                    }
                }else{
                    if (cb_newgroup_invite.isChecked()){
                        options.style = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
                    }else {
                        options.style = EMGroupManager.EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
                    }
                }
                try {
                    // 环信SDKcreateGroup参数：群名称，群描述，群成员，原因，参数设置
                    EMClient.getInstance().groupManager().createGroup(groupName,groupDecs,members,"申请加入群",options);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewGroupActivity.this, "创建群成功！", Toast.LENGTH_SHORT).show();
                            finish();   //结束当前页面
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewGroupActivity.this, "创建群失败！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
