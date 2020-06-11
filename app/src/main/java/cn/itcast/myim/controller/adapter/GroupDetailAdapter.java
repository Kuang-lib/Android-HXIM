package cn.itcast.myim.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.itcast.myim.R;
import cn.itcast.myim.model.bean.UserInfo;

public class GroupDetailAdapter extends BaseAdapter {

    private Context mContext;
    private boolean mIsCanModify; //是否允许添加和删除群成员
    private List<UserInfo> mUsers = new ArrayList<UserInfo>();
    private boolean isDeleteModel; //是否删除模式：true：是

    private OnGroupDetailListener mOnGroupDetailListener;

    public GroupDetailAdapter(Context context, boolean isCanModify, OnGroupDetailListener onGroupDetailListener) {
        mContext = context;
        mIsCanModify = isCanModify;
        mOnGroupDetailListener = onGroupDetailListener;
    }

    //通过刷新方法获取数据
    public void refresh(List<UserInfo> users){
        if (users != null && users.size() >=0){
            mUsers.clear();
            //添加“+”“-”
            UserInfo add = new UserInfo("add");
            UserInfo delete = new UserInfo("delete");
            mUsers.add(delete);
            mUsers.add(0,add);
            //添加群成员
            mUsers.addAll(0,users);
        }
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return mUsers == null ? 0 : mUsers.size();
    }

    @Override
    public Object getItem(int i) {
        return mUsers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //创建或获取ViewHolder
        ViewHolder holder = null;
        if(view == null){
            holder = new ViewHolder();

            view = View.inflate(mContext, R.layout.item_groupdetail, null);
            holder.avater = view.findViewById(R.id.iv_groupdetail_avatar);
            holder.delete = view.findViewById(R.id.iv_groupdetail_delete);
            holder.name = view.findViewById(R.id.tv_groupdetail_name);

            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }

        //获取当前item数据
        UserInfo member = mUsers.get(i);

        //显示数据
        if (mIsCanModify){  //有修改权限

            //布局的处理
            if (i == getCount()-1){     //"-"处理
                if (isDeleteModel){     //删除模式
                    view.setVisibility(View.INVISIBLE);
                }else{
                    view.setVisibility(View.VISIBLE);
                    holder.avater.setImageResource(R.drawable.em_smiley_minus_btn_pressed);
                    holder.delete.setVisibility(View.GONE);
                    holder.name.setVisibility(View.INVISIBLE);
                }
            }else if (i == getCount()-2){   //"+"处理
                if (isDeleteModel){
                    view.setVisibility(View.INVISIBLE);
                }else{
                    view.setVisibility(View.VISIBLE);
                    holder.avater.setImageResource(R.drawable.em_smiley_add_btn_pressed);
                    holder.delete.setVisibility(View.GONE);
                    holder.name.setVisibility(View.INVISIBLE);
                }
            }else {     //群成员处理
                view.setVisibility(View.VISIBLE);
                holder.name.setVisibility(View.VISIBLE);
                holder.name.setText(member.getName());
                holder.avater.setImageResource(R.drawable.em_default_avatar);
                if (isDeleteModel){
                    holder.delete.setVisibility(View.VISIBLE);
                }else{
                    holder.delete.setVisibility(View.VISIBLE);
                }
            }

            //点击事件的处理
            if (i == getCount()-1){ //"-"
                holder.avater.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isDeleteModel){
                            //进入删除模式，刷新页面
                            isDeleteModel = true;
                            notifyDataSetChanged();
                        }
                    }
                });
            }else if (i == getCount()-2){//“+”
                holder.avater.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnGroupDetailListener.onAddMembers();
                    }
                });
            }else{
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnGroupDetailListener.onDeleteMember(member);
                    }
                });
            }

        }else{

            if (i == getCount()-1 || i == getCount()-2){    //"+""-"的位置
                view.setVisibility(View.GONE);
            }else {
                view.setVisibility(View.VISIBLE);

                holder.name.setText(member.getName());
                holder.avater.setImageResource(R.drawable.em_default_avatar);
                holder.delete.setVisibility(View.GONE);
            }
        }


        //返回view
        return view;
    }

    //获取当前的删除模式
    public boolean isDeleteModel() {
        return isDeleteModel;
    }

    //设置当前的删除模式
    public void setDeleteModel(boolean deleteModel) {
        isDeleteModel = deleteModel;
    }

    private class ViewHolder{
        private ImageView avater;
        private ImageView delete;
        private TextView name;
    }

    public interface OnGroupDetailListener{
        //添加群成员方法
        void onAddMembers();

        //删除群成员方法
        void onDeleteMember(UserInfo userInfo);
    }

}
