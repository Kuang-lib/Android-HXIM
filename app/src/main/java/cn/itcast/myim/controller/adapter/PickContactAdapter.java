package cn.itcast.myim.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.itcast.myim.R;
import cn.itcast.myim.model.bean.PickContactInfo;

//选择联系人页面适配器
public class PickContactAdapter extends BaseAdapter {

    private Context mContext;
    private List<PickContactInfo> mPickContacts = new ArrayList<PickContactInfo>() ;
    private List<String> mMembersExit = new ArrayList<>();       //保存群中已存在的成员

    public PickContactAdapter(Context context,List<PickContactInfo> pickContactInfos, List<String> membersExit) {
        mContext = context;
        if (pickContactInfos != null && pickContactInfos.size() >= 0){
            mPickContacts.clear();
            mPickContacts.addAll(pickContactInfos);
        }
        mMembersExit.clear();
        mMembersExit.addAll(membersExit);
    }

    @Override
    public int getCount() {
        return mPickContacts == null ? 0 : mPickContacts.size();
    }

    @Override
    public Object getItem(int i) {
        return mPickContacts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null){
            holder = new ViewHolder();
            view = View.inflate(mContext, R.layout.item_pick_contact,null);
            holder.checked = view.findViewById(R.id.cb_pick);
            holder.tv_name = view.findViewById(R.id.tv_pick_name);

            view.setTag(holder);

        }else{
            holder = (ViewHolder) view.getTag();
        }

        PickContactInfo pickContactInfo = mPickContacts.get(i);

        holder.tv_name.setText(pickContactInfo.getUserInfo().getName());
        holder.checked.setChecked(pickContactInfo.isChecked());

        //如果已经是群成员
        if (mMembersExit.contains((pickContactInfo.getUserInfo().getHxid()))){
            holder.checked.setChecked(true);
            pickContactInfo.setChecked(true);
        }

        return view;
    }

    //获取选择的联系人
    public List<String> getPickContacts(){
        List<String> picks = new ArrayList<>();
        for (PickContactInfo pickContactInfo: mPickContacts){
            if (pickContactInfo.isChecked()){   //判断是否选中
                picks.add(pickContactInfo.getUserInfo().getName());
            }
        }
        return picks;
    }

    private class ViewHolder{
        private TextView tv_name;
        private CheckBox checked;
    }
}
