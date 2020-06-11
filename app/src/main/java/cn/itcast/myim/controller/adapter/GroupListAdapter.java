package cn.itcast.myim.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMGroup;

import java.util.ArrayList;
import java.util.List;

import cn.itcast.myim.R;
import cn.itcast.myim.model.bean.GroupInfo;

public class GroupListAdapter extends BaseAdapter {
    private Context mContext;

    private List<EMGroup> emGroups = new ArrayList<EMGroup>();

    public GroupListAdapter(Context context) {
        this.mContext = context;
    }

    //刷新方法
    public void refresh(List<EMGroup> groups){
        if (groups != null && groups.size() >= 0){
            emGroups.clear();
            emGroups.addAll(groups);
            notifyDataSetChanged();
        }
    }


    @Override
    public int getCount() {
        return emGroups == null ? 0 : emGroups.size();
    }

    @Override
    public Object getItem(int i) {
        return emGroups.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //1、创建或获取ViewHolder
        ViewHolder holder = null;
        if (holder == null){
            holder = new ViewHolder();
            view = View.inflate(mContext,R.layout.item_grouplist,null);
            holder.name  = view.findViewById(R.id.tv_grouplist_name);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        //2、获取Item数据
        EMGroup emGroup = emGroups.get(i);

        //3、显示数据
        holder.name.setText(emGroup.getGroupName());

        //4、返回数据
        return view;
    }


    private class ViewHolder{
        private TextView name;
    }


}
