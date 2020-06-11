package cn.itcast.myim.controller.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

import cn.itcast.myim.R;
import cn.itcast.myim.controller.adapter.PickContactAdapter;
import cn.itcast.myim.model.Model;
import cn.itcast.myim.model.bean.PickContactInfo;
import cn.itcast.myim.model.bean.UserInfo;
import cn.itcast.myim.utils.Constant;

//选择联系人页面
public class PickContactActivity extends Activity {

    private TextView tv_pick_save;
    private ListView lv_pickContacts;
    private List<PickContactInfo> mPicks;
    private PickContactAdapter pickContactAdapter;
    private List<String> membersExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contact);

        getData();

        initView();

        initListener();

    }

    private void getData(){
        Intent intent = getIntent();
        String groupId = intent.getStringExtra(Constant.GROUP_ID);
        if (groupId != null){
            EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
            //获取该群已存在的群成员
            membersExit = group.getMembers();
        }
        if (membersExit == null){
            membersExit = new ArrayList<>();
        }
    }

    private void initView() {
        tv_pick_save = findViewById(R.id.tv_pick_save);
        lv_pickContacts = findViewById(R.id.lv_pickContacts);

        //从本地数据库中获取所有联系人的信息
        List<UserInfo> contacts = Model.getInstance().getDbManager().getContactTableDao().getContacts();
        //将联系人信息转换为选择联系人信息：增加一个isChecked属性
        mPicks = new ArrayList<PickContactInfo>();
        if (contacts != null && contacts.size() >= 0){
            for (UserInfo contact:contacts){
                PickContactInfo pickContactInfo = new PickContactInfo(contact,false);
                mPicks.add(pickContactInfo);
            }
        }
        // 添加适配器
        pickContactAdapter = new PickContactAdapter(this, mPicks, membersExit);
        lv_pickContacts.setAdapter(pickContactAdapter);
    }

    private void initListener() {
        //listView 条目的点击事件监听
        lv_pickContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //checkbox的切换
                CheckBox cb_pick = view.findViewById(R.id.cb_pick);
                cb_pick.setChecked(!cb_pick.isChecked());

                //修改数据
                PickContactInfo pickContactInfo = mPicks.get(i);    //当前条目数据
                pickContactInfo.setChecked(cb_pick.isChecked());

                //刷新页面
                pickContactAdapter.notifyDataSetChanged();
            }
        });


        //保存按钮的点击事件
        tv_pick_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取到已经选择的联系人
                List<String> names = pickContactAdapter.getPickContacts();

                //给启动页面返回数据
                Intent intent = new Intent();
                intent.putExtra("members", names.toArray(new String[0]));
                setResult(RESULT_OK,intent);    //设置返回的结果码

                //结束当前页面
                finish();
            }
        });
    }
}
