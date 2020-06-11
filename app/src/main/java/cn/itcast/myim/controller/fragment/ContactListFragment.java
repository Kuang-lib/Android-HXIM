package cn.itcast.myim.controller.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.exceptions.HyphenateException;


import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import cn.itcast.myim.R;
import cn.itcast.myim.controller.activity.AddContactActivity;
import cn.itcast.myim.controller.activity.ChatActivity;
import cn.itcast.myim.controller.activity.GroupListActivity;
import cn.itcast.myim.controller.activity.InviteActivity;
import cn.itcast.myim.model.Model;
import cn.itcast.myim.model.bean.UserInfo;
import cn.itcast.myim.utils.Constant;
import cn.itcast.myim.utils.SpUtils;

public class ContactListFragment extends EaseContactListFragment {

    private ImageView iv_contact_red;
    private LinearLayout ll_contact_invite;
    private LocalBroadcastManager mLBM;

    //联系人邀请信息变化的广播
    private BroadcastReceiver ContactInviteChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //更新红点显示
            iv_contact_red.setVisibility(View.VISIBLE);
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

        }
    };
    //群邀请信息变化的广播
    private BroadcastReceiver GroupInviteChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            iv_contact_red.setVisibility(View.VISIBLE);
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);

        }
    };

    //联系人变化的广播
    private BroadcastReceiver ContactChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshContact();
        }
    };
    private String mHxid;
    private LinearLayout ll_contact_group;


    @Override
    protected void initView() {
        super.initView();

        //添加“+”
        titleBar.setRightImageResource(R.drawable.em_add);

        //添加头布局
        View headerViwe = View.inflate(getActivity(), R.layout.header_fragment_contact,null);
        listView.addHeaderView(headerViwe);

        //获取红点对象
        iv_contact_red = headerViwe.findViewById(R.id.iv_contact_red);

        //获取邀请信息条目的对象
        ll_contact_invite = headerViwe.findViewById(R.id.ll_contact_invite);
        //获取群组信息条目的对象
        ll_contact_group = headerViwe.findViewById(R.id.ll_contact_group);

        // 设置listview条目的点击事件
        setContactListItemClickListener(new EaseContactListItemClickListener() {
            @Override
            public void onListItemClicked(EaseUser user) {
                if (user == null) {
                    return;
                }
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername());// 传递参数
                startActivity(intent);
            }
        });



    }

    @Override
    protected void setUpView() {
        super.setUpView();

        //“+”的点击事件处理
        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddContactActivity.class);
                startActivity(intent);
            }
        });

        //初始化红点显示
        boolean isNewInvite = SpUtils.getInstance().getBoolean(SpUtils.IS_NEW_INVITE,false);
            //isNewInvite有新邀请为true，显示红点，否则不显示
        iv_contact_red.setVisibility(isNewInvite ? View.VISIBLE : View.GONE);

        //注册广播
        mLBM = LocalBroadcastManager.getInstance(getActivity());
        mLBM.registerReceiver(ContactInviteChangeReceiver,new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
        mLBM.registerReceiver(GroupInviteChangeReceiver,new IntentFilter(Constant.GROUP_INVITE_CHANGED));
        mLBM.registerReceiver(ContactChangeReceiver,new IntentFilter(Constant.CONTACT_CHANGED));


        //邀请信息条目对象的点击事件
        ll_contact_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //红点隐藏
                iv_contact_red.setVisibility(View.GONE);
                SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, false);
                //跳转页面
                Intent intent = new Intent(getActivity(), InviteActivity.class);
                startActivity(intent);
            }
        });
        //群组信息条目对象的点击事件
        ll_contact_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GroupListActivity.class);
                startActivity(intent);
            }
        });

        // 从环信服务器获取所有联系人信息
        getCOntactFromHX();

        //绑定listview和contextmenu
        registerForContextMenu(listView);


    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        //获取当前条目的环信id
        int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        EaseUser easeUser = (EaseUser) listView.getItemAtPosition(position);
        mHxid = easeUser.getUsername();
        //添加布局
        getActivity().getMenuInflater().inflate(R.menu.delete, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.contact_delete){
            //执行删除选中联系人操作
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {

                    try {
                        //到环信服务器中删除好友
                        EMClient.getInstance().contactManager().deleteContact(mHxid);
                        //本地数据库中删除
                        Model.getInstance().getDbManager().getContactTableDao().deleteContactByHxId(mHxid);
                        if(getActivity() == null){
                            return;
                        }
                        //刷新页面
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(),"删除"+mHxid+"成功",Toast.LENGTH_SHORT).show();
                                refreshContact();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        if(getActivity() == null){
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(),"删除"+mHxid+"失败",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            });
        }
        return super.onContextItemSelected(item);
    }

    // 从环信服务器获取所有联系人信息
    private void getCOntactFromHX() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //获取到所有的好友环信id
                    List<String> hxids = EMClient.getInstance().contactManager().getAllContactsFromServer();

                    if (hxids != null && hxids.size() >= 0){
                        //保存好友信息到本地数据库
                        List<UserInfo> contacts = new ArrayList<UserInfo>();
                        for (String hxid : hxids){      //将环信id转换为UserInfo
                            UserInfo userInfo = new UserInfo(hxid);
                            contacts.add(userInfo);
                        }
                        Model.getInstance().getDbManager().getContactTableDao().saveContacts(contacts,true);

                        if (getActivity() == null){
                            return;
                        }
                        //刷新页面
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshContact();
                            }
                        });
                    }

                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //刷新联系人列表
    private void refreshContact(){
        //从本地数据库中获取数据
        List<UserInfo> contacts = Model.getInstance().getDbManager().getContactTableDao().getContacts();
        if (contacts != null && contacts.size() >= 0){

            Map<String, EaseUser> contactsMap = new HashMap<>();
            for (UserInfo contact : contacts){
                EaseUser easeUser = new EaseUser(contact.getHxid());
                contactsMap.put(contact.getHxid(), easeUser);
            }
            setContactsMap(contactsMap);       //EaseContactListFragment中的方法，相当于适配器的功能

            refresh();   //EaseContactListFragment中的方法.刷新页面
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLBM.unregisterReceiver(ContactInviteChangeReceiver);
        mLBM.unregisterReceiver(GroupInviteChangeReceiver);
        mLBM.unregisterReceiver(ContactChangeReceiver);
    }
}
