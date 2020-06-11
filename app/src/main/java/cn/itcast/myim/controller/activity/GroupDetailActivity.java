package cn.itcast.myim.controller.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

import cn.itcast.myim.R;
import cn.itcast.myim.controller.adapter.GroupDetailAdapter;
import cn.itcast.myim.model.Model;
import cn.itcast.myim.model.bean.UserInfo;
import cn.itcast.myim.utils.Constant;


public class GroupDetailActivity extends Activity {
    private GridView gv_groupdetail;
    private Button btn_groupdetail_out;
    private EMGroup emGroup;
    private List<UserInfo> mUsers;
    private GroupDetailAdapter groupDetailAdapter;

    private GroupDetailAdapter.OnGroupDetailListener onGroupListener = new GroupDetailAdapter.OnGroupDetailListener() {
        //添加群成员
        @Override
        public void onAddMembers() {
            //跳转到联系人页面
            Intent intent = new Intent(GroupDetailActivity.this, PickContactActivity.class);
            intent.putExtra(Constant.GROUP_ID, emGroup.getGroupId());
            startActivityForResult(intent,2);
        }
        //删除群成员
        @Override
        public void onDeleteMember(UserInfo userInfo) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 在环信服务器中删除该群的成员
                        EMClient.getInstance().groupManager().removeUserFromGroup(emGroup.getGroupId(), userInfo.getHxid());
                        //更新页面
                        getMembersFromHxServer();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "删除群成员成功！",Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "删除群成员失败！" + e.toString(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_OK){
            //获取返回的准备邀请的群成员信息
            String[] members = data.getStringArrayExtra("members");
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 在环信服务器中，发送群成员邀请
                        EMClient.getInstance().groupManager().addUsersToGroup(emGroup.getGroupId(),members);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "发送邀请成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "发送邀请失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        getData();

        initView();

        initListener();

    }


    //获取前页面传递过来的群id
    private void getData(){
        Intent intent = getIntent();
        String groupId = intent.getStringExtra(Constant.GROUP_ID);
        if (groupId == null){
            return;
        }else{
            emGroup = EMClient.getInstance().groupManager().getGroup(groupId);
        }
    }

    private void initView() {
        gv_groupdetail = findViewById(R.id.gv_groupdetail);
        btn_groupdetail_out = findViewById(R.id.btn_groupdetail_out);

        //初始化gridview:添加适配器
        //是否允许修改取决于是否是群主或群是否公开
        boolean isCanModify = EMClient.getInstance().getCurrentUser().equals(emGroup.getOwner()) || emGroup.isPublic();
        groupDetailAdapter = new GroupDetailAdapter(this, isCanModify, onGroupListener);
        gv_groupdetail.setAdapter(groupDetailAdapter);

        //初始化button
        if (EMClient.getInstance().getCurrentUser().equals(emGroup.getOwner())) {     //判断当前用户是否是群主
            btn_groupdetail_out.setText("解散群");

            btn_groupdetail_out.setOnClickListener(new View.OnClickListener() { //点击事件
                @Override
                public void onClick(View view) {
                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // 通知环信服务器解散群
                                EMClient.getInstance().groupManager().destroyGroup(emGroup.getGroupId());

                                //发送退群的广播
                                exitGroupBroadCast();

                                //更新页面
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "解散群成功！", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "解散群失败！" + e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }
                    });
                }
            });
        }else{
            btn_groupdetail_out.setText("退群");
            btn_groupdetail_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // 通知环信服务器退群
                                EMClient.getInstance().groupManager().leaveGroup(emGroup.getGroupId());

                                //发送退群广播
                                exitGroupBroadCast();

                                //更新页面
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "退群成功！",Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "退群失败！" + e.toString(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }
                    });
                }
            });
        }

        //从环信服务器中获取所有群成员
        getMembersFromHxServer();
    }

    //添加gridview触摸监听
    private void initListener() {
        gv_groupdetail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        //判断当前是否是删除模式
                        if (groupDetailAdapter.isDeleteModel()){
                            groupDetailAdapter.setDeleteModel(false);   //切换为非删除模式
                            groupDetailAdapter.notifyDataSetChanged();  //刷新页面
                        }
                        break;
                }
                return false;
            }
        });
    }
    //发送解散群和退群广播
    private void exitGroupBroadCast() {
        LocalBroadcastManager mLBM = LocalBroadcastManager.getInstance(GroupDetailActivity.this);
        Intent intent = new Intent(Constant.EXIT_GROUP);
        intent.putExtra(Constant.GROUP_ID, emGroup.getGroupId());
        mLBM.sendBroadcast(intent);
    }

    //从环信服务器中获取所有群成员
    private void getMembersFromHxServer() {

        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //从环信服务器中获取群
                    EMGroup groupFromServer = EMClient.getInstance().groupManager().getGroupFromServer(emGroup.getGroupId());

                    List<String> members = groupFromServer.getMembers();
                    if (members != null && members.size() >= 0){
                        mUsers = new ArrayList<>();  //String 转换 User对象
                        for (String member: members){
                            UserInfo userInfo = new UserInfo(member);
                            mUsers.add(userInfo);
                        }
                    }

                    //更新页面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            groupDetailAdapter.refresh(mUsers);
                        }
                    });

                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupDetailActivity.this,"获取群信息失败"+ e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }


}
