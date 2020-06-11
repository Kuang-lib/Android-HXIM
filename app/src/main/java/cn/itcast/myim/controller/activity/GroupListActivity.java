package cn.itcast.myim.controller.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import cn.itcast.myim.R;
import cn.itcast.myim.controller.adapter.GroupListAdapter;
import cn.itcast.myim.model.Model;

public class GroupListActivity extends AppCompatActivity {

    private ListView lv_grouplist;
    private GroupListAdapter groupListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        initView();
    }

    private void initView() {

        lv_grouplist = findViewById(R.id.lv_grouplist);
        //添加头布局
        View headView = View.inflate(this, R.layout.header_grouplist, null);
        lv_grouplist.addHeaderView(headView);
        //”添加群组“点击事件
        LinearLayout ll_grouplist = headView.findViewById(R.id.ll_grouplist);
        ll_grouplist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupListActivity.this, NewGroupActivity.class);
                startActivity(intent);
            }
        });

        //添加适配器
        groupListAdapter = new GroupListAdapter(this);
        lv_grouplist.setAdapter(groupListAdapter);
        //从环信服务器中获取所有群组信息
        getGroupsFromHX();

        //listView条目的点击事件
        lv_grouplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(GroupListActivity.this, ChatActivity.class);
                //群聊类型
                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
                //群号
                if (i == 0){        //i=0:头布局的条目
                    return;
                }
                EMGroup emGroup = EMClient.getInstance().groupManager().getAllGroups().get(i-1);
                intent.putExtra(EaseConstant.EXTRA_USER_ID, emGroup.getGroupId());

                startActivity(intent);
            }
        });

    }
    
    //从环信服务器中获取所有群组信息
    private void getGroupsFromHX() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //从环信服务器中加载到本地SDK
                    List<EMGroup> mGroups = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();

                    //更新页面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupListActivity.this,"加载群信息成功",Toast.LENGTH_SHORT).show();
                            //刷新数据：从本地SDK中获取
                            groupListAdapter.refresh(EMClient.getInstance().groupManager().getAllGroups());
                        }
                    });

                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupListActivity.this,"加载群信息失败 ",Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //刷新数据
        groupListAdapter.refresh(EMClient.getInstance().groupManager().getAllGroups());
    }
}
