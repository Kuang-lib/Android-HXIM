package cn.itcast.myim.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.itcast.myim.R;
import cn.itcast.myim.model.bean.InvationInfo;
import cn.itcast.myim.model.bean.UserInfo;

//邀请信息列表的适配器
public class InviteAdapter extends BaseAdapter {
    private Context mContext;
    private List<InvationInfo> mInvaitationInfos = new ArrayList<InvationInfo>();
    private OnInviteListener mOnInviteListener;


    public InviteAdapter(Context context, OnInviteListener onInviteListener) {
        mContext = context;
        mOnInviteListener = onInviteListener;
    }


    //刷新数据的方法
    public void refresh(List<InvationInfo> invationInfos){
        if (invationInfos != null && invationInfos.size() >= 0){
            mInvaitationInfos.clear();
            mInvaitationInfos.addAll(invationInfos);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mInvaitationInfos == null ? 0 : mInvaitationInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return mInvaitationInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // 1、获取或创建一个ViewHolder
        ViewHolder holder = null;
        if (view == null){
            holder = new ViewHolder();
            view = View.inflate(mContext, R.layout.item_invite,null);

            holder.name = view.findViewById(R.id.tv_invite_name);
            holder.reason = view.findViewById(R.id.tv_invite_reason);
            holder.accept = view.findViewById(R.id.btn_invite_accept);
            holder.reject = view.findViewById(R.id.btn_invite_reject);

            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }

        // 2、获取item数据
        InvationInfo invationInfo = mInvaitationInfos.get(i);

        // 3、显示当前item数据
        UserInfo user = invationInfo.getUser();
        if (user != null){    //联系人

            holder.name.setText(invationInfo.getUser().getName());
            //按钮先隐藏，根据邀请状态来显示
            holder.accept.setVisibility(View.GONE);
            holder.reject.setVisibility(View.GONE);
            //原因
            if (invationInfo.getStatus() == InvationInfo.InvitationStatus.NEW_INVITE){  //是新的邀请

                if (invationInfo.getReason() == null){
                    holder.reason.setText("加个好友呗！");
                }else{
                    holder.reason.setText(invationInfo.getReason());
                }
                holder.accept.setVisibility(View.VISIBLE);
                holder.reject.setVisibility(View.VISIBLE);

            }else if (invationInfo.getStatus() == InvationInfo.InvitationStatus.INVITE_ACCEPT){ //接受邀请
                if (invationInfo.getReason() == null){
                    holder.reason.setText("同意加好友！");
                }else{
                    holder.reason.setText(invationInfo.getReason());
                }
            }else if (invationInfo.getStatus() == InvationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER){//邀请被接受
                if (invationInfo.getReason() == null){
                    holder.reason.setText("好友邀请被接受！");
                }else{
                    holder.reason.setText(invationInfo.getReason());
                }
            }

            //接受按钮的处理
            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnInviteListener.OnAccept(invationInfo);
                }
            });
            //拒绝按钮的处理
            holder.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnInviteListener.OnReject(invationInfo);
                }
            });

        }else{    //群组
            //显示名称
            holder.name.setText(invationInfo.getGroup().getInvatePerson());

            holder.reject.setVisibility(View.GONE);
            holder.accept.setVisibility(View.GONE);
            //显示原因
            switch (invationInfo.getStatus()){
                // 您的群申请请已经被接受
                case GROUP_APPLICATION_ACCEPTED:
                    holder.reason.setText("您的群申请请已经被接受");
                    break;
                //  您的群邀请已经被接收
                case GROUP_INVITE_ACCEPTED:
                    holder.reason.setText("您的群邀请已经被接收");
                    break;

                // 你的群申请已经被拒绝
                case GROUP_APPLICATION_DECLINED:
                    holder.reason.setText("你的群申请已经被拒绝");
                    break;

                // 您的群邀请已经被拒绝
                case GROUP_INVITE_DECLINED:
                    holder.reason.setText("您的群邀请已经被拒绝");
                    break;

                // 您收到了群邀请
                case NEW_GROUP_INVITE:
                    //显示按钮
                    holder.accept.setVisibility(View.VISIBLE);
                    holder.reject.setVisibility(View.VISIBLE);

                    // 接受邀请
                    holder.accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onInviteAccept(invationInfo);
                        }
                    });

                    // 拒绝邀请
                    holder.reject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onInviteReject(invationInfo);
                        }
                    });

                    holder.reason.setText("您收到了群邀请");
                    break;

                // 您收到了群申请
                case NEW_GROUP_APPLICATION:
                    holder.accept.setVisibility(View.VISIBLE);
                    holder.reject.setVisibility(View.VISIBLE);

                    // 接受申请
                    holder.accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onApplicationAccept(invationInfo);
                        }
                    });

                    // 拒绝申请
                    holder.reject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onApplicationReject(invationInfo);
                        }
                    });

                    holder.reason.setText("您收到了群申请");
                    break;

                // 你接受了群邀请
                case GROUP_ACCEPT_INVITE:
                    holder.reason.setText("你接受了群邀请");
                    break;

                // 您批准了群申请
                case GROUP_ACCEPT_APPLICATION:
                    holder.reason.setText("您批准了群申请");
                    break;

                // 您拒绝了群邀请
                case GROUP_REJECT_INVITE:
                    holder.reason.setText("您拒绝了群邀请");
                    break;

                // 您拒绝了群申请
                case GROUP_REJECT_APPLICATION:
                    holder.reason.setText("您拒绝了群申请");
                    break;

            }

        }

        // 4、返回view
        return view;
    }

    private class ViewHolder{
        private TextView name;
        private TextView reason;
        private Button accept;
        private Button reject;
    }

    public interface OnInviteListener{
        // 联系人接受按钮点击事件
        void OnAccept(InvationInfo invationInfo);
        //联系人拒绝按钮点击事件
        void OnReject(InvationInfo invationInfo);

        // 接受邀请按钮处理
        void onInviteAccept(InvationInfo invationInfo);
        // 拒绝邀请按钮处理
        void onInviteReject(InvationInfo invationInfo);

        // 接受申请按钮处理
        void onApplicationAccept(InvationInfo invationInfo);
        // 拒绝申请按钮处理
        void onApplicationReject(InvationInfo invationInfo);

    }
}
